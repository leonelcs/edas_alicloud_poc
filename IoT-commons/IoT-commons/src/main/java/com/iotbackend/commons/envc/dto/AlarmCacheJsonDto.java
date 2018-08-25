package com.iotbackend.commons.envc.dto;

import java.util.List;

import lombok.Data;

@Data
public class AlarmCacheJsonDto {
	
	private String slaveId;
	
	private String controlType;
	
	private MappingJsonDto mapping;
	
	private List<AlarmObjectJsonDto> data;

}
