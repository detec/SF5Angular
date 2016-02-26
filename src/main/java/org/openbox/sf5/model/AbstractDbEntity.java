package org.openbox.sf5.model;

// This class should be used in Hibernate entities that are stored in database.
public abstract class AbstractDbEntity {

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
