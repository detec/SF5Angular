package org.openbox.sf5.common;

import org.openbox.sf5.dao.DAO;
import org.openbox.sf5.service.CriterionService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractJsonizerTest {

	@Autowired
	public DAO DAO;

	// @Autowired
	// public SessionFactory sessionFactory;

	@Autowired
	public CriterionService criterionService;

	public void setUpAbstract() {
		disableLogsWhenTesting();

	}

	public void disableLogsWhenTesting() {
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.OFF);

	}
}
