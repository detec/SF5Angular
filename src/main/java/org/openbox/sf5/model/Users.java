package org.openbox.sf5.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "Users")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Users extends AbstractDbEntity implements Serializable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}
		Users other = (Users) obj;
		if (enabled != other.enabled) {
			return false;
		}
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

	private static final long serialVersionUID = -6789497093756301793L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	public long getId() {

		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "username", unique = false, nullable = false, length = 50)
	@XmlID
	private String username;

	public void setusername(String username) {
		this.username = username;
	}

	public String getusername() {
		return username;
	}

	@Override
	public String toString() {
		return username;
	}

	@Column(name = "password", unique = false, nullable = false, length = 15)
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String Password) {
		this.password = Password;
	}

	@Column(name = "enabled", unique = false, nullable = false)
	private boolean enabled;

	public boolean getenabled() {
		return enabled;
	}

	public void setenabled(boolean enabled) {
		this.enabled = enabled;
	}

	@OneToMany(mappedBy = "parent_id", fetch = FetchType.EAGER, orphanRemoval = true)
	@Cascade({ CascadeType.ALL })
	@OrderColumn(name = "lineNumber")
	@JsonManagedReference
	@Valid
	@NotNull
	@Size(min = 1)
	private List<Usersauthorities> authorities;

	public List<Usersauthorities> getauthorities() {
		return authorities;
	}

	public void setauthorities(List<Usersauthorities> authorities) {
		this.authorities = authorities;
	}

	public Users(String username, String Password, boolean enabled, List<Usersauthorities> authorities) {

		this.username = username;
		this.password = Password;
		this.enabled = enabled;
		this.authorities = authorities;

	}

	public Users() {
	}

}