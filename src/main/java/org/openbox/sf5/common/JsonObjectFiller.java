package org.openbox.sf5.common;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.hibernate.collection.internal.PersistentList;
import org.openbox.sf5.model.AbstractDbEntity;

/**
 * This class is intended for static functions that convert DB objects into
 * JSON.
 *
 * @author Andrii Duplyk
 *
 */
public class JsonObjectFiller {

	private static <T, L> JsonObjectBuilder getJsonObjectBuilderFromClassInstance(T object)
			throws IllegalAccessException {
		Field[] fields;
		fields = object.getClass().getDeclaredFields();

		JsonObjectBuilder job = Json.createObjectBuilder();
		// use reflection
		// arrayOfTransponders.add(arg0)
		for (int i = 0; i < fields.length; i++) {

			String fieldName = fields[i].getName();
			if ("serialVersionUID".equals(fieldName)) {
				continue;
			}

			fields[i].setAccessible(true);

			// http://stackoverflow.com/questions/21120999/representing-null-in-json

			// here we need to check if a field is a PersistentList.
			if (fields[i].get(object) instanceof PersistentList) {
				// here we should serialize content of the list
				@SuppressWarnings("unchecked")
				List<L> persistentList = (List<L>) fields[i].get(object);

				job.add(fieldName, getJsonArray(persistentList));

			} else {
				if (fields[i].get(object) != null) {

					// checking if field is boolean
					if (fields[i].getType().equals(boolean.class)) {
						JsonValue strValue = ((boolean) fields[i].get(object)) ? JsonValue.TRUE : JsonValue.FALSE;
						job.add(fieldName, strValue);
					}

					// checking if it is Date class
					else if (fields[i].getType().equals(Timestamp.class)) {
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
						String strValue = formatter.format(fields[i].get(object));
						job.add(fieldName, strValue);
					}

					// writing as JsonNumber
					else if (fields[i].getType().equals(long.class)) {
						long strValue = (long) fields[i].get(object);
						job.add(fieldName, strValue);
					}

					// other classes
					else {
						String strValue = fields[i].get(object).toString();
						job.add(fieldName, strValue);
					}

				} else {
					JsonValue strValue = JsonValue.NULL;
					job.add(fieldName, strValue);
				}
			}

		} // end of loop

		return job;
	}

	/**
	 * This method returns class from the field name
	 *
	 * @param type
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AbstractDbEntity> Class<?> getFieldClass(Class<T> type, String fieldName) {
		Field[] fields;
		fields = type.getDeclaredFields();

		List<Field> fieldList = Arrays.asList(fields);
		Class<T> clazz = null;
		List<Class<T>> classList = new ArrayList<>();

		// find field with the given name and return its class
		fieldList.stream().filter(t -> t.getName().equals(fieldName))
				.forEach(t -> classList.add((Class<T>) t.getType()));

		if (classList.size() == 1) {
			clazz = classList.get(0);
		}

		return clazz;
	}

	private static <T> JsonArray getJsonArray(List<T> objList) {
		JsonArrayBuilder arrayOfObjects = Json.createArrayBuilder();
		objList.stream().forEach(t -> {

			try {
				JsonObjectBuilder trans = JsonObjectFiller.getJsonObjectBuilderFromClassInstance(t);
				arrayOfObjects.add(trans);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

		return arrayOfObjects.build();

	}

	public static SimpleDateFormat getJsonDateFormatter() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

		formatter.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

		return formatter;
	}

}
