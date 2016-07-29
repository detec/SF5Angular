package org.openbox.sf5.service;

import org.openbox.sf5.model.UserDto;
import org.openbox.sf5.model.Users;

public interface IUserService {
	Users registerNewUserAccount(UserDto accountDto) throws IllegalArgumentException;
}
