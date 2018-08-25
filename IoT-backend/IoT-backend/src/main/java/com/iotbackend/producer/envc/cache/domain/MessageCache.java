package com.iotbackend.producer.envc.cache.domain;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash("Message")
@Data
@NoArgsConstructor
public class MessageCache implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String text;

}
