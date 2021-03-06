package org.openbox.sf5.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.openbox.sf5.model.AbstractDbEntity;

public interface DAO {

	public <T extends AbstractDbEntity> void add(T obj);

	public <T extends AbstractDbEntity> void remove(Class<T> type, long id);

	public <T extends AbstractDbEntity> void update(T obj);

	public <T extends AbstractDbEntity> T select(Class<T> type, long id);

	public <T extends AbstractDbEntity> void saveOrUpdate(T obj);

	public <T extends AbstractDbEntity> List<T> findAll(Class<T> type);

	public <T extends AbstractDbEntity> List<T> findAllWithRestrictions(Class<T> type, Criterion criterion);

	public SessionFactory getSessionFactory();

	public Session openSession();

	public <T extends AbstractDbEntity> T updateEM(T entity);

	public <T extends AbstractDbEntity> T saveEM(T entity);

	//
	// public void setSessionFactory(SessionFactory sessionFactory);

}
