package org.openbox.sf5.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.openbox.sf5.model.AbstractDbEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class DAOImpl implements DAO, Serializable {

	@Override
	public Session openSession() {
		Session session = getSessionFactory().openSession();
		// Session session = entityManager.unwrap(Session.class);
		return session;
	}

	@Override
	public <T extends AbstractDbEntity> void add(T obj) {
		Session s = openSession();
		s.beginTransaction();
		s.save(obj);
		s.getTransaction().commit();
		s.close();

	}

	@Override
	public <T extends AbstractDbEntity> void remove(Class<T> type, long id) {
		Session s = openSession();
		s.beginTransaction();
		Object c = s.get(type, id);
		s.delete(c);
		s.getTransaction().commit();
		s.close();
	}

	@Override
	public <T extends AbstractDbEntity> void update(T obj) {
		Session s = openSession();
		s.beginTransaction();
		s.update(obj);
		s.getTransaction().commit();
		s.close();
	}

	@Override
	@Transactional(readOnly = true)
	public <T extends AbstractDbEntity> T select(Class<T> type, long id) {
		Session s = openSession();

		s.beginTransaction();
		@SuppressWarnings("unchecked")
		T obj = s.get(type, id);
		s.close();
		return obj;
	}

	@Override
	public <T extends AbstractDbEntity> void saveOrUpdate(T obj) {
		Session s = openSession();

		s.beginTransaction();
		s.saveOrUpdate(obj);
		s.getTransaction().commit();
		s.close();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public <T extends AbstractDbEntity> List<T> findAll(Class<T> type) {

		List<T> list = new ArrayList<>();

		Session s = openSession();
		s.beginTransaction();
		list = s.createQuery("from " + type.getName() + " order by id").list();
		s.getTransaction().commit();
		s.close();
		return list;
	}

	@Override
	@Transactional(readOnly = true)
	public <T extends AbstractDbEntity> List<T> ObjectsCriterionList(Class<T> type, Criterion criterion) {
		Session s = openSession();

		Criteria criteria = s.createCriteria(type)

				.add(criterion)

				.addOrder(Order.desc("id"));
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY); // kill
																					// duplicates

		@SuppressWarnings("unchecked")
		List<T> list = criteria.list();

		s.close();
		return list;
	}

	@Override
	public <T extends AbstractDbEntity> T updateEM(T entity) {
		T mergedEntity = getEntityManager().merge(entity);
		return mergedEntity;
	}

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	// @Autowired
	private SessionFactory sessionFactory;

	public DAOImpl() {

	}

	@Override
	public SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			// Session session = entityManager.unwrap(Session.class);
			// sessionFactory = session.getSessionFactory();

			sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

		}
		return sessionFactory;

	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public DAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private EntityManager entityManager;

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	private static final long serialVersionUID = 643710250463318145L;
}
