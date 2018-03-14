package com.wallethub.repository.impl;

import javax.persistence.EntityManager;

import com.wallethub.domain.Log;
import com.wallethub.repository.Repository;
import com.wallethub.util.AbstractRepository;

/**
 * @author ivofreitas
 *
 */
public class LogRepositoryImpl extends AbstractRepository<Log, Long> implements Repository<Log>{

	public LogRepositoryImpl(EntityManager entityManager) {
		super(entityManager);
	}

}
