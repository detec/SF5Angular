package org.openbox.sf5.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public class UserDto {

	@NotBlank
	@Range(min = 1, max = 20)
	private String username;

	@NotBlank
	@Range(min = 1, max = 20)
	private String password;

	@NotBlank
	@Range(min = 1, max = 20)
	private String matchingPassword;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMatchingPassword() {
		return matchingPassword;
	}

	public void setMatchingPassword(String matchingPassword) {
		this.matchingPassword = matchingPassword;
	}

	// standard getters and setters
}
