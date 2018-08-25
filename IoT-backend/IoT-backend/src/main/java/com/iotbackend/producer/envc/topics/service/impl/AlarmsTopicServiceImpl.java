package com.iotbackend.producer.envc.topics.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.dozer.DozerBeanMapper;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iotbackend.commons.envc.dto.AlarmCacheJsonDto;
import com.iotbackend.commons.envc.dto.AlarmObjectJsonDto;
import com.iotbackend.commons.envc.helper.ValidatorConverterHelper;
import com.iotbackend.producer.envc.cache.domain.DeviceAlarmCache;
import com.iotbackend.producer.envc.cache.repository.DeviceAlarmRepository;
import com.iotbackend.producer.envc.messages.MqttDeviceClient;
import com.iotbackend.producer.envc.messages.MqttMiddlewareClient;
import com.iotbackend.producer.envc.messages.MqttTopics;
import com.iotbackend.producer.envc.topics.service.IAlarmsTopicService;


@Service
public class AlarmsTopicServiceImpl implements IAlarmsTopicService {

	private static final Logger logger = LoggerFactory.getLogger(AlarmsTopicServiceImpl.class);
	
	@Autowired
	Gson gson;
	
	@Autowired
	DozerBeanMapper mapper;

	@Autowired
	MqttDeviceClient deviceClient;

	@Autowired
	MqttMiddlewareClient middlewareClient;

	@Autowired
	DeviceAlarmRepository deviceAlarmRepository;

	private BiFunction<MqttMessage, String, Boolean> processCacheReadSignal = (m, t) -> {
		logger.debug("Message Received at cache read signal");
		String[] topicTokens = t.split("/");
		logger.debug("Receiving a message {} on topic {}", m.toString(), t);

		String deviceId = topicTokens[topicTokens.length-1];
		logger.debug("Received devide Id", deviceId);
		publishRequestToDeviceAlarmCache(deviceId);
		return true;
	};

	private BiFunction<MqttMessage, String, Boolean> processResponseDeviceCache = (m, t) -> {
		logger.debug("Receiving a message {} on topic {}", m.toString(), t);
		if (m == null || m.toString().isEmpty())
			return false;
		String[] topicTokens = t.split("/");
		String deviceId = ValidatorConverterHelper.cleanDeviceNumber(topicTokens[topicTokens.length - 1]);

		AlarmCacheJsonDto[] alarmsJson = null;
		try {
			logger.debug("Alarm message - {}", m.toString());
			alarmsJson = gson.fromJson(m.toString(), AlarmCacheJsonDto[].class);
		} catch (Exception exception) {
			logger.error("Parser exception, message {}", exception.getMessage());
			exception.printStackTrace();
			return false;
		}
		// no risk of null pointer because the exception on alarmDto stop the method
		logger.info("alarms Json: {}", alarmsJson.toString());
		List<AlarmCacheJsonDto> alarmCacheList = Arrays.asList(alarmsJson);
		alarmCacheList.stream()
		    .forEach(alarmDto -> {
		    	logger.info("alarms Json: {}", alarmDto.getData());
				List<AlarmObjectJsonDto> alarmObjectList = alarmDto.getData();
				// mappping to jpa cache entity
				if (alarmObjectList != null) {
					List<DeviceAlarmCache> listOfAlarmCache = alarmObjectList.stream().map(alarmObject -> {
						logger.info("alarm object: {}", alarmObject.toString());
						DeviceAlarmCache alarmCache = mapper.map(alarmObject, DeviceAlarmCache.class);
						logger.info("alarm cache: {}", alarmCache.toString());
						return alarmCache;
					}).collect(Collectors.toList());

					deviceAlarmRepository.saveDeviceAlarmCache(deviceId, alarmDto.getSlaveId(), alarmDto.getControlType(),
							alarmDto.getMapping(), listOfAlarmCache);
					logger.debug("Alarm cache successfuly saved");
				}
		    });

		return true;

	};

