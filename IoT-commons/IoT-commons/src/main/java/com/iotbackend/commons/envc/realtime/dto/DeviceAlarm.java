package com.iotbackend.commons.envc.realtime.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAlarm {

	private String id;
	
	private String sensor;
	
	private String active;
	
	private String board;
	
	private String error;
	
	private Timestamp createdOn;

}
