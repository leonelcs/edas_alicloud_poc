package com.iotbackend.commons.envc.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.commons.envc.service.dto.FunctionsOfType;
import com.iotbackend.commons.envc.service.dto.RPCFunctionCache;
import com.iotbackend.commons.envc.service.dto.RPCFunctionsLocationRequest;

public interface FunctionsRPCService {
	
	RPCFunctionCache getCacheBySlave(String deviceId, String slaveId);
	
	RPCFunctionCache getCacheByFarm(String farmId);
	
	RPCFunctionCache getFunctionsBySlave(String deviceId, String slaveId, List<String> functionList);
	
	RPCFunctionCache getFunctionsByFarm(String farmId, List<String> functionList);
	
	Map<String, Map<String, Integer>> getFunctionsOfChildrenByParent(Set<String> functionsId, LocalType childrenType, LocalType parentType, String parentId, String controlType);
	
	Map<String, Map<String, Map<String, Integer>>> getFunctionsOfLocationList(RPCFunctionsLocationRequest functionsByLocations);
	
	Map<String, Map<String, Map<String, Long>>> getFunctionsOfLocationCumulative(List<FunctionsOfType> functions, LocalType parentType, List<String> parentId, LocalType childType);

}
