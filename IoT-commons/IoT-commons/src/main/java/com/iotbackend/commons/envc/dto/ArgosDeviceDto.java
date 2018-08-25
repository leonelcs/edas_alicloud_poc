package com.iotbackend.commons.envc.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class ArgosDeviceDto {
	
	private String deviceId;
	
	private String pin;
	
	private String name;
	
	private String location;
	
	private String version;
	
	private String ip;
	
	private String localIp;
	
	private Timestamp registeredOn;
	
	private String type;
	
}

