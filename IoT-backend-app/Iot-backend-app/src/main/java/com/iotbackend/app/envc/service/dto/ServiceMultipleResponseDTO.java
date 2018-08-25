package com.iotbackend.app.envc.service.dto;

import lombok.Data;

@Data
public class ServiceMultipleResponseDTO<T> {

	private int resultCode;
	
	private String resultMsg;
	
	private ServiceMultipleResponseDataDTO<T> data;
	
}
