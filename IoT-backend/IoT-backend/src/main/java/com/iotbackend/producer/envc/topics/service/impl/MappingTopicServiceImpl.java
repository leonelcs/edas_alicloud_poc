package com.iotbackend.producer.envc.topics.service.impl;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.iotbackend.commons.envc.dto.MappingJsonDto;
import com.iotbackend.producer.envc.messages.MqttDeviceClient;
import com.iotbackend.producer.envc.messages.MqttTopics;
import com.iotbackend.producer.envc.topics.service.IMappingTopicService;

public class MappingTopicServiceImpl implements IMappingTopicService {
	
	private static final Logger logger = LoggerFactory.getLogger(MappingTopicServiceImpl.class);
	
	@Autowired
	Gson gson;
	
	@Autowired
	MqttDeviceClient deviceClient;

	@Override
	public void publishCreateModifyMapping(MappingJsonDto dto) {
		logger.info("Publishing to {}", MqttTopics.ARGOS_MAPPING_MODIFY_ADD_DEVICEID);
		String json = gson.toJson(dto);
		try {
			deviceClient.publish(MqttTopics.ARGOS_MAPPING_MODIFY_ADD_DEVICEID, new MqttMessage(json.getBytes()));
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to publish, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void publishDeleteMapping(MappingJsonDto dto) {
		logger.info("Publishing to {}", MqttTopics.ARGOS_MAPPING_MODIFY_REMOVE_DEVICEID);
		String json = gson.toJson(dto);
		try {
			deviceClient.publish(MqttTopics.ARGOS_MAPPING_MODIFY_REMOVE_DEVICEID, new MqttMessage(json.getBytes()));
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to publish, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}
	}

}
