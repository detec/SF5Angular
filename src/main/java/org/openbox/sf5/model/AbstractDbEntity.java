package org.openbox.sf5.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

// This class should be used in Hibernate entities that are stored in database.
@MappedSuperclass
public class AbstractDbEntity implements Serializable {

	private static final long serialVersionUID = -4019144518870996041L;

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
