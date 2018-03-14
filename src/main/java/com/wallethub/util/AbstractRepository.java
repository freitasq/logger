package com.wallethub.util;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.jpa.HibernateEntityManager;

import com.mysql.jdbc.MySQLConnection;

/**
 * @author ivofreitas
 */

public abstract class AbstractRepository<T, ID extends Serializable> implements GenericRepository<T, ID> {

	protected final EntityManager entityManager;

	protected final Class<T> entityClass;

	private final Logger LOGGER = Logger.getLogger(AbstractRepository.class);

	@SuppressWarnings("unchecked")
	public AbstractRepository(EntityManager entityManager) {
		super();
		this.entityManager = Objects.requireNonNull(entityManager);
		this.entityClass = (Class<T>) Generics.getTypeParameter(this.getClass());
	}

	protected Session getSession() {
		HibernateEntityManager hem = this.entityManager.unwrap(HibernateEntityManager.class);
		return hem.getSession();
	}

	protected org.hibernate.Criteria criteria() {
		return getSession().createCriteria(this.entityClass);
	}

	protected org.hibernate.Criteria criteria(Object obj) {
		return getSession().createCriteria(obj.getClass());
	}

	protected org.hibernate.Criteria byExample(org.hibernate.criterion.Example example) {
		Objects.requireNonNull(example);
		return criteria().add(example);
	}

	protected org.hibernate.Criteria byExample(org.hibernate.criterion.Example example, Integer page, Integer size) {
		Objects.requireNonNull(example);
		return criteria().add(example).setFirstResult(page * size).setMaxResults(size);
	}

	@Override
	public Optional<T> save(T entity) {
		Objects.requireNonNull(entity);
		return Optional.ofNullable(this.entityManager.merge(entity));
	}

	public Optional<T> merge(T entity) {
		Objects.requireNonNull(entity);
		return Optional.ofNullable(this.entityManager.merge(entity));
	}

	@Override
	public List<T> save(Iterable<T> entities) {
		Objects.requireNonNull(entities);
		List<T> entitiesSaved = Collections.emptyList();
		entities.forEach(entity -> entitiesSaved.add(entityManager.merge(entity)));
		return entitiesSaved;
	}

	@Override
	public Optional<T> findOne(ID id) {
		Objects.requireNonNull(id);
		return Optional.ofNullable(this.entityManager.find(entityClass, id));
	}

	@Override
	public T findReference(ID id) {
		Objects.requireNonNull(id);
		return this.entityManager.getReference(entityClass, id);
	}

	@Override
	public List<T> findAll() {
		CriteriaQuery<T> criteriaQuery = this.entityManager.getCriteriaBuilder().createQuery(this.entityClass);
		Root<T> from = criteriaQuery.from(this.entityClass);
		criteriaQuery.select(from);
		return this.entityManager.createQuery(criteriaQuery).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<List<T>> findAll(Integer page, Integer size, T param) {
		Example example = Example.create(param).ignoreCase().enableLike(MatchMode.ANYWHERE);
		return Optional.ofNullable(byExample(example, page, size).addOrder(Order.asc("id")).list());
	}

	@Override
	public void delete(ID id) {
		this.entityManager.remove(findReference(id));
	}

	protected ConnectionProvider getConnectionProvider() {
		return getSession().getSessionFactory().getSessionFactoryOptions().getServiceRegistry()
				.getService(ConnectionProvider.class);
	}

	protected Connection getConnection(ConnectionProvider connectionProvider) {
		try {
			return connectionProvider.getConnection();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	protected Connection getWrapper(Connection connection) {
		try {
			return connection.unwrap(MySQLConnection.class);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	protected void closeConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
