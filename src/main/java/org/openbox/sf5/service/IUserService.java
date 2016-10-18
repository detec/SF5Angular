package org.openbox.sf5.service;

import org.openbox.sf5.model.UserDto;
import org.openbox.sf5.model.Users;

/**
 * Registration of new account.
 *
 * @author Andrii Duplyk
 *
 */
@FunctionalInterface
public interface IUserService {
	/**
	 * Add new user to database from DTO object.
	 * 
	 * @param accountDto
	 * @return
	 */
	Users registerNewUserAccount(UserDto accountDto);
}
