package com.iotbackend.producer.envc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.boot.hsf.annotation.HSFProvider;
import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.commons.envc.service.FunctionsRPCService;
import com.iotbackend.commons.envc.service.dto.FunctionsOfType;
import com.iotbackend.commons.envc.service.dto.RPCFunctionCache;
import com.iotbackend.commons.envc.service.dto.RPCFunctionsLocationRequest;
import com.iotbackend.producer.envc.cache.repository.DeviceFunctionRepository;

@HSFProvider(serviceInterface = FunctionsRPCService.class, serviceVersion = "1.0.0")
public class FunctionsRPCServiceImpl implements FunctionsRPCService {

	private static final Logger logger = LoggerFactory.getLogger(FunctionsRPCServiceImpl.class);

	@Autowired
	DeviceFunctionRepository functionRepository;

	@Override
	public RPCFunctionCache getCacheBySlave(String deviceId, String slaveId) {
		RPCFunctionCache deviceCache = new RPCFunctionCache();
		deviceCache.setSlaveId(slaveId);
		deviceCache.setDeviceId(deviceId);
		deviceCache.setFunctionData(functionRepository.getDataFromSlave(deviceId, slaveId));

		return deviceCache;
	}

	@Override
	public RPCFunctionCache getCacheByFarm(String farmId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RPCFunctionCache getFunctionsBySlave(String deviceId, String slaveId, List<String> functionList) {
		logger.debug("getFunctionsBySlave requesting " + functionList.size() + " elements");
		for (String function : functionList)
			logger.debug(function);

		Map<String, Integer> functionData = new HashMap<>();
		for (String functionId : functionList) {
			Integer functionValue = functionRepository.getFunctionValue(deviceId, slaveId, functionId);
			functionData.put(functionId, functionValue);
		}

		RPCFunctionCache deviceCache = new RPCFunctionCache();
		deviceCache.setSlaveId(slaveId);
		deviceCache.setDeviceId(deviceId);
		deviceCache.setFunctionData(functionData);

		return deviceCache;
	}

	@Override
	public RPCFunctionCache getFunctionsByFarm(String farmId, List<String> functionList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Map<String, Integer>> getFunctionsOfChildrenByParent(Set<String> functionsId,
			LocalType childrenType, LocalType parentType, String parentId, String controlType) {
		return functionRepository.getFunctionsOfChildrenByParent(functionsId, childrenType, parentType, parentId,
				controlType);
	}

	@Override
	public Map<String, Map<String, Map<String, Integer>>> getFunctionsOfLocationList(
			RPCFunctionsLocationRequest functionsByLocations) {
		// TODO Auto-generated method stub
		logger.debug("calling the method getFunctionsOfLocationList");
		return functionRepository.getFunctionsOfLocationList(functionsByLocations);
	}

	@Override
	public Map<String, Map<String, Map<String, Long>>> getFunctionsOfLocationCumulative(List<FunctionsOfType> functions,
			LocalType parentType, List<String> parentId, LocalType childType) {
		return functionRepository.getFunctionsOfLocationCumulative(functions, parentType, parentId, childType);
	}

}
