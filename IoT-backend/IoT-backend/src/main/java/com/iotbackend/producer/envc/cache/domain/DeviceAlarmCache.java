package com.iotbackend.producer.envc.cache.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@RedisHash("DeviceAlarmCache")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of= {"alarmId"})
public class DeviceAlarmCache implements Serializable {

	/**
	 * No version controlling serialized objects
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String alarmId;
	
	private String active; //0: delete 1: warning 2: active

	private String board;
	
	private String sensor;
	
	private String error;
	
	private Timestamp createdOn;
	
}
