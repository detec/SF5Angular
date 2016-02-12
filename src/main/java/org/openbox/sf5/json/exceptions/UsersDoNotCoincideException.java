package org.openbox.sf5.json.exceptions;

public class UsersDoNotCoincideException extends Exception {

	private String messsage;

	public UsersDoNotCoincideException(String messsage) {
		this.messsage = messsage;
	}

	private static final long serialVersionUID = -7482837951781925896L;

}
