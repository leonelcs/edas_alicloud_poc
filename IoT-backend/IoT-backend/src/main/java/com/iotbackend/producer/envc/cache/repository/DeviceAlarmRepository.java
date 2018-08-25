package com.iotbackend.producer.envc.cache.repository;

import java.util.List;
import java.util.Map;

import com.iotbackend.commons.envc.dto.MappingJsonDto;
import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.producer.envc.cache.domain.DeviceAlarmCache;

public interface DeviceAlarmRepository {
	
	public void saveDeviceAlarmCache(String deviceId, String slaveId, String controlType, MappingJsonDto mappingDto, List<DeviceAlarmCache> listOfAlarm);

	public void saveOrUpdateSingleAlarm(String deviceId, String slaveId, String controlType, MappingJsonDto mappingDto, DeviceAlarmCache alarm);
	
	public Map<String, Map<String, Long>> getAlarmsOfLocationCumulative(LocalType localType, List<String> localIds);
	
	public Map<String, Map<String, Map<String, DeviceAlarmCache>>> findAlarmsByLocationList(LocalType localType, List<String> localIds);

}
