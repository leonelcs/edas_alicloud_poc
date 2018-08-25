package com.iotbackend.app.envc.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iotbackend.app.envc.service.dto.AlarmFunctionListRequestDTO;
import com.iotbackend.app.envc.service.dto.ServiceSingleResponseDTO;
import com.iotbackend.commons.envc.dto.AlarmObjectJsonDto;
import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.commons.envc.service.AlarmRPCService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@CrossOrigin
@RestController
@RequestMapping(value = "/alarm")
public class DeviceAlarmController {
	
	private static final Logger logger = LoggerFactory.getLogger(DeviceAlarmController.class);
	
	@Autowired
	AlarmRPCService alarmService;
	
	@PostMapping(value = "/alarmlist/department")
	@ApiOperation(value = "Responds a list of active alarms for each requested departement per mapping and per device type", notes = "Responds a list of alarms per mapping and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, AlarmObjectJsonDto>>>> getAlarmListByDepartment(
			@RequestBody AlarmFunctionListRequestDTO request) {
		
		logger.debug("Receiving local ids - {} ", request.getLocalIds().toString());
		logger.info("LocalType number: {}", LocalType.DEPARTMENT.getValue());
		return getAlarmListByLocation(LocalType.DEPARTMENT, request.getLocalIds());
	}
	
	@PostMapping(value = "/alarmlist/farm")
	@ApiOperation(value = "Responds a list of active alarms for each requested farm per mapping and per device type", notes = "Responds a list of alarms per mapping and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, AlarmObjectJsonDto>>>> getAlarmListByFarm(
			@RequestBody AlarmFunctionListRequestDTO request) {
		logger.debug("Receiving local ids - {} ", request.getLocalIds().toString());
		logger.info("LocalType number: {}", LocalType.FARM.getValue());
		return getAlarmListByLocation(LocalType.FARM, request.getLocalIds());
	}
	
	@PostMapping(value = "/alarmlist/building")
	@ApiOperation(value = "Responds a list of active alarms for each requested building per mapping and per device type", notes = "Responds a list of alarms per mapping and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, AlarmObjectJsonDto>>>> getAlarmListByBuilding(
			@RequestBody AlarmFunctionListRequestDTO request) {
		logger.debug("Receiving local ids - {} ", request.getLocalIds().toString());
		logger.info("LocalType number: {}", LocalType.BUILDING.getValue());
		return getAlarmListByLocation(LocalType.BUILDING, request.getLocalIds());
	}
	
	@PostMapping(value = "/alarmlist/area")
	@ApiOperation(value = "Responds a list of active alarms for each requested area per mapping and per device type", notes = "Responds a list of alarms per mapping and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, AlarmObjectJsonDto>>>> getAlarmListByArea(
			@RequestBody AlarmFunctionListRequestDTO request) {
		logger.debug("Receiving local ids - {} ", request.getLocalIds().toString());
		logger.info("LocalType number: {}", LocalType.AREA.getValue());
		return getAlarmListByLocation(LocalType.AREA, request.getLocalIds());
	}

	private ServiceSingleResponseDTO<Map<String, Map<String, Map<String, AlarmObjectJsonDto>>>> getAlarmListByLocation( LocalType localType, List<String> localIds) {
		logger.debug("Receiving local ids - {} of type {}",localIds.toString(), localType);
		ServiceSingleResponseDTO<Map<String, Map<String, Map<String, AlarmObjectJsonDto>>>> responseObject = new ServiceSingleResponseDTO<>();
		
		responseObject.setResultCode(0);
		responseObject.setResultMsg("success");
		Map<String,Map<String,Map<String,AlarmObjectJsonDto>>> alarmMap = alarmService.findAlarmsByLocationList(localType, localIds);
		logger.info("See the alarmMap {}", alarmMap.toString());
		responseObject.setData(alarmMap);
		
		return responseObject;
	}
	
	@PostMapping(value = "/alarmcount/department")
	@ApiOperation(value = "Responds a list of active alarm counts for the department and its children per mapping and per device type", notes = "Responds a list of alarm counts for the mapping and its children per mapping and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Long>>> getAlarmCountByDepartment(
			@RequestBody AlarmFunctionListRequestDTO request) {
		logger.debug("Receiving local ids - {} ", request.getLocalIds().toString());
		return getAlarmCountByLocation(LocalType.DEPARTMENT, request.getLocalIds());
	}
	
	@PostMapping(value = "/alarmcount/farm")
	@ApiOperation(value = "Responds a list of active alarm counts for the department and its children per mapping and per device type", notes = "Responds a list of alarm counts for the mapping and its children per mapping and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Long>>> getAlarmCountByFarm(
			@RequestBody AlarmFunctionListRequestDTO request) {
		logger.debug("Receiving local ids - {} ", request.getLocalIds().toString());
		return getAlarmCountByLocation(LocalType.FARM, request.getLocalIds());
	}
	
	@PostMapping(value = "/alarmcount/building")
	@ApiOperation(value = "Responds a list of active alarm counts for the building and its children per mapping and per device type", notes = "Responds a list of alarm counts for the mapping and its children per mapping and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Long>>> getAlarmCountByBuilding(
			@RequestBody AlarmFunctionListRequestDTO request) {
		logger.debug("Receiving local ids - {} ", request.getLocalIds().toString());
		return getAlarmCountByLocation(LocalType.BUILDING, request.getLocalIds());
	}
	
	@PostMapping(value = "/alarmcount/area")
	@ApiOperation(value = "Responds a list of active alarm counts for the area and its children per mapping and per device type", notes = "Responds a list of alarm counts for the mapping and its children per mapping and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Long>>> getAlarmCountByArea(
			@RequestBody AlarmFunctionListRequestDTO request) {
		logger.debug("Receiving local ids - {} ", request.getLocalIds().toString());
		return getAlarmCountByLocation(LocalType.AREA, request.getLocalIds());
	}

	private ServiceSingleResponseDTO<Map<String, Map<String, Long>>> getAlarmCountByLocation( LocalType localType, List<String> localIds) {
		
		ServiceSingleResponseDTO<Map<String, Map<String, Long>>> responseObject = new ServiceSingleResponseDTO<>();
		
		responseObject.setResultCode(0);
		responseObject.setResultMsg("success");
		responseObject.setData(alarmService.getAlarmsOfLocationCumulative(localType, localIds));
		
		return responseObject;
	}

}
