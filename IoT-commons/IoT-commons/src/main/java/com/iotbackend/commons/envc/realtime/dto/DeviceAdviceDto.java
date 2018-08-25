package com.iotbackend.commons.envc.realtime.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAdviceDto {
	
	private String deviceId;
	
	private Boolean online;
	
	private Timestamp onlineLastTime;

}
