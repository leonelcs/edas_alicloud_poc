package com.iotbackend.commons.envc.service.dto;

import java.util.List;

import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;

import lombok.Data;

@Data
public class RPCFunctionsLocationRequest {
	
	private LocalType localType;
	
	private List<String> localIds;
	
	private List<FunctionsOfType> functions;

}
