package org.openbox.sf5.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.UserDto;
import org.openbox.sf5.model.Users;
import org.openbox.sf5.model.Usersauthorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Implementation of user creation.
 *
 * @author Andrii Duplyk
 *
 */
@Service
@Scope(value = "session")
public class UserService implements IUserService {

	@Autowired
	private DAO objectsController;

	// @Transactional // IJ031017: You cannot set autocommit during a managed
	// transaction
	@Override
	public Users registerNewUserAccount(UserDto accountDto) {

		if (userExists(accountDto.getUsername())) {
			throw new IllegalArgumentException("There is an account with that username: " + accountDto.getUsername());
		}

		Users newUser = new Users();
		newUser.setUsername(accountDto.getUsername());
		newUser.setPassword(accountDto.getPassword());
		List<Usersauthorities> listAuthorities = new ArrayList<>();

		Usersauthorities newLine = new Usersauthorities(accountDto.getUsername(), "ROLE_USER", newUser, 1);
		listAuthorities.add(newLine);
		newUser.setAuthorities(listAuthorities);
		newUser.setEnabled(true);
		objectsController.saveOrUpdate(newUser);

		return newUser;

	}

	private boolean userExists(String username) {

		Criterion criterion = Restrictions.eq("username", username);
		List<Users> rec = objectsController.findAllWithRestrictions(Users.class, criterion);

		return rec.isEmpty() ? false : true;

	}

	public DAO getObjectsController() {
		return objectsController;
	}

	public void setObjectsController(DAO objectsController) {
		this.objectsController = objectsController;
	}

}
