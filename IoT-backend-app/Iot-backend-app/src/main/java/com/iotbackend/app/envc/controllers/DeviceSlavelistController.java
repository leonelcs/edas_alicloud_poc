package com.iotbackend.app.envc.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iotbackend.commons.envc.service.SlavelistRPCService;
import com.iotbackend.commons.envc.service.dto.RPCSlaveInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@CrossOrigin
@RestController
@RequestMapping(value = "/slavelist")
public class DeviceSlavelistController {
	
	private static final Logger logger = LoggerFactory.getLogger(DeviceSlavelistController.class);
	
	@Autowired
	SlavelistRPCService slavelistService;
	
	@GetMapping(value = "/get_slave_info/{deviceId}/{slaveId}")
	@ApiOperation(value = "Respond the info of a slave")
	public ResponseEntity<RPCSlaveInfo> getSlaveInfo(@PathVariable String deviceId, @PathVariable String slaveId){
		
		RPCSlaveInfo info = slavelistService.getSlaveInfo(deviceId, slaveId);
		
		if(info != null)
			return new ResponseEntity<RPCSlaveInfo>(info, HttpStatus.OK);
		else
			return new ResponseEntity<RPCSlaveInfo>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "/get_slaves_by_department/{departmentId}")
	@ApiOperation(value = "Respond a list of slaves contained by the department")
	public ResponseEntity<List<RPCSlaveInfo>> getSlavesByDepartment(@PathVariable String departmentId){
		
		List<RPCSlaveInfo> slavelist = slavelistService.getSlavesByDepartment(departmentId);
		
		if(slavelist != null)
			return new ResponseEntity<List<RPCSlaveInfo>>(slavelist, HttpStatus.OK);
		else
			return new ResponseEntity<List<RPCSlaveInfo>>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "/get_slaves_by_farm/{farmId}")
	@ApiOperation(value = "Respond a list of slaves contained by the farm")
	public ResponseEntity<List<RPCSlaveInfo>> getSlavesByFarm(@PathVariable String farmId) {

		List<RPCSlaveInfo> slavelist = slavelistService.getSlavesByFarm(farmId);
		
		if(slavelist != null)
			return new ResponseEntity<List<RPCSlaveInfo>>(slavelist, HttpStatus.OK);
		else
			return new ResponseEntity<List<RPCSlaveInfo>>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "/get_slaves_by_building/{buildingId}")
	@ApiOperation(value = "Respond a list of slaves contained by the building")
	public ResponseEntity<List<RPCSlaveInfo>> getSlavesByBuilding(@PathVariable String buildingId) {

		List<RPCSlaveInfo> slavelist = slavelistService.getSlavesByBuilding(buildingId);
		
		if(slavelist != null)
			return new ResponseEntity<List<RPCSlaveInfo>>(slavelist, HttpStatus.OK);
		else
			return new ResponseEntity<List<RPCSlaveInfo>>(HttpStatus.NO_CONTENT);
	}
}
