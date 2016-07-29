package org.openbox.sf5.json.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.openbox.sf5.common.Intersections;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.SettingsConversion;
import org.openbox.sf5.model.Users;
import org.openbox.sf5.service.CriterionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsJsonizer {

	public Settings saveNewSetting(Settings setting, boolean calculateIntersection) throws SQLException {
		long id = setting.getId();
		// if we receive non-empty id
		// We use the same method for new and existing settings
		// if (id != 0) {
		// return HttpStatus.CONFLICT;
		// }

		// HttpStatus returnStatus = (id > 0) ? HttpStatus.OK :
		// HttpStatus.CREATED;

		// let's make a copy of conversion lines
		// List<SettingsConversion> originalLines = setting.getConversion();
		// List<SettingsConversion> copiedList = new
		// ArrayList<SettingsConversion>(originalLines);
		// originalLines.clear();
		// setting.setConversion(copiedList);

		// objectsController.saveOrUpdate(setting);
		// Let's try to use EntityManager method.
		if (setting.getId() > 0) {
			setting = objectsController.updateEM(setting);
		} else {
			setting = objectsController.saveEM(setting);
		}

		if (calculateIntersection) {
			// Intersections intersections = new Intersections();
			// intersections.setSessionFactory(sessionFactory);
			List<SettingsConversion> scList = setting.getConversion();
			try {
				int rows = intersections.checkIntersection(scList, setting);
			} catch (SQLException se) {
				throw new SQLException("Error when calculating intersection lines", se);
			}

			setting.setConversion(scList);

			setting = objectsController.updateEM(setting);

		}

		return setting;
	}

	@SuppressWarnings("unchecked")
	public List<Settings> getSettingsByArbitraryFilter(String fieldName, String typeValue, Users user) {
		List<Settings> settList = new ArrayList<>();

		Criterion userCriterion = Restrictions.eq("user", user);

		// building arbitrary criterion
		Criterion arbitraryCriterion = criterionService.getCriterionByClassFieldAndStringValue(Settings.class,
				fieldName, typeValue);

		if (arbitraryCriterion == null) {
			return settList;
		}

		Session session = objectsController.openSession();
		Criteria criteria = session.createCriteria(Settings.class).add(userCriterion).add(arbitraryCriterion);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		settList = criteria.list();

		session.close();

		return settList;

	}

	public List<Settings> getSettingsByUser(Users user) {
		List<Settings> settList = new ArrayList<>();

		Criterion userCriterion = Restrictions.eq("user", user);
		settList = objectsController.findAllWithRestrictions(Settings.class, userCriterion);

		return settList;

	}

	public List<Settings> getSettingsByUserLogin(String login) {
		List<Settings> settList = new ArrayList<>();

		Criterion userCriterion = criterionService.getUserCriterion(login, Settings.class);
		if (userCriterion == null) {
			return settList;
		}

		settList = objectsController.findAllWithRestrictions(Settings.class, userCriterion);

		return settList;
	}

	public Settings getSettingById(long settingId, Users user) {
		Settings setting = null;

		Criterion userCriterion = Restrictions.eq("user", user);

		Criterion settingIdCriterion = Restrictions.eq("id", settingId);

		Session session = objectsController.openSession();
		@SuppressWarnings("unchecked")
		List<Settings> records = session.createCriteria(Settings.class).add(userCriterion).add(settingIdCriterion)
				.list();

		if (records.size() == 0) {
			// There is no such setting with username
			return setting;
		} else {
			setting = records.get(0);
		}
		session.close();

		return setting;
	}

	public void deleteSetting(long id) {
		objectsController.remove(Settings.class, id);
	}

	public void deleteSettingLine(long id) {
		objectsController.remove(SettingsConversion.class, id);
	}

	@Autowired
	private CriterionService criterionService;

	// @Autowired
	// private SessionFactory sessionFactory;

	@Autowired
	private DAO objectsController;

	@Autowired
	private Intersections intersections;

	public DAO getObjectsController() {
		return objectsController;
	}

	public void setObjectsController(DAO objectsController) {
		this.objectsController = objectsController;
	}

	public CriterionService getCriterionService() {
		return criterionService;
	}

	public void setCriterionService(CriterionService criterionService) {
		this.criterionService = criterionService;
	}

	// public SessionFactory getSessionFactory() {
	// return sessionFactory;
	// }
	//
	// public void setSessionFactory(SessionFactory sessionFactory) {
	// this.sessionFactory = sessionFactory;
	// }

}
