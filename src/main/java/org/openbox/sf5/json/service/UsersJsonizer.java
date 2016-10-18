package org.openbox.sf5.json.service;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.Settings;
import org.openbox.sf5.model.Users;
import org.openbox.sf5.service.CriterionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Helper service class for users.
 *
 * @author Andrii Duplyk
 *
 */
@Service
public class UsersJsonizer {

	@Autowired
	private CriterionService criterionService;

	@Autowired
	private DAO objectsController;

	@Autowired
	private SettingsJsonizer settingsJsonizer;

	/**
	 *
	 * @param userId
	 * @return
	 */
	public Users getUserById(long userId) {
		return objectsController.select(Users.class, userId);
	}

	/**
	 *
	 * @param userId
	 */
	public void removeUser(long userId) {
		// First we need to select all user settings and remove them
		Users user = getUserById(userId);
		List<Settings> userSettings = settingsJsonizer.getSettingsByUser(user);
		userSettings.stream().forEach(t -> objectsController.remove(Settings.class, t.getId()));

		objectsController.remove(Users.class, userId);
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public HttpStatus saveUser(Users user) {
		long id = user.getId();
		HttpStatus statusResult = (id != 0) ? HttpStatus.OK : HttpStatus.CREATED;

		objectsController.saveOrUpdate(user);
		return statusResult;

	}

	/**
	 *
	 * @param typeValue
	 * @return
	 */
	public Users getUserByLogin(String typeValue) {
		Users returnUser = null;
		Criterion criterion = criterionService.getCriterionByClassFieldAndStringValue(Users.class, "username",
				typeValue);

		if (criterion == null) {
			return returnUser;
		}

		List<Users> userList = objectsController.findAllWithRestrictions(Users.class, criterion);
		if (userList.isEmpty()) {
			return returnUser;
		}
		return userList.get(0);

	}

	/**
	 *
	 * @return
	 */
	public List<Users> getAllUsers() {
		return objectsController.findAll(Users.class);

	}

	/**
	 * returns false if there is no such user. Otherwise - false.
	 *
	 * @param typeValue
	 * @return
	 */
	public Boolean checkIfUsernameExists(String typeValue) {
		Boolean result = false;
		Criterion criterion = criterionService.getCriterionByClassFieldAndStringValue(Users.class, "username",
				typeValue);

		if (criterion == null) {
			return result;
		}
		List<Users> userList = objectsController.findAllWithRestrictions(Users.class, criterion);

		return (userList.isEmpty()) ? false : true;

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
