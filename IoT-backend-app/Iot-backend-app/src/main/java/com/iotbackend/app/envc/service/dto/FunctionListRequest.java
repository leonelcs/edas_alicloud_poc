package com.iotbackend.app.envc.service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunctionListRequest {
	private String controlType;

	List<String> functionList;
}
