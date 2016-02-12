package org.openbox.sf5.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.openbox.sf5.model.AbstractDbEntity;

public interface DAO {

	public <T extends AbstractDbEntity> void add(T obj);

	public <T extends AbstractDbEntity> void remove(Class<T> type, long id);

	public <T extends AbstractDbEntity> void update(T obj);

	public <T extends AbstractDbEntity> T select(Class<T> type, long id);

	public <T extends AbstractDbEntity> void saveOrUpdate(T obj);

	public <T extends AbstractDbEntity> List<T> list(Class<T> type);

	public <T extends AbstractDbEntity> List<T> ObjectsCriterionList(Class<T> type, Criterion criterion);

	public SessionFactory getSessionFactory();

	public void setSessionFactory(SessionFactory sessionFactory);

}
