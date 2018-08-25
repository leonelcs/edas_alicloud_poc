package com.iotbackend.producer.envc.cache.repository.impl;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.iotbackend.producer.envc.cache.domain.DeviceAdviceCache;
import com.iotbackend.producer.envc.cache.repository.DeviceAdviceRepository;

@Repository
public class DeviceAdviceRepositoryImpl implements DeviceAdviceRepository<String, DeviceAdviceCache> {

	/**
	 * basekey is dc, short name for Device Registration
	 * The whole key will be separated by : 
	 */
	private static final String baseKEY = "dr";
	
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String, String, DeviceAdviceCache> hashOperations;
	
	public DeviceAdviceRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	@PostConstruct
	private void init() {
		hashOperations = redisTemplate.opsForHash();
	}
	
	@Override
	public void save(DeviceAdviceCache deviceAdvice) {
		hashOperations.put(baseKEY,deviceAdvice.getDeviceID(),deviceAdvice);
	}

	@Override
	public Map<String, DeviceAdviceCache> findAll() {
		return hashOperations.entries(baseKEY);
	}

}
