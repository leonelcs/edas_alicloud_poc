package com.iotbackend.producer.envc.topics.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iotbackend.commons.envc.dto.MappingJsonDto;
import com.iotbackend.commons.envc.helper.ValidatorConverterHelper;
import com.iotbackend.commons.envc.realtime.dto.DeviceSlaveCache;
import com.iotbackend.producer.envc.cache.repository.DeviceFunctionRepository;
import com.iotbackend.producer.envc.messages.MqttDeviceClient;
import com.iotbackend.producer.envc.messages.MqttMiddlewareClient;
import com.iotbackend.producer.envc.messages.MqttTopics;
import com.iotbackend.producer.envc.topics.service.IFunctionsTopicService;

@Service
public class FunctionsTopicServiceImpl implements IFunctionsTopicService {

	private static final Logger logger = LoggerFactory.getLogger(FunctionsTopicServiceImpl.class);

	@Autowired
	MqttDeviceClient deviceClient;

	@Autowired
	MqttMiddlewareClient middlewareClient;
	
	@Autowired
	Gson gson;

	@Autowired
	DeviceFunctionRepository deviceFunctionRepository;

	private BiFunction<MqttMessage, String, Boolean> processCacheReadSignal = (m, t) -> {
		logger.info("Receiving a message {} on topic {}", m.toString(), t);
		String[] topicTokens = t.split("/");

		logger.debug("Received the device id {}", topicTokens.length - 1);
		String deviceId = topicTokens[topicTokens.length - 1];

		publishRequestToDeviceCache(deviceId);
		return true;
	};

	private BiFunction<MqttMessage, String, Boolean> processResponseDeviceCache = (m, t) -> {

		logger.debug("Receiving the cache message {} on topic {}", m.toString(), t);
		if (m == null || m.toString().isEmpty())
			return false;
		String[] topicTokens = t.split("/");
		String deviceId = ValidatorConverterHelper.cleanDeviceNumber(topicTokens[topicTokens.length - 1]);
		logger.debug("The device Id {} was changed to save on redis", deviceId);

		DeviceSlaveCache[] deviceSlaveArray;
		try {
			deviceSlaveArray = gson.fromJson(m.toString(), DeviceSlaveCache[].class);
			if (deviceSlaveArray.length > 0) {
				final List<DeviceSlaveCache> deviceSlaveList = Arrays.asList(deviceSlaveArray);

				deviceFunctionRepository.saveDeviceCache(deviceId, deviceSlaveList);
				//Publishing in mqtt in another thread to not block the redis persistence
				Thread thread = new Thread(new Runnable() {

					List<DeviceSlaveCache> p = deviceSlaveList;
				       public void run() { 
				    	   logger.debug("Thread initialization to update mqtt topic at middleware - ");
				         p.stream().forEach( slave ->
				         	publishDeviceFunctionUpdate(deviceId, slave)	 );
				       }
				});
				thread.start();
				return true;
			} else {
				logger.info("Empty message");
				return false;
			}

		} catch (Exception e) {
			logger.error("Parser exception, message: {}", e.getMessage());
			e.printStackTrace();
			return false;
		}

	};

	private BiFunction<MqttMessage, String, Boolean> processIndividualFunctionChange = (m, t) -> {
		if (m == null || m.toString().isEmpty())
			return false;
		String[] topicTokens = t.split("/");
		String deviceId = ValidatorConverterHelper.cleanDeviceNumber(topicTokens[3]);
		if (topicTokens.length != 6) {
			logger.error("The topic is not correct, the correct is {} and it is {}",
					"Argos/Function/Change/<DeviceId>/<SlaveId>/<FunctionID>", t);
			return false;
		}
		logger.info("Receiving a message {} on topic {}", m.toString(), t);
		String value = m.toString();
		Integer intValue = Integer.parseInt(value);
		publishDeviceIndividualFunctionUpdate(topicTokens[3], topicTokens[4], topicTokens[5], value);
		deviceFunctionRepository.updateSlaveDataValue(deviceId, topicTokens[4], topicTokens[5], intValue);
		return true;
	};
	
	private String createMQttTopic(String deviceId, String slaveId, String controlType,
			MappingJsonDto slaveMapping, String function) {
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("EC/Function/Update/");
		buffer.append(slaveMapping.getAreaId()).append("/");	// areaId
		buffer.append(slaveMapping.getBuildingId()).append("/");	// buildingId
		buffer.append(slaveMapping.getFarmId()).append("/");	// farmId
		buffer.append(slaveMapping.getDepartmentId()).append("/");	// departmentId
		buffer.append(slaveMapping.getCompartmentId()).append("/");	// compartmentId
		buffer.append(controlType).append("/");	// controlType
		buffer.append(deviceId).append("/");	// deviceId
		buffer.append(slaveId).append("/");	// slaveId
		buffer.append(function); // function
		return buffer.toString();
	}
	
