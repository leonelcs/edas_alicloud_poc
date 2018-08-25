package com.iotbackend.commons.envc.service;

import java.util.List;
import java.util.Map;

import com.iotbackend.commons.envc.dto.AlarmObjectJsonDto;
import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;

public interface AlarmRPCService {
	
	public Map<String, Map<String, Long>> getAlarmsOfLocationCumulative(LocalType localType, List<String> localIds);
	
	public Map<String, Map<String, Map<String, AlarmObjectJsonDto>>> findAlarmsByLocationList(LocalType localType, List<String> localIds);

}
