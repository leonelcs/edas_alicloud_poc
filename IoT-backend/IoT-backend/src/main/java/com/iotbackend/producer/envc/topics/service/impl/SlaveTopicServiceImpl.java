package com.iotbackend.producer.envc.topics.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import javax.annotation.PostConstruct;

import org.dozer.DozerBeanMapper;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.iotbackend.commons.envc.dto.MappingJsonDto;
import com.iotbackend.commons.envc.dto.SlaveCacheJsonDto;
import com.iotbackend.producer.envc.domain.ArgosDeviceEntity;
import com.iotbackend.producer.envc.domain.ArgosSlaveEntity;
import com.iotbackend.producer.envc.domain.MappingVO;
import com.iotbackend.producer.envc.domain.SlaveId;
import com.iotbackend.producer.envc.domain.repository.ArgosDeviceRepository;
import com.iotbackend.producer.envc.domain.repository.ArgosSlaveRepository;
import com.iotbackend.producer.envc.messages.MqttDeviceClient;
import com.iotbackend.producer.envc.messages.MqttMiddlewareClient;
import com.iotbackend.producer.envc.messages.MqttTopics;
import com.iotbackend.producer.envc.topics.service.ISlaveTopicService;

@Service
public class SlaveTopicServiceImpl implements ISlaveTopicService {

	private static final Logger logger = LoggerFactory.getLogger(SlaveTopicServiceImpl.class);
	
	@Autowired
	Gson gson;
	
	@Autowired
	DozerBeanMapper mapper;

	@Autowired
	MqttDeviceClient deviceClient;

	@Autowired
	MqttMiddlewareClient middlewareClient;

	@Autowired
	ArgosSlaveRepository slaveRepository;

	@Autowired
	ArgosDeviceRepository deviceRepository;

	private BiFunction<MqttMessage, String, Boolean> processSlaveCache = (m, t) -> {
		if (m == null || m.toString().isEmpty())
			return false;
		logger.info("Receiving a message {} on topic {}", m.toString(), t);

		SlaveCacheJsonDto[] auxList = null;
		try {
			auxList = gson.fromJson(m.toString(), SlaveCacheJsonDto[].class);
		} catch (Exception e) {
			logger.error("Gson throw exception, message: {}", e.getMessage());
			e.printStackTrace();
			return false;
		}

		logger.debug("{} slaves are registering theirselves", auxList.length);

		String[] topicTokens = t.split("/");
		String deviceId = topicTokens[topicTokens.length - 1];
		logger.debug("Requesting the device {} ", deviceId);
		// uses a transactional method in order to avoid session closed exceptions

		saveDeviceSlaves(deviceId, auxList);

		return true;
	};

	@Transactional
	private void saveDeviceSlaves(String deviceId, SlaveCacheJsonDto[] dtos) {
		// getting the device entity
		ArgosDeviceEntity deviceEntity = deviceRepository.findOne(deviceId);

		if (deviceEntity == null) {
			logger.debug("Slaves can't be saved now because the bridge device is not saved");

			return;
		} else {
			logger.info("Argos device: {}", deviceEntity.getDeviceId());
		}
		// setting the entity manually
		List<ArgosSlaveEntity> slaveList = new ArrayList<>();
		for (SlaveCacheJsonDto aux : dtos) {
			logger.info("Argos slave device to be processed: {}", aux);
			ArgosSlaveEntity entity = new ArgosSlaveEntity();
			SlaveId id = new SlaveId();
			id.setDeviceId(deviceId);
			id.setArgosId(aux.getArgosId());
			entity.setSlaveId(id);
			entity.setName(aux.getName());
			entity.setControlType(aux.getControlType());
			entity.setHardwareType(aux.getHardwareType());
			MappingJsonDto[] mappings = aux.getMappings();

			List<MappingVO> mapList = new ArrayList<>();
			for (MappingJsonDto mappingJsonDto : mappings) {
				MappingVO map = mapper.map(mappingJsonDto, MappingVO.class);
				map.setSlave(entity);
				mapList.add(map);
			}
			entity.setMappings(mapList);
			slaveList.add(entity);
		}
		if (!slaveList.isEmpty()) {
			deviceEntity.setSlaves(slaveList);
			try {
				deviceRepository.save(deviceEntity);
				logger.info("updating the device");
			} catch (Exception e) {
				logger.error("Persistence throw exception, message: {}", e.getMessage());
				e.printStackTrace();
				return;
			}

		}

		logger.debug("Finishing the slave treatment");

	}

	@PostConstruct
	@Override
	public void startAllSubscriptions() {
		subscribeToSlaveCache();
	}

	@Override
	public void publishPingToSlave() {
		logger.info("Publishing to {}", MqttTopics.ARGOS_SLAVELIST_PING);
		try {
			deviceClient.publish(MqttTopics.ARGOS_SLAVELIST_PING, new MqttMessage("".getBytes()));
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to publish, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void subscribeToSlaveCache() {
		logger.info("Subscribing to {}", MqttTopics.ARGOS_SLAVELIST_DEVICE_DEVICEID);
		try {
			deviceClient.subscribe(processSlaveCache, MqttTopics.ARGOS_SLAVELIST_DEVICE_DEVICEID);
		} catch (MqttException e) {
			logger.error("MqttClient face some problem to subscribe, please se the message: {}", e.getMessage());
			e.printStackTrace();
		}

	}

}
