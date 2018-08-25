package com.iotbackend.producer.envc.cache.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash("DeviceAdviceCache")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAdviceCache implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String deviceID;
	
	private Boolean online;
	
	private Timestamp onlineLastTime;

}
