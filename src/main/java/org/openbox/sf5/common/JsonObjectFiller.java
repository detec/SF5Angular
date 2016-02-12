package org.openbox.sf5.common;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import org.hibernate.collection.internal.PersistentList;
import org.openbox.sf5.model.AbstractDbEntity;

// This class is intended for static functions that convert DB objects into JSON.
public class JsonObjectFiller {

	// seems to be the first correct implementation for hibernate mapping
	// projects using 1C mapping tool.
	public static <T, L> JsonObjectBuilder getJsonObjectBuilderFromClassInstance(T object)
			throws IllegalArgumentException, IllegalAccessException {
		Field fields[];
		fields = object.getClass().getDeclaredFields();

		JsonObjectBuilder JOB = Json.createObjectBuilder();
		// use reflection
		// arrayOfTransponders.add(arg0)
		for (int i = 0; i < fields.length; i++) {

			String fieldName = fields[i].getName();
			if (fieldName.equals("serialVersionUID")) {
				continue;
			}

			fields[i].setAccessible(true);

			// http://stackoverflow.com/questions/21120999/representing-null-in-json

			// here we need to check if a field is a PersistentList.
			if (fields[i].get(object) instanceof PersistentList) {
				// here we should serialize content of the list
				@SuppressWarnings("unchecked")
				List<L> persistentList = (List<L>) fields[i].get(object);

				// String strValue = getJsonFromObjectsList(persistentList);

				JOB.add(fieldName, getJsonArray(persistentList));

			} else {
				if (fields[i].get(object) != null) {

					// checking if field is boolean
					if (fields[i].getType().equals(boolean.class)) {
						JsonValue strValue = ((boolean) fields[i].get(object) == true) ? JsonValue.TRUE
								: JsonValue.FALSE;
						JOB.add(fieldName, strValue);
					}

					// checking if it is Date class
					else if (fields[i].getType().equals(Timestamp.class)) {
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
						String strValue = formatter.format(fields[i].get(object));
						JOB.add(fieldName, strValue);
					}

					// writing as JsonNumber
					else if (fields[i].getType().equals(long.class)) {
						long strValue = (long) fields[i].get(object);
						JOB.add(fieldName, strValue);
					}

					// other classes
					else {
						String strValue = fields[i].get(object).toString();
						JOB.add(fieldName, strValue);
					}

				} else {
					JsonValue strValue = JsonValue.NULL;
					JOB.add(fieldName, strValue);
				}
			}

		} // end of loop

		return JOB;
	}

	// This method returns class from the field name
	@SuppressWarnings("unchecked")
	public static <T extends AbstractDbEntity> Class<?> getFieldClass(Class<T> type, String fieldName) {
		Field fields[];
		fields = type.getDeclaredFields();

		List<Field> fieldList = Arrays.asList(fields);
		Class<T> clazz = null;
		List<Class<T>> classList = new ArrayList<Class<T>>();

		// find field with the given name and return its class
		fieldList.stream().filter(t -> t.getName().equals(fieldName)).forEach(t -> {
			classList.add((Class<T>) t.getType());
		});

		if (classList.size() == 1) {
			clazz = classList.get(0);
		}

		// o.getClass().getField("fieldName").getType().isPrimitive(); for
		// primitives
		return clazz;
	}

	public static <T> JsonArray getJsonArray(List<T> objList) {
		JsonArrayBuilder arrayOfObjects = Json.createArrayBuilder();
		objList.stream().forEach(t -> {

			try {
				JsonObjectBuilder trans = JsonObjectFiller.getJsonObjectBuilderFromClassInstance(t);
				arrayOfObjects.add(trans);
			} catch (Exception e) {
				e.printStackTrace();
			}

		});

		JsonArray JObject = arrayOfObjects.build();
		return JObject;
	}

	public static <T> String getJsonFromObjectsList(List<T> objList) {
		JsonArray JObject = getJsonArray(objList);

		// JSON pretty formatting - Taken from:
		// glassfish4\docs\javaee-tutorial\examples\web\jsonp\jsonpmodel\src\main\java\javaeetutorial
		// \jsonpmodel\ObjectModelBean.java
		// http://stackoverflow.com/questions/23007567/java-json-pretty-print-javax-json
		Map<String, Boolean> config = new HashMap<>();
		config.put(JsonGenerator.PRETTY_PRINTING, true);
		JsonWriterFactory factory = Json.createWriterFactory(config);

		// http://blog.eisele.net/2013/02/test-driving-java-api-for-processing.html
		StringWriter sw = new StringWriter();
		try (JsonWriter jw = factory.createWriter(sw)) {
			jw.writeArray(JObject);
		}

		return sw.toString();

	}

	// public static <T> ResponseEntity<GenericXMLListWrapper<T>>
	// returnGenericWrapperResponseBySatList(List<T> entityList,
	// Class<T> type) {
	//
	// GenericXMLListWrapper<T> wrapper = new GenericXMLListWrapper<T>();
	// wrapper.setWrappedList(entityList);
	//
	// //
	// http://stackoverflow.com/questions/14268981/modify-a-class-definitions-annotation-string-parameter-at-runtime
	// //
	// http://stackoverflow.com/questions/16545868/exception-converting-object-to-xml-using-jaxb
	//
	// // We should replace stub for satellites in root element
	// final XmlRootElement classAnnotation =
	// wrapper.getClass().getAnnotation(XmlRootElement.class);
	// ChangeAnnotation.changeAnnotationValue(classAnnotation, "name",
	// type.getSimpleName().toLowerCase()); // this
	// // seems
	// // to
	// // work
	//
	// // we should also change annotation of @XmlSeeAlso
	// final XmlSeeAlso classSeeAlsoAnnotation =
	// wrapper.getClass().getAnnotation(XmlSeeAlso.class);
	// // Player[] thePlayers = new Player[playerCount + 1];
	// Class[] clazzArray = new Class[1];
	// clazzArray[0] = type;
	// ChangeAnnotation.changeAnnotationValue(classSeeAlsoAnnotation, "value",
	// clazzArray);
	//
	// return new ResponseEntity<GenericXMLListWrapper<T>>(wrapper,
	// HttpStatus.OK);
	// }

	public static SimpleDateFormat getJsonDateFormatter() {
		// SimpleDateFormat formatter = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		// formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		formatter.setTimeZone(TimeZone.getTimeZone("Europe/Kiev"));

		return formatter;
	}

}
