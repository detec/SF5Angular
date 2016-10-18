package org.openbox.sf5.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Satellite entity.
 *
 * @author Andrii Duplyk
 *
 */
@Entity
@Table(name = "Satellites")
@XmlRootElement(name = "satellite")
public class Satellites extends AbstractDbEntity implements Serializable {

	private static final long serialVersionUID = -2077586473579019427L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "name", unique = false, nullable = false, length = 50)
	private String name;

	public Satellites() {

	}

	public Satellites(String name) {
		this.name = name;
	}

	@Override
	public String toString() {

		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public long getId() {

		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}