package com.iotbackend.app.envc.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iotbackend.app.envc.service.dto.ServiceMultipleResponseDTO;
import com.iotbackend.app.envc.service.dto.ServiceMultipleResponseDataDTO;
import com.iotbackend.commons.envc.dto.ArgosDeviceDto;
import com.iotbackend.commons.envc.realtime.dto.DeviceAdviceDto;
import com.iotbackend.commons.envc.service.AnnounceRPCService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@CrossOrigin
@RestController
@RequestMapping(value = "/announce")
public class DeviceAnnounceController {
	
	
	private static final Logger logger = LoggerFactory.getLogger(DeviceAnnounceController.class);
	
	@Autowired
	AnnounceRPCService announceService;

	@GetMapping(value = "/get_device_status")
	@ApiOperation(value="Respond list of device status",notes="Returns the list of devices and their status")
	public ServiceMultipleResponseDTO<DeviceAdviceDto> getDevicesStatus() {
		
		List<DeviceAdviceDto> data = null;
		try {
			data = announceService.getAllDeviceStatus();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Failed to get mappings for device");
			e.printStackTrace();
		}
		
		ServiceMultipleResponseDataDTO<DeviceAdviceDto> resultData = new ServiceMultipleResponseDataDTO<>();
		resultData.setData(data);
		resultData.setTotal(resultData.getData().size());
		
		ServiceMultipleResponseDTO<DeviceAdviceDto> resultObject = new ServiceMultipleResponseDTO<>();
		resultObject.setResultCode(0);
		resultObject.setResultMsg("success");
		resultObject.setData(resultData);

		return resultObject;
	}
	
	@GetMapping("/registered_devices")
	@ApiOperation(value="Respond list of device registration information",notes="Returns the list of devices with all important information")
	public ServiceMultipleResponseDTO<ArgosDeviceDto> listAllDevices() {
		
		List<ArgosDeviceDto> data = null;
		try {
			data = announceService.listAllArgosDevices();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Failed to get mappings for device");
			e.printStackTrace();
		}
		
		ServiceMultipleResponseDataDTO<ArgosDeviceDto> resultData = new ServiceMultipleResponseDataDTO<>();
		resultData.setData(data);
		resultData.setTotal(resultData.getData().size());
		
		ServiceMultipleResponseDTO<ArgosDeviceDto> resultObject = new ServiceMultipleResponseDTO<>();
		resultObject.setResultCode(0);
		resultObject.setResultMsg("success");
		resultObject.setData(resultData);

		return resultObject;
 
	}
	

	
 }
