package com.wallethub.util;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author ivofreitas
 */

public interface GenericRepository<T, ID extends Serializable> {

	Optional<T> save(T entity);

	Optional<T> merge(T entity);

	List<T> save(Iterable<T> entities);

	Optional<T> findOne(ID id);

	T findReference(ID id);

	List<T> findAll();

	Optional<List<T>> findAll(Integer page, Integer size, T param);

	void delete(ID id);

}
