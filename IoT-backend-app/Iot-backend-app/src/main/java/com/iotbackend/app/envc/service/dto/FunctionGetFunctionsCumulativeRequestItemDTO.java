package com.iotbackend.app.envc.service.dto;

import java.util.List;

import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.commons.envc.service.dto.FunctionsOfType;

import lombok.Data;

@Data
public class FunctionGetFunctionsCumulativeRequestItemDTO {
	
	private LocalType childLocalType;
	
	private List<String> localIds;
	
	private List<FunctionsOfType> requestList;
	
}
