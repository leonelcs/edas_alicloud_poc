package com.iotbackend.producer.envc.cache.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisServiceConfig {

	@Value("${redis.server}")
	private String redisServer = "";

	@Value("${redis.port}")
	private String redisPort = "";
	
	@Value("${redis.password}")
	private String redisPassword = "";


	public Integer getRedisPort() {
		return new Integer(redisPort).intValue();
	}
	
	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
		jedisConnFactory.setHostName(redisServer);
		jedisConnFactory.setPort(getRedisPort());
		if (redisPassword!=null && !redisPassword.isEmpty()) {
			jedisConnFactory.setPassword(redisPassword);
		}
		return jedisConnFactory;
	}

	@Bean
	@Scope("prototype")
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		RedisSerializer<String> keySerializer = new StringRedisSerializer();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(keySerializer);
		template.setHashKeySerializer(keySerializer);
		return template;
	}

}
