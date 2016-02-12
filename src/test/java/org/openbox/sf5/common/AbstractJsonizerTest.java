package org.openbox.sf5.common;

import org.hibernate.SessionFactory;
import org.openbox.sf5.dao.DAO;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractJsonizerTest {

	@Autowired
	public DAO DAO;

	@Autowired
	public SessionFactory sessionFactory;

	// @Autowired
	// public CriterionService criterionService;

	public void setUpAbstract() {
		disableLogsWhenTesting();

	}

	public void disableLogsWhenTesting() {
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.OFF);

	}
}
