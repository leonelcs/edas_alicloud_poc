package com.iotbackend.producer.envc.cache.repository.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;

import com.iotbackend.commons.envc.dto.AlarmStatus;
import com.iotbackend.commons.envc.dto.MappingJsonDto;
import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.producer.envc.cache.domain.DeviceAlarmCache;
import com.iotbackend.producer.envc.cache.repository.DeviceAlarmRepository;

/**
 * Implementation of the cache of a Device Alarm
 * Based in put the simple string values in compound key 
 * @author lsilva
 *
 */
@Repository
public class DeviceAlarmRepositoryImpl implements DeviceAlarmRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(DeviceAlarmRepositoryImpl.class);
	
	/**
	 * basekey is da, short name for Device Alarm
	 * The whole key will be separated by : 
	 */
	private final String baseKEY = "da";
	
	private final String separation = ":";
	
	private final String wildcard = "*";
	
	private final String empty = "";
	
	private RedisTemplate<String, Object> redisTemplate;
	
	private HashOperations<String, String, DeviceAlarmCache> hashOperations;
	
	public DeviceAlarmRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
		this.redisTemplate.setHashValueSerializer(serializer);
		this.redisTemplate.setValueSerializer(serializer);
		this.redisTemplate.getKeySerializer();
	}
	
	@PostConstruct
	private void init() {
		hashOperations = redisTemplate.opsForHash();
	}
	
	private StringBuffer createRedisKey(String deviceId, String slaveId, String controlType, MappingJsonDto slaveMapping) {
		StringBuffer buffer = new StringBuffer();
		
		//compound the key
		buffer.append(baseKEY)
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getAreaId() == null ) ? empty : slaveMapping.getAreaId())
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getBuildingId() == null ) ? empty : slaveMapping.getBuildingId())
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getFarmId() == null ) ? empty : slaveMapping.getFarmId())
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getDepartmentId() == null ) ? empty : slaveMapping.getDepartmentId())
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getCompartmentId() == null ) ? empty : slaveMapping.getCompartmentId())
			.append(separation)
			.append(controlType == null ? empty : controlType)
			.append(separation)
			.append(deviceId)
			.append(separation)
			.append(slaveId);
		
		return buffer;
	}
	
	/**
	 * This method creates the key for consult the redis cache, when you send the null value the key will be turn the wildcard, for empty will keep empty.
	 * @param deviceId - device id, null for wildcard and empty for no value
	 * @param slaveId - slave id, null for wildcard and empty for no value
	 * @param controlType - control type, null for wildcard and empty for no value
	 * @param slaveMapping
	 * 			areaId - field value to fill, null to wildcar, empty for no value
	 * 			buildingId - field value to fill, null to wildcar, empty for no value
	 *          farmId - field value to fill, null to wildcar, empty for no value
	 *          departmentId - field value to fill, null to wildcar, empty for no value
	 *          CompartmentId - field value to fill, null to wildcar, empty for no value
	 * @return
	 */
	private StringBuffer createRedisDeviceSearchKey(String deviceId, String slaveId, String controlType, MappingJsonDto slaveMapping) {
		StringBuffer buffer = new StringBuffer();
		
		//compound the key
		buffer.append(baseKEY)
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getAreaId() ==null ) ? wildcard : slaveMapping.getAreaId() ) //areaId
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getBuildingId() ==null ) ? wildcard : slaveMapping.getBuildingId() ) //buildingId
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getFarmId() ==null ) ? wildcard : slaveMapping.getFarmId() ) //farmId
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getDepartmentId() ==null ) ? wildcard : slaveMapping.getDepartmentId() ) // departmentId
			.append(separation)
			.append((slaveMapping == null || slaveMapping.getCompartmentId() ==null ) ? wildcard : slaveMapping.getCompartmentId() ) // compartmentId
			.append(separation)
			.append((controlType ==null) ? wildcard : controlType) //control type
			.append(separation)
			.append((deviceId ==null) ? wildcard : deviceId) //device id
			.append(separation)
			.append((slaveId ==null) ? wildcard : slaveId); //slave id
		
		return buffer;
	}

	@Override
	public void saveDeviceAlarmCache(String deviceId, String slaveId, String controlType, MappingJsonDto mappingDto, List<DeviceAlarmCache> listOfAlarm) {
		logger.debug("list of alarms = {}", listOfAlarm.toString());
		StringBuffer key = createRedisKey(deviceId, slaveId, controlType, mappingDto);
		logger.debug("Alarm Key = {}", key.toString());

		Map<String, DeviceAlarmCache> oldCache = hashOperations.entries(key.toString());
		if (oldCache!=null && !oldCache.isEmpty()) {
			logger.info("Cleaning cache before saving");
			List<String> hashKeys = oldCache.entrySet().stream().map( m -> m.getKey() ).collect(Collectors.toList());
			Long dataDeleted = hashOperations.delete(key.toString(), hashKeys.toArray());
			logger.info("{} alarm(s) was/were deleted from the cache", dataDeleted);			
		}

		listOfAlarm.stream().forEach( alarm -> {
				logger.debug("Saving alarm data", alarm.toString());
				hashOperations.put(key.toString(), alarm.getAlarmId(), alarm);
			} );
	}

	@Override
	public void saveOrUpdateSingleAlarm(String deviceId, String slaveId, String controlType, MappingJsonDto mappingDto,
			DeviceAlarmCache alarm) {
		StringBuffer templateKey = createRedisDeviceSearchKey(deviceId, slaveId, controlType, mappingDto);
		Set<String> keys = hashOperations.keys(templateKey.toString());
		Optional<String> optKey = keys.stream().findFirst();
		
		if (optKey.isPresent()) {
			String key = optKey.get();
			DeviceAlarmCache deviceAlarm = hashOperations.get(key.toString(), alarm.getAlarmId());
			if (deviceAlarm!=null) {
				if (alarm.getActive().equals(AlarmStatus.DELETE.getValue())) {
					hashOperations.delete(key.toString(), alarm.getAlarmId());
					logger.info("The alarm {} was deleted", alarm.getAlarmId());
					return;
				}
			}
			hashOperations.put(key.toString(), alarm.getAlarmId(), alarm);
		} else {
			StringBuffer key = createRedisKey(deviceId, slaveId, controlType, mappingDto);
			hashOperations.put(key.toString(), alarm.getAlarmId(), alarm);
		}

	}
	
	@Override
	public Map<String, Map<String, Map<String, DeviceAlarmCache>>> findAlarmsByLocationList(LocalType localType, List<String> localIds) {
		logger.info("findAlarmsByLocationList(LocalType localType, List<String> localIds) called");
		Map<String, Map<String, Map<String, DeviceAlarmCache>>> mapByLocation = new HashMap<>();

		localIds.stream()
		.forEach( localId -> {
			String templateKey = generateTemplateKey(localType, localId);
			logger.info("This is the key template {}", templateKey);
			
			RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
			ScanOptions scanOptions = ScanOptions.scanOptions().match(templateKey).count(1000).build();
			logger.info("scanOptions {}", scanOptions.toString());
			Set<String> keys = hashOperations.keys(templateKey);
			logger.info("Number of keys {}", keys.size());

			Map<String, Map<String, DeviceAlarmCache>> mapByControlType = new HashMap<>();
			try(Cursor<byte[]> c = connection.scan(scanOptions)) {
				while(c.hasNext()) {
					String key = new String(c.next(), "UTF-8");
					String[] keyTokens = key.split(":");
					String controlType = keyTokens[6];
					logger.info("Key used in the entries command: {}", key);
					Map<String, DeviceAlarmCache> auxMap = hashOperations.entries(key);
					logger.info("Map return in the entries command: {}", auxMap.toString());
					if (!mapByControlType.containsKey(controlType) && auxMap != null) {
						mapByControlType.put(controlType, auxMap);
					} else {
						Map<String, DeviceAlarmCache> existingMap = mapByControlType.get(controlType);
						existingMap.putAll(auxMap);
						mapByControlType.put(controlType, existingMap);
					}
				}
				mapByLocation.put(localId, mapByControlType);
			} catch (IOException e) {
				logger.error("the scan key command throwed a excpetion, see the message: ", e.getMessage());
				e.printStackTrace();
			}
		
		});
		return mapByLocation;
	}
	
	public String generateTemplateKeyDirectAlarms(LocalType localType, String localId) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(baseKEY);
		buffer.append(separation);
		for (int i=0; i < localType.getValue(); i++) {
			buffer.append(wildcard);
			buffer.append(separation);
		}
		buffer.append(localId);
		buffer.append(separation);
		for (int i=localType.getValue()+1; i < 5; i++) {
			buffer.append(empty);
			buffer.append(separation);
		}
		buffer.append(wildcard); //controlType
		buffer.append(separation);
		buffer.append(wildcard); //deviceId
		buffer.append(separation);
		buffer.append(wildcard); //slaveId
		return buffer.toString();
	}
	
	@Override
	public Map<String, Map<String, Long>> getAlarmsOfLocationCumulative(LocalType localType, List<String> localIds) {
		logger.debug("Starting method getAlarmsOfLocationCumulative to type");
		Map<String, Map<String, Long>> mapFunctionSum = new HashMap<>();
		localIds.stream()
			.forEach( localId -> {
				logger.debug("Local Id {}", localId);
				String templateKey = generateTemplateKey(localType, localId);
				
				RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
				ScanOptions scanOptions = ScanOptions.scanOptions().match(templateKey).count(1000).build();
				Map<String,Long> mapByControlType = new HashMap<>();
				try(Cursor<byte[]> c = connection.scan(scanOptions)) {
					while(c.hasNext()) {
						String key = new String(c.next(), "UTF-8");
						String[] keyTokens = key.split(":");
						logger.debug("Key {}", key);
						String controlType = keyTokens[6];
						logger.debug("Control Type {}", controlType);
						Long sumAux = hashOperations.size(key).longValue();
						logger.debug("number os hashs {}", sumAux);
						if (mapByControlType.containsKey(controlType)) {
							Long sum = mapByControlType.get(controlType);
							
							sum += sumAux;
							mapByControlType.put(controlType, sum);
						} else {
							mapByControlType.put(controlType, sumAux );
						}

					}
					mapFunctionSum.put(localId, mapByControlType);
				} catch (IOException e) {
					logger.error("the scan key command throwed a excpetion, see the message: ", e.getMessage());
					e.printStackTrace();
				}
			
			});
		logger.info("Map {}", mapFunctionSum.toString());
		return mapFunctionSum;
	}
	
	
	
	public String generateTemplateKey(LocalType localType, String localId) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(baseKEY);
		buffer.append(separation);
		logger.info("number of local typing {} ", localType.getValue());
		for (int i=0; i < localType.getValue(); i++) {
			buffer.append(wildcard);
			buffer.append(separation);
		}
		buffer.append(localId);
		buffer.append(separation);
		for (int i=localType.getValue()+1; i < 7; i++) {
			buffer.append(wildcard);
			buffer.append(separation);
		}
		buffer.append(wildcard);
		return buffer.toString();
	}

}
