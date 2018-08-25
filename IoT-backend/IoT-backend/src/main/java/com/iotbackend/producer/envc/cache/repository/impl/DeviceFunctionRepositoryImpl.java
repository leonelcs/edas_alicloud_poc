package com.iotbackend.producer.envc.cache.repository.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import com.iotbackend.producer.envc.cache.config.IntegerSerializer;
import com.iotbackend.commons.envc.dto.MappingJsonDto;
import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.commons.envc.realtime.dto.DeviceSlaveCache;
import com.iotbackend.commons.envc.service.dto.FunctionsOfType;
import com.iotbackend.commons.envc.service.dto.RPCFunctionsLocationRequest;
import com.iotbackend.producer.envc.cache.repository.DeviceFunctionRepository;
import com.iotbackend.producer.envc.messages.MqttMiddlewareClient;

/**
 * Implementation of the cache of a Device Based in put the simple string values
 * in compound key
 * 
 * @author lsilva
 *
 */
@Repository
public class DeviceFunctionRepositoryImpl implements DeviceFunctionRepository {

	private static final Logger logger = LoggerFactory.getLogger(DeviceFunctionRepositoryImpl.class);
	
	@Autowired
	MqttMiddlewareClient middlewareClient;

	/**
	 * basekey is dc, short name for Device Cache Teh whole key will be separated by
	 */
	private final String baseKEY = "dc";

	private final String separation = ":";

	private final String wildcard = "*";

	private final String empty = "";

	private RedisTemplate<String, Object> redisTemplate;

	private HashOperations<String, String, Integer> hashOperations;

