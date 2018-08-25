package com.iotbackend.commons.envc.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class AlarmObjectJsonDto {
	
	private String alarmId;
	
	private String active; //0: not active 1: alarm 2:

	private String board;
	
	private String sensor;
	
	private String error;
	
	private Timestamp createdOn;

}
