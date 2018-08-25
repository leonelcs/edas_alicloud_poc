package com.iotbackend.commons.envc.realtime.dto;

import java.io.Serializable;
import java.util.Map;

import com.iotbackend.commons.envc.dto.MappingJsonDto;

import lombok.Data;


@Data
public class DeviceSlaveCache implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String slaveId;
	
	private String controlType;
	
	private MappingJsonDto mapping;
	
	private Map<String, Integer> data;

}
