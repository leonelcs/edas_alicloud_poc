package com.iotbackend.commons.envc.service;

import java.util.List;

import com.iotbackend.commons.envc.service.dto.RPCSlaveInfo;

public interface SlavelistRPCService {
	
	RPCSlaveInfo getSlaveInfo(String deviceId, String slaveId);
	
	List<RPCSlaveInfo> getSlavesByDepartment(String departmentId);
	
	List<RPCSlaveInfo> getSlavesByFarm(String farmId);
	
	List<RPCSlaveInfo> getSlavesByBuilding(String buildingId);

}