	public DeviceFunctionRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
		RedisSerializer<Integer> serializer = new IntegerSerializer();
		redisTemplate.setHashValueSerializer(serializer);
		redisTemplate.setValueSerializer(serializer);
	}

	@PostConstruct
	private void init() {
		hashOperations = redisTemplate.opsForHash();
	}

	public StringBuffer createRedisKey(String deviceId, String slaveId, String controlType,
			MappingJsonDto slaveMapping) {
		StringBuffer buffer = new StringBuffer();

		// compound the key
		buffer.append(baseKEY).append(separation)
				.append((slaveMapping == null || slaveMapping.getAreaId() == null) ? empty : slaveMapping.getAreaId())
				.append(separation)
				.append((slaveMapping == null || slaveMapping.getBuildingId() == null) ? empty
						: slaveMapping.getBuildingId())
				.append(separation)
				.append((slaveMapping == null || slaveMapping.getFarmId() == null) ? empty : slaveMapping.getFarmId())
				.append(separation)
				.append((slaveMapping == null || slaveMapping.getDepartmentId() == null) ? empty
						: slaveMapping.getDepartmentId())
				.append(separation)
				.append((slaveMapping == null || slaveMapping.getCompartmentId() == null) ? empty
						: slaveMapping.getCompartmentId())
				.append(separation).append(controlType == null ? empty : controlType).append(separation)
				.append(deviceId).append(separation).append(slaveId);

		return buffer;
	}

	/**
	 * This method creates the key for consult the redis cache, when you send the
	 * null value the key will be turn the wildcard, for empty will keep empty.
	 * 
	 * @param deviceId
	 *            - device id, null for wildcard and empty for no value
	 * @param slaveId
	 *            - slave id, null for wildcard and empty for no value
	 * @param controlType
	 *            - control type, null for wildcard and empty for no value
	 * @param slaveMapping
	 *            areaId - field value to fill, null to wildcar, empty for no value
	 *            buildingId - field value to fill, null to wildcar, empty for no
	 *            value farmId - field value to fill, null to wildcar, empty for no
	 *            value departmentId - field value to fill, null to wildcar, empty
	 *            for no value CompartmentId - field value to fill, null to wildcar,
	 *            empty for no value
	 * @return
	 */
	private StringBuffer createRedisDeviceSearchKey(String deviceId, String slaveId, String controlType,
			MappingJsonDto slaveMapping) {
		StringBuffer buffer = new StringBuffer();

		// compond the key
		buffer.append(baseKEY).append(separation)
				.append((slaveMapping == null || slaveMapping.getAreaId() == null) ? wildcard
						: slaveMapping.getAreaId()) // areaId
				.append(separation)
				.append((slaveMapping == null || slaveMapping.getBuildingId() == null) ? wildcard
						: slaveMapping.getBuildingId()) // buildingId
				.append(separation)
				.append((slaveMapping == null || slaveMapping.getFarmId() == null) ? wildcard
						: slaveMapping.getFarmId()) // farmId
				.append(separation)
				.append((slaveMapping == null || slaveMapping.getDepartmentId() == null) ? wildcard
						: slaveMapping.getDepartmentId()) // departmentId
				.append(separation)
				.append((slaveMapping == null || slaveMapping.getCompartmentId() == null) ? wildcard
						: slaveMapping.getCompartmentId()) // compartmentId
				.append(separation).append((controlType == null) ? wildcard : controlType) // control type
				.append(separation).append((deviceId == null) ? wildcard : deviceId) // device id
				.append(separation).append((slaveId == null) ? wildcard : slaveId); // slave id

		return buffer;
	}
	
	public Optional<String> retrieveExistingKey(String deviceId, String slaveId) {
		StringBuffer buffer = createRedisDeviceSearchKey(deviceId, slaveId, null, null);
		
		Set<String> keySet = hashOperations.keys(buffer.toString());
		return keySet.stream().findFirst();
	}
	
	private MappingJsonDto getMappingFromKey(String key)
	{
		MappingJsonDto output = new MappingJsonDto();
		
		String[] parts = key.split(":");
		
		output.setAreaId(parts[1]);
		output.setBuildingId(parts[2]);
		output.setFarmId(parts[3]);
		output.setDepartmentId(parts[4]);
		output.setCompartmentId(parts[5]);
		
		return output;
	}
	
	private String getControlTypeFromKey(String key)
	{
		return key.split(":")[6];
	}
	
	@Override
	public List<Pair<String, MappingJsonDto>> getMappingsByDevice(String deviceId, String slaveId)
	{		
		try {
			String redisDeviceId = deviceId.replace(":", "");
			
			List<Pair<String, MappingJsonDto>> output = redisTemplate.keys(createRedisDeviceSearchKey(redisDeviceId, slaveId, null, null).toString()).stream().map(key -> {
				return Pair.of(getControlTypeFromKey(key), getMappingFromKey(key));
			}).collect(Collectors.toList());
			
			return output;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Failed to get mappings by deviced");
			return null;
		}
	}
	

	@Override
	public void saveDeviceCache(String deviceID, List<DeviceSlaveCache> slaveList) {

		slaveList.stream().forEach(slave -> {

			StringBuffer redisKey = createRedisKey(deviceID, slave.getSlaveId(), slave.getControlType(),
					slave.getMapping());
			hashOperations.putAll(redisKey.toString(), slave.getData());
		});
	}

	@Override
	public void updateSlaveDataValue(String deviceId, String slaveId, String functionId, Integer value) {

		StringBuffer redisKey = createRedisDeviceSearchKey(deviceId, slaveId, null, null);

		Optional<String> optKey = redisTemplate.keys(redisKey.toString()).stream().findFirst();

		try {
			hashOperations.put(optKey.get(), functionId, value);
		} catch (NoSuchElementException e) {
			logger.debug("Redis didn't find a suitable match for the search key");
		}


	}

	@Override
	public Integer getFunctionValue(String deviceId, String slaveId, String functionId) {

		StringBuffer redisKey = createRedisDeviceSearchKey(deviceId, slaveId, null, null);
		Set<String> deviceKey = redisTemplate.keys(redisKey.toString());

		if (deviceKey.isEmpty())
			return null;
		else
			return hashOperations.get(deviceKey.iterator().next(), functionId);

	}

	@Override
	public Map<String, Integer> getDataFromSlave(String deviceId, String slaveId) {

		StringBuffer redisKey = createRedisDeviceSearchKey(deviceId, slaveId, null, null);
		Set<String> deviceKey = redisTemplate.keys(redisKey.toString());

		if (deviceKey.isEmpty())
			return null;
		else
			return hashOperations.entries(deviceKey.iterator().next());

	}

	@Override
	public List<DeviceSlaveCache> getDeviceCache(String deviceId) {

		StringBuffer redisKey = createRedisDeviceSearchKey(deviceId, null, null, null);
		Set<String> keys = redisTemplate.keys(redisKey.toString());

		return keys.stream().map(key -> {
			String[] keyTokens = key.split(":");
			DeviceSlaveCache slaveCache = new DeviceSlaveCache();
			slaveCache.setSlaveId(keyTokens[keyTokens.length - 1]);
			slaveCache.setData(hashOperations.entries(key));
			return slaveCache;
		}).collect(Collectors.toList());
	}

	/**
	 * 
	 * @param functionsId
	 *            the set of the requested functions
	 * @param parentType
	 *            - level of the parent location, mind the screen you are
	 * @param parentId
	 *            - the Id of the location that will group the information, the id
	 *            of the screen you are showing
	 * @param childrenType
	 *            - The level of the information you need to show, mind a table with
	 *            the functions showed by department for example
	 * @param controlType
	 *            - the control type of the devices (Ventilation, Feed... )
	 * @return
	 */
	@Override
	public Map<String, Map<String, Integer>> getFunctionsOfChildrenByParent(Set<String> functionsId,
			LocalType childrenType, LocalType parentType, String parentId, String controlType) {
		try {
			String locationKey = createCustomKey(parentType, parentId, childrenType, controlType);
			Set<String> keys = hashOperations.keys(locationKey);
			Map<String, Map<String, Integer>> resultMap = new HashMap<>();
			for (String key : keys) {

				Map<String, Integer> map = functionsId.stream()
						.collect(Collectors.toMap(f -> f, f -> hashOperations.get(key, f)));
				String[] keyTokens = key.split(separation);
				resultMap.put(keyTokens[childrenType.getValue() + 1], map);
			}
			return resultMap;
		} catch (Exception e) {
			logger.error("All fields are mandatory, see message {}", e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public String createCustomKey(LocalType groupIdType, String groupId, LocalType location, String controlType)
			throws Exception {
		if (groupIdType == null || groupId == null || location == null || controlType == null)
			throw new Exception("All attributes of this method are mandatory");
		if (groupIdType.getValue() > location.getValue())
			throw new Exception("location type must be a sub category of the group by");
		StringBuffer buffer = new StringBuffer();
		buffer.append(baseKEY);
		buffer.append(separation);
		for (int i = 0; i < groupIdType.getValue(); i++) {
			buffer.append(wildcard);
			buffer.append(separation);
		}
		buffer.append(groupId);
		buffer.append(separation);
		if (groupIdType.getValue() < location.getValue())
			for (int i = groupIdType.getValue() + 1; i <= location.getValue(); i++) {
				buffer.append(wildcard);
				buffer.append(separation);
			}
		if (location.getValue() + 1 < 5)
			for (int i = location.getValue() + 1; i < 5; i++) {
				buffer.append(separation);
			}
		buffer.append(controlType);
		buffer.append(separation);
		buffer.append(wildcard); // deviceId
		buffer.append(separation);
		buffer.append(wildcard); // slaveId
		return buffer.toString();
	}

	public Map<String, Map<String, Map<String, Integer>>> getFunctionsOfLocationList(
			RPCFunctionsLocationRequest functionsByLocations) {
		logger.debug("The object {} arrived at repository", functionsByLocations);
		Map<String, Map<String, Map<String, Integer>>> resultByLocation = new HashMap<>();
		for (String localId : functionsByLocations.getLocalIds()) {
			logger.debug("local id {} of level {}", localId, functionsByLocations.getLocalType());
			Map<String, Map<String, Integer>> mapOfControlTypes = new HashMap<>();
			for (FunctionsOfType functionOfType : functionsByLocations.getFunctions()) {
				Map<String, Integer> mapOfFunctions = new HashMap<>();
				String templateKey = getKey(functionsByLocations.getLocalType(), localId,
						functionOfType.getControlType());
				logger.debug("got the key {}", templateKey);
				for (String function : functionOfType.getFunctionsIds()) {
					logger.debug("ask for function {}", function);
					Set<String> keys = redisTemplate.keys(templateKey);
					keys.forEach(key -> {
						Integer value = hashOperations.get(key, function);
						mapOfFunctions.put(function, value);
					});

				}
				mapOfControlTypes.put(functionOfType.getControlType(), mapOfFunctions);
			}
			resultByLocation.put(localId, mapOfControlTypes);
		}
		return resultByLocation;
	}

	public Map<String, Map<String, Map<String, Long>>> getFunctionsOfLocationCumulative(List<FunctionsOfType> functions, LocalType parentType, List<String> parentIds, LocalType childType) {
		
		
		logger.debug("Getting cumulative functions for " + parentType.toString() + " id: " + parentIds);
		
		Map<String, Map<String, Map<String, Long>>> resultOutput = new HashMap<>();
		
		parentIds.stream().forEach(parentId -> {
			Map<String, Map<String, Long>> output = new HashMap<>();
			functions.stream().forEach(item -> {
				try {
					
					String searchKey = createCustomKey(parentType, parentId, childType, item.getControlType());
					logger.debug("Searchkey: " + searchKey);
					
					
					RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
					ScanOptions scanOptions = ScanOptions.scanOptions().match(searchKey).count(1000).build();
					
					List<List<Integer>> functionResults = new ArrayList<>();
					try(Cursor<byte[]> c = connection.scan(scanOptions)) {
						while(c.hasNext()) {
							String key = new String(c.next(), "UTF-8");
							functionResults.add(hashOperations.multiGet(key, item.getFunctionsIds()));
						}
					}
					
					Map<String, Long> result = new HashMap<>();
					
					IntStream.range(0, item.getFunctionsIds().size()).forEachOrdered(i -> {
						long valueSum = functionResults.stream().mapToLong(v -> v.get(i)).sum();
						logger.debug(item.getFunctionsIds().get(i) + " has total value " + valueSum);
						result.put(item.getFunctionsIds().get(i), valueSum);
						
					});
					
					output.put(item.getControlType(), result);
					
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				};
				
			});	
			
			resultOutput.put(parentId, output);
		});
		
		return resultOutput;		
	}
	
	private String getKey(LocalType localType, String localId, String controlType) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(baseKEY);
		buffer.append(separation);
		for (int i = 0; i < localType.getValue(); i++) {
			buffer.append(wildcard);
			buffer.append(separation);
		}
		buffer.append(localId);
		buffer.append(separation);
		for (int i = localType.getValue() + 1; i < 5; i++) {
			buffer.append(empty);
			buffer.append(separation);
		}
		buffer.append(controlType);
		buffer.append(separation);
		buffer.append(wildcard);
		buffer.append(separation);
		buffer.append(wildcard);

		return buffer.toString();

	}

}
