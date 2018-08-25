package com.iotbackend.commons.envc.service.dto;

import java.util.Map;

@lombok.Data
public class RPCFunctionCache {
	
	private String deviceId;
	
	private String slaveId;
		
	private Map<String, Integer> functionData;

}