	private BiFunction<MqttMessage, String, Boolean> processIndividualAlarmChange = (m, t) -> {
		logger.debug("Receiving a message {} on topic {}", m.toString(), t);
		if (m == null || m.toString().isEmpty())
			return false;
		String[] topicTokens = t.split("/");
		String deviceId = ValidatorConverterHelper.cleanDeviceNumber(topicTokens[3]);
		String slaveId = topicTokens[4];

		if (topicTokens.length != 6) {
			logger.error("The topic is not correct, the correct is {} and it is {}",
					"Argos/Alarm/Change/<DeviceId>/<SlaveId>/<AlarmId>", t);
			return false;
		}

		String value = m.toString();
		try {
			DeviceAlarmCache alarm = gson.fromJson(value, DeviceAlarmCache.class);
			deviceAlarmRepository.saveOrUpdateSingleAlarm(deviceId, slaveId, null, null, alarm);
			logger.debug("The alarm saving finished successfuly");
			return true;
		} catch (Exception e) {
			logger.error("Parser exception, message {}", e.getMessage());
			e.printStackTrace();
			return false;
		}
	};

	@PostConstruct
	@Override
	public void startAllSubscriptions() {
		 subscribeToAlarmChanges();
		 subscribeToDeviceAlarmCacheReadySignal();
		 subscribeToResponseDeviceAlarmCache();
		 publishPingToDeviceAlarmCache();
	}

	@Override
	public void subscribeToDeviceAlarmCacheReadySignal() {
		logger.info("Subscribing to {}", MqttTopics.ARGOS_ALARM_CACHE_READY_ID);
		try {
			deviceClient.subscribe(processCacheReadSignal, MqttTopics.ARGOS_ALARM_CACHE_READY_ID);
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to subscribe, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	@Scheduled(fixedDelayString = "${argos.cache.pingMilliseconds}")
	public void publishPingToDeviceAlarmCache() {
		logger.info("Publish a 'ping' request on topic {}", MqttTopics.ARGOS_ALARM_CACHE_READY_PING);
		try {
			deviceClient.publish(MqttTopics.ARGOS_ALARM_CACHE_READY_PING, new MqttMessage("".getBytes()));
		} catch (MqttException e) {
			logger.error("MqttClient faced problems to publish on topic {}",
					MqttTopics.ARGOS_ALARM_CACHE_READY_PING);
			e.printStackTrace();
		}

	}

	@Override
	public void publishRequestToDeviceAlarmCache(String deviceId) {
		logger.debug("Publish a 'get' request on topic {}",
				MqttTopics.ARGOS_ALARM_CACHE_GET_ID + "/" + deviceId);

		try {
			deviceClient.publish(MqttTopics.ARGOS_ALARM_CACHE_GET_ID + "/" + deviceId,
					new MqttMessage("".getBytes()));
		} catch (MqttException e) {
			logger.error("MqttClient faced problems to publish on topic {}",
					MqttTopics.ARGOS_ALARM_CACHE_GET_ID + "/" + deviceId);
			e.printStackTrace();
		}
	}

	@Override
	public void subscribeToResponseDeviceAlarmCache() {
		logger.info("Subscribing to {}", MqttTopics.ARGOS_ALARM_CACHE_RESPONSE_ID);
		try {
			deviceClient.subscribe(processResponseDeviceCache, MqttTopics.ARGOS_ALARM_CACHE_RESPONSE_ID);
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to subscribe, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void subscribeToAlarmChanges() {
		logger.info("Subscribing to {}", MqttTopics.ARGOS_ALARM_CHANGE_DEVICEID_SLAVEID);
		try {
			deviceClient.subscribe(processIndividualAlarmChange, MqttTopics.ARGOS_ALARM_CHANGE_DEVICEID_SLAVEID);
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to subscribe, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}

	}

}
