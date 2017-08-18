package org.openbox.sf5.service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.openbox.sf5.common.JsonObjectFiller;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.AbstractDbEntity;
import org.openbox.sf5.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Helper class to work with generic criteria methods.
 *
 * @author Andrii Duplyk
 *
 */
@Service
public class CriterionService {

	@Autowired
	private DAO objectController;

	/**
     * Returns ready to use criterion
     *
     * @param type
     *            - class
     * @param fieldName
     *            fieldname
     * @param typeValue
     *            - value of field
     * @return
     */
	public <T extends AbstractDbEntity> Criterion getCriterionByClassFieldAndStringValue(Class<T> type,
			String fieldName, String typeValue) {
		Criterion criterion = null;

		// We have the following situation
		// 1. Field name is of primitive type. Then we use simple Criterion.
		// 2. Field is enum. Then it should be String representation of an enum.
		// 3. Field is String.
		// 4. Filed is entity class, retrieved from database. Then we select
		// object by id, that came as typeValue.

        Field field = JsonObjectFiller.getEntityField(type, fieldName);
        fieldName = Optional.ofNullable(field).map(Field::getName).orElse(fieldName);
        Class<?> fieldClazz = Optional.ofNullable(field).map(Field::getType).orElse(null);

		// check if this field has some class, not null
		if (fieldClazz == null) {
			// Return empty criterion
			return criterion;
		}

		else if (fieldClazz.isPrimitive()) {
			criterion = Restrictions.eq(fieldName, Long.parseLong(typeValue));
		}

		// check that it is an enum
		else if (Enum.class.isAssignableFrom(fieldClazz)) {
			// must select from HashMap where key is String representation of
			// enum

			// http://stackoverflow.com/questions/1626901/java-enums-list-enumerated-values-from-a-class-extends-enum
			List<?> enumList = enum2list((Class<? extends Enum>) fieldClazz);
			HashMap<String, Object> hm = new HashMap<>();
			enumList.stream().forEach(t -> hm.put(t.toString(), t));

			// now get enum value by string representation
			criterion = Restrictions.eq(fieldName, hm.get(typeValue));
		}

		else if (fieldClazz == String.class) {
			// we build rather primitive criterion
			criterion = Restrictions.eq(fieldName, typeValue);
		}

		else {
			// it is a usual class
			T filterObject = objectController.select((Class<T>) fieldClazz, Long.parseLong(typeValue));
			criterion = Restrictions.eq(fieldName, filterObject);

		}

		return criterion;
	}

	/**
     * Constructs user criterion
     * 
     * @param login
     * @param type
     * @return
     */
    public <T extends AbstractDbEntity> Criterion getUserCriterion(String login, Class<T> type) {
        SimpleExpression criterion = Restrictions.eq("username", login);
        return objectController.findAllWithRestrictions(Users.class, criterion).stream().findAny().map(Users::getId)
                .map(Long::new).map(Object::toString)
                .map(idStr -> getCriterionByClassFieldAndStringValue(type, "User", idStr)).orElse(criterion);
    }

	@SuppressWarnings("rawtypes")
	private static List<Enum> enum2list(Class<? extends Enum> cls) {
		return Arrays.asList(cls.getEnumConstants());
	}

	public DAO getObjectController() {
		return objectController;
	}

	public void setObjectController(DAO objectController) {
		this.objectController = objectController;
	}

}
