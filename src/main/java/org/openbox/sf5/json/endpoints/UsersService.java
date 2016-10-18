package org.openbox.sf5.json.endpoints;

import java.util.ArrayList;
import java.util.List;

import org.openbox.sf5.common.SF5SecurityContext;
import org.openbox.sf5.json.exceptions.NotAuthenticatedException;
import org.openbox.sf5.json.service.UsersJsonizer;
import org.openbox.sf5.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Users controller
 *
 * @author Andrii Duplyk
 *
 */
@RestController
@EnableWebMvc
@RequestMapping(value = "${jaxrs.path}/users/")
public class UsersService {

	private static final String CONSTANT_COULDNT_GET_USER = "Couldn't get currently authenticated user!";

	@Autowired
	private UsersJsonizer usersJsonizer;

	@Autowired
	private SF5SecurityContext securityContext;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.GET)
	ResponseEntity<List<Users>> getAllUsers() throws NotAuthenticatedException {
		getVerifyAuthenticatedUser();

		List<Users> listOfUsers = usersJsonizer.getAllUsers();
		return new ResponseEntity<>(listOfUsers, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "{userId}", method = RequestMethod.DELETE)
	ResponseEntity<Users> deleteUser(@PathVariable("userId") long userId) {
		Users user = null;
		try {
			user = usersJsonizer.getUserById(userId);
		} catch (Exception e) {
			throw new IllegalStateException("Error getting user from database!", e);
		}

		if (user == null) {

			throw new IllegalArgumentException("No user found in database for id: " + userId);
		}

		if (isAdmin(user)) {
			throw new IllegalStateException("Cannot delete administrative user!");
		}

		try {
			usersJsonizer.removeUser(userId);
		} catch (Exception e) {
			throw new IllegalStateException("Error deleting user from database with id: " + userId);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}

	// https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html
	// We allow only for admin and enabled user.
	@PreAuthorize("hasRole('ROLE_ADMIN') or (#login  == authentication.name)")
	@RequestMapping(value = "filter/username/{login}", method = RequestMethod.GET)
	ResponseEntity<Users> getUserByLogin(@PathVariable("login") String login) {
		Users retUser = null;
		try {
			retUser = usersJsonizer.getUserByLogin(login);
		} catch (Exception e) {
			throw new IllegalStateException("Error getting user from database!", e);
		}

		if (retUser == null) {
			throw new IllegalArgumentException("No user found in database for login: " + login);
		}

		return new ResponseEntity<>(retUser, HttpStatus.OK);

	}

	// anonymous access is needed.
	@PreAuthorize("hasRole('ROLE_ADMIN')") // only admin can save changed users
											// with REST.
	@RequestMapping(method = RequestMethod.POST)
	ResponseEntity<Users> saveUser(@RequestBody Users user) {
		// check if such user exists.

		// Check not to disable admin user.
		if (!user.getEnabled() && isAdmin(user)) {
			throw new IllegalStateException("It is not allowed to disable user with admin role!");
		}

		HttpStatus statusResult = null;

		try {
			statusResult = usersJsonizer.saveUser(user);
		}

		catch (Exception e) {
			throw new IllegalStateException("Error when saving user to database", e);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("UserId", Long.toString(user.getId()));

		return new ResponseEntity<>(user, headers, statusResult);
	}

	private boolean isAdmin(Users user) {
		return user.getAuthorities().stream().filter(t -> "ROLE_ADMIN".equals(t.getAuthority())).count() > 0;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')") // only admin can save changed users
	// with REST.
	@RequestMapping(value = "{userId}", method = RequestMethod.PUT)
	ResponseEntity<Users> updateUser(@RequestBody Users user) {
		return saveUser(user);
	}

	@RequestMapping(value = "exists/username/{login}", method = RequestMethod.GET)
	ResponseEntity<Boolean> ifSuchLoginExists(@PathVariable("login") String login) {
		Boolean result = usersJsonizer.checkIfUsernameExists(login);
		if (!result) {
			return new ResponseEntity<>(result, HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "currentuser", method = RequestMethod.GET)
	ResponseEntity<List<Users>> getCurrentlyAuthenticatedUser() throws NotAuthenticatedException {
		Users currentUser = getVerifyAuthenticatedUser();

		List<Users> listOfUsers = new ArrayList<>();
		listOfUsers.add(currentUser);
		return new ResponseEntity<>(listOfUsers, HttpStatus.OK);

	}

	public UsersJsonizer getUsersJsonizer() {
		return usersJsonizer;
	}

	public void setUsersJsonizer(UsersJsonizer usersJsonizer) {
		this.usersJsonizer = usersJsonizer;
	}

	private Users getVerifyAuthenticatedUser() throws NotAuthenticatedException {
		Users currentUser = securityContext.getCurrentlyAuthenticatedUser();
		if (currentUser == null) {

			throw new NotAuthenticatedException(CONSTANT_COULDNT_GET_USER);
		}

		return currentUser;

	}

}
