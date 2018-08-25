package com.iotbackend.producer.envc.cache.repository;

import java.util.Map;

public interface DeviceAdviceRepository<ID, T> {
	
	void save(T t);
	
	Map<String, T> findAll();

}
