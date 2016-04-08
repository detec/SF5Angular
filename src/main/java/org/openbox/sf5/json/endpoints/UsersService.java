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

@RestController
@EnableWebMvc
@RequestMapping(value = "${jaxrs.path}/users/")
public class UsersService {

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<Users>> getAllUsers() throws NotAuthenticatedException {
		Users currentUser = securityContext.getCurrentlyAuthenticatedUser();
		if (currentUser == null) {

			throw new NotAuthenticatedException("Couldn't get currently authenticated user!");
		}

		List<Users> listOfUsers = new ArrayList<Users>();
		listOfUsers = usersJsonizer.getAllUsers();
		return new ResponseEntity<List<Users>>(listOfUsers, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping(value = "{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<Users> deleteUser(@PathVariable("userId") long userId) {
		Users user = null;
		try {
			user = usersJsonizer.getUserById(userId);
		} catch (Exception e) {
			throw new IllegalStateException("Error getting user from database!", e);
		}

		if (user == null) {

			throw new IllegalArgumentException("No user found in database for id: " + userId);
		}

		if (user.getauthorities().contains("ROLE_ADMIN")) {
			throw new IllegalArgumentException("Cannot delete administrative user!");
		}

		try {
			usersJsonizer.removeUser(userId);
		} catch (Exception e) {
			throw new IllegalStateException("Error deleting user from database with id: " + userId);
		}

		return new ResponseEntity<Users>(HttpStatus.NO_CONTENT);

	}

	// https://docs.spring.io/spring-security/site/docs/3.0.x/reference/el-access.html
	// We allow only for admin and enabled user.
	// @PreAuthorize("hasRole('ROLE_ADMIN') or (#login == principal and
	// principal.enabled)")
	@PreAuthorize("hasRole('ROLE_ADMIN') or (#login  == authentication.name)")
	@RequestMapping(value = "filter/username/{login}", method = RequestMethod.GET)
	public ResponseEntity<Users> getUserByLogin(@PathVariable("login") String login) {
		Users retUser = null;
		try {
			retUser = usersJsonizer.getUserByLogin(login);
		} catch (Exception e) {
			throw new IllegalStateException("Error getting user from database!", e);
		}

		if (retUser == null) {
			// return new ResponseEntity<Users>(HttpStatus.NO_CONTENT);
			throw new IllegalArgumentException("No user found in database for login: " + login);
		}

		return new ResponseEntity<Users>(retUser, HttpStatus.OK);

	}

	// @PreAuthorize("hasRole('ROLE_ADMIN')") // if new user registers -
	// anonymous access is needed.
	@PreAuthorize("hasRole('ROLE_ADMIN')") // only admin can save changed users
											// with REST.
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Users> saveUser(@RequestBody Users user)
			throws IllegalArgumentException, IllegalStateException {
		// check if such user exists.
		// Boolean result =
		// usersJsonizer.checkIfUsernameExists(user.getusername());
		// if (result) {
		// // return new ResponseEntity<Long>(HttpStatus.ACCEPTED);
		// throw new IllegalArgumentException("Not created! Username already
		// exists.");
		// }

		// Check not to disable admin user.
		if (user.getenabled() == false
				&& user.getauthorities().stream().filter(t -> t.getAuthority().equals("ROLE_ADMIN")).count() > 0) {
			throw new IllegalStateException("It is not allowed to disable user with admin role!");
		}

		HttpStatus statusResult = null;

		try {
			statusResult = usersJsonizer.saveUser(user);
		}

		catch (Exception e) {
			throw new IllegalStateException("Error when saving user to database", e);
		}
		// if (statusResult.equals(HttpStatus.CONFLICT)) {
		// return new ResponseEntity<Long>(statusResult);
		// }

		HttpHeaders headers = new HttpHeaders();
		headers.add("UserId", Long.toString(user.getId()));
		// return new ResponseEntity<Long>(new Long(user.getId()), headers,
		// HttpStatus.CREATED);
		return new ResponseEntity<Users>(user, headers, statusResult);
	}

	@RequestMapping(value = "exists/username/{login}", method = RequestMethod.GET)
	public ResponseEntity<Boolean> ifSuchLoginExists(@PathVariable("login") String login) {
		Boolean result = usersJsonizer.checkIfUsernameExists(login);
		if (!result) {
			return new ResponseEntity<Boolean>(result, HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<Boolean>(result, HttpStatus.ACCEPTED);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@RequestMapping(value = "currentuser", method = RequestMethod.GET)
	public ResponseEntity<List<Users>> getCurrentlyAuthenticatedUser() throws NotAuthenticatedException {
		Users currentUser = securityContext.getCurrentlyAuthenticatedUser();

		if (currentUser == null) {

			// return new ResponseEntity<Settings>(HttpStatus.UNAUTHORIZED);
			throw new NotAuthenticatedException("Couldn't get currently authenticated user!");
		}

		List<Users> listOfUsers = new ArrayList<Users>();
		listOfUsers.add(currentUser);
		return new ResponseEntity<List<Users>>(listOfUsers, HttpStatus.OK);

	}

	public UsersJsonizer getUsersJsonizer() {
		return usersJsonizer;
	}

	public void setUsersJsonizer(UsersJsonizer usersJsonizer) {
		this.usersJsonizer = usersJsonizer;
	}

	@Autowired
	private UsersJsonizer usersJsonizer;

	@Autowired
	private SF5SecurityContext securityContext;

}
