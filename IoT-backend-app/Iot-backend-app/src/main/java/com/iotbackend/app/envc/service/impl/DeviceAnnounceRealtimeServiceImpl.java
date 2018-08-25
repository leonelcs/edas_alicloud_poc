package com.iotbackend.app.envc.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.function.BiFunction;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.iotbackend.app.envc.messages.MqttMiddlewareClient;
import com.iotbackend.app.envc.messages.MqttTopics;
import com.iotbackend.app.envc.service.IDeviceAnnounceRealtimeService;
import com.iotbackend.commons.envc.realtime.dto.DeviceAdviceDto;

@Service
public class DeviceAnnounceRealtimeServiceImpl implements IDeviceAnnounceRealtimeService {

	private static final Logger logger = LoggerFactory.getLogger(DeviceAnnounceRealtimeServiceImpl.class);

	/**
	 * Template that allow you to send a message to a websocket topic
	 */
	private SimpMessagingTemplate template;

	@Autowired
	MqttMiddlewareClient middlewareClient;

	public DeviceAnnounceRealtimeServiceImpl(SimpMessagingTemplate template) {
		this.template = template;
	}

	BiFunction<MqttMessage, String, Boolean> treatDeviceStatus = (m, t) -> {
		// testing either is a message or not.
		if (m == null || m.toString().isEmpty())
			return false;
		logger.debug("Treating the message {] from the middleware topic {} ", m.toString(), t);
		// creating the object
		DeviceAdviceDto deviceAdvice = new DeviceAdviceDto();
		String[] topic = t.split("/");
		logger.debug("device Id is {}", topic[topic.length - 1]);
		deviceAdvice.setDeviceId(topic[topic.length - 1]);
		if ("1".equals(m.toString())) {
			deviceAdvice.setOnline(true);
			deviceAdvice.setOnlineLastTime(Timestamp.valueOf(LocalDateTime.now()));
		} else
			deviceAdvice.setOnline(false);
		// send the message to the topic
		System.out.println("received the message - " + deviceAdvice.getDeviceId() + ", " + deviceAdvice.getOnline());
		template.convertAndSend("/device-status-broker/device-status", deviceAdvice);

		return true;
	};

	@Override
	public void subscribeToDeviceStatus() {
		logger.debug("Subscribing to the topic {}", MqttTopics.ARGOS_ONLINE_STATE_ID);

		try {
			middlewareClient.subscribe(treatDeviceStatus, MqttTopics.ARGOS_ONLINE_STATE_ID);
		} catch (MqttException e) {
			logger.error("The mqtt client failed, see the message: {}", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void startAllSubscriptions() {
		subscribeToDeviceStatus();

	}

}
