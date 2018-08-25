package com.iotbackend.producer.envc.cache.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.util.Pair;

import com.iotbackend.commons.envc.dto.MappingJsonDto;
import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.commons.envc.realtime.dto.DeviceSlaveCache;
import com.iotbackend.commons.envc.service.dto.FunctionsOfType;
import com.iotbackend.commons.envc.service.dto.RPCFunctionsLocationRequest;

public interface DeviceFunctionRepository {
	
	public void saveDeviceCache(String deviceID, List<DeviceSlaveCache> slaveList);

	public void updateSlaveDataValue(String deviceID, String slaveId, String functionId, Integer valueId);

	public Integer getFunctionValue(String deviceId, String slaveId, String functionId);

	public Map<String, Integer> getDataFromSlave(String deviceId, String slaveId);

	public List<DeviceSlaveCache> getDeviceCache(String deviceId);
	
	//Location functions
	public Map<String, Map<String, Integer>> getFunctionsOfChildrenByParent(Set<String> functionsId, LocalType childrenType, LocalType parentType, String parentId, String controlType);
	
	public Map<String, Map<String, Map<String, Integer>>> getFunctionsOfLocationList(RPCFunctionsLocationRequest functionsByLocations);
	
	public Map<String, Map<String, Map<String, Long>>> getFunctionsOfLocationCumulative(List<FunctionsOfType> functions, LocalType parentType, List<String> parentIds, LocalType childType);
	
	//key functions
	public Optional<String> retrieveExistingKey(String deviceId, String slaveId);
	
	public StringBuffer createRedisKey(String deviceId, String slaveId, String controlType,
			MappingJsonDto slaveMapping);
	
	public List<Pair<String,MappingJsonDto>> getMappingsByDevice(String deviceId, String slaveId);

}