	private String createMQttTopic(String deviceId, String slaveId, String controlType,
			MappingJsonDto slaveMapping) {
		
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("EC/Function/MultiUpdate/");
		buffer.append(slaveMapping.getAreaId()).append("/");	// areaId
		buffer.append(slaveMapping.getBuildingId()).append("/");	// buildingId
		buffer.append(slaveMapping.getFarmId()).append("/");	// farmId
		buffer.append(slaveMapping.getDepartmentId()).append("/");	// departmentId
		buffer.append(slaveMapping.getCompartmentId()).append("/");	// compartmentId
		buffer.append(controlType).append("/");	// controlType
		buffer.append(deviceId).append("/");	// deviceId
		buffer.append(slaveId);	// slaveId
		return buffer.toString();
	}
	
	private void publishDeviceIndividualFunctionUpdate(String deviceId, String slaveId, String function, String value)
	{		
		deviceFunctionRepository.getMappingsByDevice(deviceId, slaveId).stream().forEach(mapping -> {
			
			String topic = createMQttTopic(deviceId, slaveId, mapping.getFirst(), mapping.getSecond(), function);			
			try {
				middlewareClient.publish(topic, new MqttMessage(value.getBytes()));
			} catch (MqttException e) {
				logger.error("Mqtt Exception when publishing Function Update to Middleware, message: {}", e.getMessage());
				e.printStackTrace();
			}
		});
	}
	
	private Boolean publishDeviceFunctionUpdate(String deviceId, DeviceSlaveCache deviceSlaveCache)
	{
		String key = createMQttTopic(deviceId, deviceSlaveCache.getSlaveId(), deviceSlaveCache.getControlType(),
				deviceSlaveCache.getMapping());
		String json = gson.toJson(deviceSlaveCache);
		logger.debug("message - {}", json);
		try {
			middlewareClient.publish(key, new MqttMessage(json.getBytes()));
			return true;
		} catch (MqttException e) {
			logger.debug("Error during publishing - {}", e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	@PostConstruct
	@Override
	public void startAllSubscriptions() {
		subscribeToDeviceCacheReadSignal();
		subscribeToResponseDeviceCache();
		subscribeToFunctionChanges();
		publishPingToDeviceCache();
	}

	@Override
	public void subscribeToDeviceCacheReadSignal() {
		logger.info("Subscribing to {}", MqttTopics.ARGOS_FUNCTION_CACHE_READY_ID);
		try {
			deviceClient.subscribe(processCacheReadSignal, MqttTopics.ARGOS_FUNCTION_CACHE_READY_ID);
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to subscribe, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	@Scheduled(fixedDelayString = "${argos.cache.pingMilliseconds}")
	public void publishPingToDeviceCache() {
		logger.info("Publish a 'get' request on topic {}", MqttTopics.ARGOS_FUNCTION_CACHE_READY_PING);
		try {
			deviceClient.publish(MqttTopics.ARGOS_FUNCTION_CACHE_READY_PING, new MqttMessage("".getBytes()));
		} catch (MqttException e) {
			logger.error("MqttClient faced problems to publish on topic {}",
					MqttTopics.ARGOS_FUNCTION_CACHE_READY_PING);
			e.printStackTrace();
		}
	}

	@Override
	public void publishRequestToDeviceCache(String deviceId) {

		logger.debug("Publish a 'get' request on topic {}", MqttTopics.ARGOS_FUNCTION_CACHE_GET_ID + "/" + deviceId);

		try {
			deviceClient.publish(MqttTopics.ARGOS_FUNCTION_CACHE_GET_ID + "/" + deviceId,
					new MqttMessage("".getBytes()));
		} catch (MqttException e) {
			logger.error("MqttClient faced problems to publish on topic {}",
					MqttTopics.ARGOS_FUNCTION_CACHE_GET_ID + "/" + deviceId);
			e.printStackTrace();
		}
	}

	@Override
	public void subscribeToResponseDeviceCache() {
		logger.info("Subscribing to {}", MqttTopics.ARGOS_FUNCTION_CACHE_RESPONSE_ID);
		try {
			deviceClient.subscribe(processResponseDeviceCache, MqttTopics.ARGOS_FUNCTION_CACHE_RESPONSE_ID);
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to subscribe, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void subscribeToFunctionChanges() {
		logger.info("Subscribing to {}", MqttTopics.ARGOS_FUNCTION_CHANGE_DEVICEID_SLAVEID_FUNCTIONID);
		try {
			deviceClient.subscribe(processIndividualFunctionChange,
					MqttTopics.ARGOS_FUNCTION_CHANGE_DEVICEID_SLAVEID_FUNCTIONID);
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to subscribe, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}
	}

}
