package org.openbox.sf5.json.service;

import java.sql.SQLException;
import java.util.Collections;
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

/**
 * Settings helper service class.
 *
 * @author Andrii Duplyk
 *
 */
@Service
public class SettingsJsonizer {

	@Autowired
	private CriterionService criterionService;

	@Autowired
	private DAO objectsController;

	@Autowired
	private Intersections intersections;

	/**
	 *
	 * @param setting
	 * @param calculateIntersection
	 * @return
	 * @throws SQLException
	 */
	public Settings saveNewSetting(Settings setting, boolean calculateIntersection) throws SQLException {

		// Let's try to use EntityManager method.
		if (setting.getId() > 0) {
			setting = objectsController.updateEM(setting);
		} else {
			setting = objectsController.saveEM(setting);
		}

		if (calculateIntersection) {

			List<SettingsConversion> scList = setting.getConversion();
			try {
				intersections.checkIntersection(scList, setting);
			} catch (SQLException se) {
				throw new SQLException("Error when calculating intersection lines", se);
			}

			setting.setConversion(scList);

			setting = objectsController.updateEM(setting);

		}

		return setting;
	}

	/**
	 *
	 * @param fieldName
	 * @param typeValue
	 * @param user
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Settings> getSettingsByArbitraryFilter(String fieldName, String typeValue, Users user) {
		List<Settings> settList;

		Criterion userCriterion = Restrictions.eq("user", user);

		// building arbitrary criterion
		Criterion arbitraryCriterion = criterionService.getCriterionByClassFieldAndStringValue(Settings.class,
				fieldName, typeValue);

		if (arbitraryCriterion == null) {
			return Collections.emptyList();
		}

		Session session = objectsController.openSession();
		Criteria criteria = session.createCriteria(Settings.class).add(userCriterion).add(arbitraryCriterion);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

		settList = criteria.list();

		session.close();

		return settList;

	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public List<Settings> getSettingsByUser(Users user) {
		Criterion userCriterion = Restrictions.eq("user", user);
		return objectsController.findAllWithRestrictions(Settings.class, userCriterion);

	}

	/**
	 *
	 * @param login
	 * @return
	 */
	public List<Settings> getSettingsByUserLogin(String login) {
		Criterion userCriterion = criterionService.getUserCriterion(login, Settings.class);
		if (userCriterion == null) {
			return Collections.emptyList();
		}

		return objectsController.findAllWithRestrictions(Settings.class, userCriterion);

	}

	/**
	 *
	 * @param settingId
	 * @param user
	 * @return
	 */
	public Settings getSettingById(long settingId, Users user) {
		Settings setting = null;

		Criterion userCriterion = Restrictions.eq("user", user);

		Criterion settingIdCriterion = Restrictions.eq("id", settingId);

		Session session = objectsController.openSession();
		@SuppressWarnings("unchecked")
		List<Settings> records = session.createCriteria(Settings.class).add(userCriterion).add(settingIdCriterion)
				.list();

		if (records.isEmpty()) {
			// There is no such setting with username
			return setting;
		} else {
			setting = records.get(0);
		}
		session.close();

		return setting;
	}

	/**
	 *
	 * @param id
	 */
	public void deleteSetting(long id) {
		objectsController.remove(Settings.class, id);
	}

	/**
	 *
	 * @param id
	 */
	public void deleteSettingLine(long id) {
		objectsController.remove(SettingsConversion.class, id);
	}

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

}
