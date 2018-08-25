package com.iotbackend.producer.envc.cache.repository;

import java.util.Map;

public interface MessageRepository<ID, T> {

	Map<ID, T> findAll();

	void add(T entity);

	void delete(ID id);

	T findById(ID id);

}
