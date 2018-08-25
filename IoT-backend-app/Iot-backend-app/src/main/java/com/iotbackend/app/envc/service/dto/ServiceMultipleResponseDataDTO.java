package com.iotbackend.app.envc.service.dto;

import java.util.List;

import lombok.Data;

@Data
public class ServiceMultipleResponseDataDTO<T> {
	
	private List<T> data;
	
	private int total;
}	
