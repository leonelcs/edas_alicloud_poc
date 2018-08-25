package com.iotbackend.producer.envc.topics.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
import com.iotbackend.commons.envc.helper.ValidatorConverterHelper;
import com.iotbackend.producer.envc.cache.domain.DeviceAdviceCache;
import com.iotbackend.producer.envc.cache.repository.DeviceAdviceRepository;
import com.iotbackend.producer.envc.domain.ArgosDeviceEntity;
import com.iotbackend.producer.envc.domain.repository.ArgosDeviceRepository;
import com.iotbackend.producer.envc.messages.MqttDeviceClient;
import com.iotbackend.producer.envc.messages.MqttMiddlewareClient;
import com.iotbackend.producer.envc.messages.MqttTopics;
import com.iotbackend.producer.envc.topics.service.IAnnounceTopicService;

@Service
public class AnnounceTopicServiceImpl implements IAnnounceTopicService {

	private static final Logger logger = LoggerFactory.getLogger(AnnounceTopicServiceImpl.class);
	
	@Autowired
	Gson gson;

	@Autowired
	MqttDeviceClient deviceClient;

	@Autowired
	MqttMiddlewareClient middlewareClient;

	@Autowired
	ArgosDeviceRepository argosRepository;

	@Autowired
	DeviceAdviceRepository<String, DeviceAdviceCache> deviceRepository;

	@PostConstruct
	@Override
	public void startAllSubscriptions() {
		subscribeToDeviceAnnounce();
		// subscribeDeviceStatus();
	}

	private BiFunction<MqttMessage, String, Boolean> processDeviceAnnounce = (m, t) -> {
		String messageJson = m.toString();

		logger.debug("Receiving a message {} on topic {}", messageJson, t);

		ArgosDeviceEntity entity = gson.fromJson(messageJson, ArgosDeviceEntity.class);

		if (!argosRepository.exists(entity.getDeviceId())) {
			logger.info("Entity first registration: {}", entity.getDeviceId());
			entity.setRegisteredOn(Timestamp.valueOf(LocalDateTime.now()));
		} else {
			logger.info("Entity already exists over id : {}", entity.getDeviceId());
			entity = argosRepository.findOne(entity.getDeviceId());
		}
		ArgosDeviceEntity response = argosRepository.save(entity);
		logger.info("The device id {} got saved", response.getDeviceId());
		return true;
	};

	private BiFunction<MqttMessage, String, Boolean> treatOnlineMessage = (m, t) -> {

		logger.debug("The message {} is received in the topic {}", m.toString(), t);
		if (m == null || m.toString().isEmpty())
			return false;
		// sending the information to middleware mqtt - non-blocking async call
		logger.debug("Sending the message {} to the middleware mqtt topic {}", m.toString(), t);

		try {
			middlewareClient.publish(t, m);
		} catch (MqttException e) {
			logger.error("Problems when publishing in middleware mqtt, see the message: {}", e.getMessage());
			e.printStackTrace();
		}

		DeviceAdviceCache deviceAdvice = new DeviceAdviceCache();
		String[] topic = t.split("/");

		logger.debug("device Id is {}", topic[topic.length - 1]);


		String deviceId = ValidatorConverterHelper.cleanDeviceNumber(topic[topic.length - 1]);

		deviceAdvice.setDeviceID(deviceId);
		if ("1".equals(m.toString())) {
			deviceAdvice.setOnline(true);
			deviceAdvice.setOnlineLastTime(Timestamp.valueOf(LocalDateTime.now()));
		} else
			deviceAdvice.setOnline(false);

		logger.debug("Device Id {} status {} saved on redis", deviceAdvice.getDeviceID(), deviceAdvice.getOnline());

		deviceRepository.save(deviceAdvice);
		return true;
	};

	@Override
	public void subscribeToDeviceAnnounce() {
		logger.info("Subscribing to {}", MqttTopics.ARGOS_ANNOUNCE_DEVICE_ID);
		try {
			deviceClient.subscribe(processDeviceAnnounce, MqttTopics.ARGOS_ANNOUNCE_DEVICE_ID);
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Scheduled(fixedDelayString = "${argos.announce.pingMilliseconds}")
	public String pingDeviceForAnnounce() {
		try {
			deviceClient.publish(MqttTopics.ARGOS_ANNOUNCE_PING, new MqttMessage("".getBytes()));
			return "successful";
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Failed for the reasion: " + e.getMessage();
		}
	}

	@Override
	public void subscribeDeviceStatus() {
		logger.info("Subscribing to {}", MqttTopics.ARGOS_ONLINE_STATE_ID);
		try {
			deviceClient.subscribe(treatOnlineMessage, MqttTopics.ARGOS_ONLINE_STATE_ID);
		} catch (MqttException e) {
			logger.error("The MQttConnection thrown a exception - {} ", e.getMessage());
			e.printStackTrace();
		}
	}

}
