package com.iotbackend.app.envc.service.dto;

import lombok.Data;

@Data
public class ServiceSingleResponseDTO<T> {

	private int resultCode;
	
	private String resultMsg;
	
	private T data;
	
}
