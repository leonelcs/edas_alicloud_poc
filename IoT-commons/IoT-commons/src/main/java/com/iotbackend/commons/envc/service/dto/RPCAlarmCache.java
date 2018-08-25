package com.iotbackend.commons.envc.service.dto;

import java.util.List;

@lombok.Data
public class RPCAlarmCache {
	
	private String deviceId;
	
	private String slaveId;
	
	private List<RPCAlarmItem> alarmData;

}
