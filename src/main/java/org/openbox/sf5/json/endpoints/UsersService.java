package org.openbox.sf5.json.endpoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.openbox.sf5.common.SF5SecurityContext;
import org.openbox.sf5.json.exceptions.NotAuthenticatedException;
import org.openbox.sf5.json.service.UsersJsonizer;
import org.openbox.sf5.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping(value = "${jaxrs.path}/users/", produces = MediaType.APPLICATION_JSON)
public class UsersService {

	private static final String CONSTANT_COULDNT_GET_USER = "Couldn't get currently authenticated user!";

	@Autowired
	private UsersJsonizer usersJsonizer;

	@Autowired
	private SF5SecurityContext securityContext;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
	ResponseEntity<List<Users>> getAllUsers() throws NotAuthenticatedException {
		getVerifyAuthenticatedUser();

		List<Users> listOfUsers = usersJsonizer.getAllUsers();
		return new ResponseEntity<>(listOfUsers, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "{userId}")
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
    @GetMapping("filter/username/{login}")
	ResponseEntity<Users> getUserByLogin(@PathVariable("login") String login) {
		Users retUser = null;
		try {
			retUser = usersJsonizer.getUserByLogin(login);
		} catch (Exception e) {
			throw new IllegalStateException("Error getting user from database!", e);
		}

        return Optional.ofNullable(retUser).map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElseThrow(
                () -> new IllegalArgumentException(String.join("", "No user found in database for login: ", login)));
	}

	// anonymous access is needed.
	@PreAuthorize("hasRole('ROLE_ADMIN')") // only admin can save changed users
											// with REST.
    @PostMapping
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
    @PutMapping("{userId}")
	ResponseEntity<Users> updateUser(@RequestBody Users user) {
		return saveUser(user);
	}

    @GetMapping("exists/username/{login}")
	ResponseEntity<Boolean> ifSuchLoginExists(@PathVariable("login") String login) {
        return usersJsonizer.checkIfUsernameExists(login) ? new ResponseEntity<>(true, HttpStatus.ACCEPTED)
                : new ResponseEntity<>(false, HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("currentuser")
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
