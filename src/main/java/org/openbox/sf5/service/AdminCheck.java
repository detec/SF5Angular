package org.openbox.sf5.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.model.Users;
import org.openbox.sf5.model.Usersauthorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component // NFO: Overriding bean definition for bean
public class AdminCheck {

	@Autowired
	private DAO objectsController;

	private boolean adminHasBeenChanged = false;

	@PostConstruct
	public void initialize() {

		Criterion criterea = Restrictions.eq("username", "admin");
		List<Users> adminsList = objectsController.findAllWithRestrictions(Users.class, criterea);

		if (adminsList.isEmpty()) {
			List<Usersauthorities> rolesList = new ArrayList<>();

			Users admin = new Users("admin", "1", true, rolesList);

			fillTables(admin, rolesList);
			objectsController.saveOrUpdate(admin);
		}

		else {

			Users adminUser = adminsList.get(0);
			List<Usersauthorities> rolesList = adminUser.getauthorities();

			fillTables(adminUser, rolesList);

			if (adminHasBeenChanged == true) {
				objectsController.saveOrUpdate(adminUser);
			}

		}

	}

	public void fillTables(Users adminUser, List<Usersauthorities> rolesList) {
		List<String> textRoles = new ArrayList<>();
		textRoles.add("ROLE_ADMIN");
		textRoles.add("ROLE_USER");

		// ROLE_ADMIN
		Usersauthorities checkRoleAdmin = new Usersauthorities(adminUser.getusername(), "ROLE_ADMIN", adminUser, 1);

		if (!rolesList.contains(checkRoleAdmin)) {
			adminHasBeenChanged = true;
			rolesList.add(checkRoleAdmin);
		}

		// ROLE_USER
		Usersauthorities checkRoleUser = new Usersauthorities(adminUser.getusername(), "ROLE_USER", adminUser, 2);

		if (!rolesList.contains(checkRoleUser)) {
			adminHasBeenChanged = true;
			rolesList.add(checkRoleUser);
		}

	}

}
