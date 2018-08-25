package com.iotbackend.commons.envc.service.dto;

import java.util.List;

import lombok.Data;

@Data
public class FunctionsOfType {
	
	private String controlType;
	
	private List<String> functionsIds;

}
