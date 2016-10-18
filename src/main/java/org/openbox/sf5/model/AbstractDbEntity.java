package org.openbox.sf5.model;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * This class should be used in Hibernate entities that are stored in database.
 * 
 * @author Andrii Duplyk
 *
 */
@MappedSuperclass
public class AbstractDbEntity implements Serializable {

	private static final long serialVersionUID = -4019144518870996041L;

}
