package org.openbox.sf5.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "Usersauthorities")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Usersauthorities extends AbstractDbEntity implements Serializable {

	private static final long serialVersionUID = 108703010218830663L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne
	@JoinColumn(name = "parent_id", unique = false, nullable = false, foreignKey = @ForeignKey(name = "FK_UserAuthorities"))
	@JsonBackReference
	@XmlIDREF
	private Users parent_id;

	@Column(name = "username", unique = false, nullable = false)
	private String username;

	private long lineNumber;

	@Column(name = "authority", unique = false, nullable = false, length = 50)
	private String authority;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Users getParent_id() {
		return parent_id;
	}

	public void setParent_id(Users parent_id) {
		this.parent_id = parent_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public Usersauthorities(String username, String authority, Users parent_id, long pLine) {

		this.username = username;
		this.authority = authority;
		this.parent_id = parent_id;
		lineNumber = pLine;

	}

	public Usersauthorities() {
	}

}