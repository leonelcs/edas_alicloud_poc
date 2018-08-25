package com.iotbackend.producer.envc.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.boot.hsf.annotation.HSFProvider;
import com.iotbackend.commons.envc.dto.AlarmObjectJsonDto;
import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.commons.envc.service.AlarmRPCService;
import com.iotbackend.producer.envc.cache.domain.DeviceAlarmCache;
import com.iotbackend.producer.envc.cache.repository.DeviceAlarmRepository;

@HSFProvider(serviceInterface = AlarmRPCService.class, serviceVersion = "1.0.0")
public class AlarmRPCServiceImpl implements AlarmRPCService {

	private static final Logger logger = LoggerFactory.getLogger(AlarmRPCServiceImpl.class);
	
	@Autowired
	private DeviceAlarmRepository repository; 
	
	
	@Autowired
	private DozerBeanMapper mapper;

	@Override
	public Map<String, Map<String, Long>> getAlarmsOfLocationCumulative(LocalType localType, List<String> localIds) {
		Map<String, Map<String, Long>> resultMap = repository.getAlarmsOfLocationCumulative(localType, localIds);
		logger.debug("Result map being returned - {}", resultMap.toString());
		return resultMap;
	}

	@Override
	public Map<String, Map<String, Map<String, AlarmObjectJsonDto>>> findAlarmsByLocationList(LocalType localType,
			List<String> localIds) {
		Map<String,Map<String,Map<String, AlarmObjectJsonDto>>> mapByLocationDTO = new HashMap<>();
		logger.debug("receiving type {} and local ids {}", localType, localIds.toString());
		Map<String,Map<String,Map<String, DeviceAlarmCache>>> mapByLocationCache = repository.findAlarmsByLocationList(localType, localIds);
		logger.debug("The map got retrieved by the repository, see: {}", mapByLocationCache.toString());

		mapByLocationCache.entrySet()
			.forEach( location -> {
						logger.debug("iterate location");
						Map<String, Map<String, AlarmObjectJsonDto>> mapByControlType = new HashMap<>();
						location.getValue().entrySet()
							.stream()
							.forEach( controlType -> {
								logger.debug("iterate controlType");
								Map<String, AlarmObjectJsonDto> alarmMap = new HashMap<>();
								controlType.getValue().entrySet()
									.stream()
									.forEach( alarm -> {
										logger.debug("iterate function");
										DeviceAlarmCache deviceCache = alarm.getValue();
										logger.debug("Alarm {}", deviceCache.toString());
										AlarmObjectJsonDto alarmDto = mapper.map(deviceCache, AlarmObjectJsonDto.class);
										alarmMap.put(alarm.getKey(), alarmDto);
									});
								logger.debug("Map by alarm - {}", alarmMap);
								mapByControlType.put(controlType.getKey(), alarmMap);
							});
						logger.debug("Map by controlType - {}", mapByControlType);
						mapByLocationDTO.put(location.getKey(), mapByControlType);
						
					}
					);
		logger.debug("Result map - {}", mapByLocationDTO.toString());
		
		return mapByLocationDTO;
	}
	
	
	
	
}
