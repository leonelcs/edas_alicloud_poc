package com.iotbackend.app.envc.controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iotbackend.app.envc.service.dto.FunctionGetFunctionsCumulativeRequestItemDTO;
import com.iotbackend.app.envc.service.dto.FunctionGetFunctionsRequestDTO;
import com.iotbackend.app.envc.service.dto.ServiceSingleResponseDTO;
import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.commons.envc.service.FunctionsRPCService;
import com.iotbackend.commons.envc.service.dto.RPCFunctionsLocationRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(value = "/functions")
@CrossOrigin
public class DeviceFunctionsController {

	private static final Logger logger = LoggerFactory.getLogger(DeviceFunctionsController.class);

	@Autowired
	FunctionsRPCService functionsService;

	@PostMapping(value = "/functionlist/department")
	@ApiOperation(value = "Responds a list of department function values per device and per device type", notes = "Responds a list of department function values per device and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Integer>>>> getFunctionListByDepartment(
			@RequestBody FunctionGetFunctionsRequestDTO request) {
		logger.debug("The json is {} ", request.toString());
		return requestFunctions(request, LocalType.DEPARTMENT);
	}

	@PostMapping(value = "/functionlist/farm")
	@ApiOperation(value = "Responds a list of farm function values per device and per device type", notes = "Responds a list of farm function values per device and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Integer>>>> getFunctionListByFarm(
			@RequestBody FunctionGetFunctionsRequestDTO request) {
		return requestFunctions(request, LocalType.FARM);
	}

	@PostMapping(value = "/functionlist/building")
	@ApiOperation(value = "Responds a list of building function values per device and per device type", notes = "Responds a list of building function values per device and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Integer>>>> getFunctionListByBuilding(
			@RequestBody FunctionGetFunctionsRequestDTO request) {
		return requestFunctions(request, LocalType.BUILDING);
	}

	@PostMapping(value = "/functionlist/area")
	@ApiOperation(value = "Responds a list of area function values per device and per device type", notes = "Responds a list of area function values per device and per device type")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Integer>>>> getFunctionListByArea(
			@RequestBody FunctionGetFunctionsRequestDTO request) {
		return requestFunctions(request, LocalType.AREA);
	}
	
	@PostMapping(value = "/functionlist/area/cumulative")
	@ApiOperation(value = "Responds the sum of values for a function list of all children to the area", notes = "Responds the sum of values for a function list of all children to the area")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Long>>>> getFunctionListByAreaCumulative(
			@RequestBody FunctionGetFunctionsCumulativeRequestItemDTO request) {
		
		Map<String, Map<String, Map<String, Long>>> result = 
				functionsService.getFunctionsOfLocationCumulative(
						request.getRequestList(), 
						LocalType.AREA, 
						request.getLocalIds(), 
						request.getChildLocalType());
		
		ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Long>>>> responseObject = new ServiceSingleResponseDTO<>();
		responseObject.setResultCode(0);
		responseObject.setResultMsg("success");
		responseObject.setData(result);
		
		return responseObject;
	}
	
	@PostMapping(value = "/functionlist/building/cumulative")
	@ApiOperation(value = "Responds the sum of values for a function list of all children to the building", notes = "Responds the sum of values for a function list of all children to the building")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Long>>>> getFunctionListByBuildingCumulative(
			@RequestBody FunctionGetFunctionsCumulativeRequestItemDTO request) {
		
		Map<String, Map<String, Map<String, Long>>> result = 
				functionsService.getFunctionsOfLocationCumulative(
						request.getRequestList(), 
						LocalType.BUILDING, 
						request.getLocalIds(), 
						request.getChildLocalType());
		
		ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Long>>>> responseObject = new ServiceSingleResponseDTO<>();
		responseObject.setResultCode(0);
		responseObject.setResultMsg("success");
		responseObject.setData(result);
		
		return responseObject;
	}
	
	@PostMapping(value = "/functionlist/farm/cumulative")
	@ApiOperation(value = "Responds the sum of values for a function list of all children to the farm", notes = "Responds the sum of values for a function list of all children to the farm")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Long>>>> getFunctionListByFarmCumulative(
			@RequestBody FunctionGetFunctionsCumulativeRequestItemDTO request) {
		
		Map<String, Map<String, Map<String, Long>>> result = 
				functionsService.getFunctionsOfLocationCumulative(
						request.getRequestList(), 
						LocalType.FARM, 
						request.getLocalIds(), 
						request.getChildLocalType());
		
		ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Long>>>> responseObject = new ServiceSingleResponseDTO<>();
		responseObject.setResultCode(0);
		responseObject.setResultMsg("success");
		responseObject.setData(result);
		
		return responseObject;
	}
	
	@PostMapping(value = "/functionlist/department/cumulative")
	@ApiOperation(value = "Responds the sum of values for a function list of all children to the department", notes = "Responds the sum of values for a function list of all children to the department")
	public ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Long>>>> getFunctionListByDepartmentCumulative(
			@RequestBody FunctionGetFunctionsCumulativeRequestItemDTO request) {
		
		Map<String, Map<String, Map<String, Long>>> result = 
				functionsService.getFunctionsOfLocationCumulative(
						request.getRequestList(), 
						LocalType.DEPARTMENT, 
						request.getLocalIds(), 
						request.getChildLocalType());
		
		ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Long>>>> responseObject = new ServiceSingleResponseDTO<>();
		responseObject.setResultCode(0);
		responseObject.setResultMsg("success");
		responseObject.setData(result);
		
		return responseObject;
	}

	private ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Integer>>>> requestFunctions(
			FunctionGetFunctionsRequestDTO request, LocalType localType) {

		logger.debug("received message is {}", request);
		ServiceSingleResponseDTO<Map<String, Map<String, Map<String, Integer>>>> response = new ServiceSingleResponseDTO<>();
		response.setResultCode(0);
		response.setResultMsg("success");
		response.setData(requestFunctionList(request, localType));
		return response;

	}

	private Map<String, Map<String, Map<String, Integer>>> requestFunctionList(FunctionGetFunctionsRequestDTO request,
			LocalType localType) {

		RPCFunctionsLocationRequest functionsRequest = new RPCFunctionsLocationRequest();
		functionsRequest.setLocalType(localType);
		functionsRequest.setLocalIds(request.getLocalIds());
		functionsRequest.setFunctions(request.getRequestList());
		
		return functionsService.getFunctionsOfLocationList(functionsRequest);

	}
}
