package com.iotbackend.producer.envc.service.impl;

import java.util.List;

import com.alibaba.boot.hsf.annotation.HSFProvider;
import com.iotbackend.commons.envc.service.SlavelistRPCService;
import com.iotbackend.commons.envc.service.dto.RPCSlaveInfo;

@HSFProvider(serviceInterface = SlavelistRPCService.class, serviceVersion = "1.0.0")
public class SlavelistRPCServiceImpl implements SlavelistRPCService {

	@Override
	public RPCSlaveInfo getSlaveInfo(String deviceId, String slaveId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RPCSlaveInfo> getSlavesByDepartment(String departmentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RPCSlaveInfo> getSlavesByFarm(String departmentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RPCSlaveInfo> getSlavesByBuilding(String buildingId) {
		// TODO Auto-generated method stub
		return null;
	}

}
