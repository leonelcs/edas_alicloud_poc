package com.iotbackend.producer.envc.cache.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class IntegerSerializer implements RedisSerializer<Integer> {
	@Override
	public byte[] serialize(Integer value) throws SerializationException {
		if (value == null)
			return null;
		
		return value.toString().getBytes();
		
		//return new byte[] { (byte) ((value >>> 24) + 0x30), (byte) ((value >>> 16) + 0x30), (byte) ((value >>> 8) + 0x30), (byte) (((int) value) + 0x30) };
	}

	@Override
	public Integer deserialize(byte[] b) throws SerializationException {
		if (b == null)
			return null;
		
		return Integer.parseInt(new String(b));
	}
}
