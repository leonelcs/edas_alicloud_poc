package com.iotbackend.commons.envc.dto;

import lombok.Data;

@Data
public class SlaveCacheJsonDto {
	
	private String argosId;
	
	private String name;
	
	private String hardwareType;
	
	private String controlType;
	
	private MappingJsonDto[] mappings;
	
	

}
