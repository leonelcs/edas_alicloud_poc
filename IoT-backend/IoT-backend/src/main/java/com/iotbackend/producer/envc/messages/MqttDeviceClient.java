package com.iotbackend.producer.envc.messages;


import java.util.function.BiFunction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Connect to the MQtt instance that communicate with the devices
 * @author lsilva
 *
 */
@Component
public class MqttDeviceClient {
	
	private static final Logger logger = LoggerFactory.getLogger(MqttDeviceClient.class);

	@Value("${mqtt.deviceInstance.server}")
	String deviceServer;

	@Value("${mqtt.deviceInstance.port}")
	String devicePort;

	@Value("${mqtt.deviceInstance.user}")
	String deviceUser;

	@Value("${mqtt.deviceInstance.password}")
	String devicePassword;

	MqttClient client;

	@PostConstruct
	public void postConstruct() {
		try {
			logger.info("Connecting deviceClient");

			client = new MqttClient("tcp://"+deviceServer+":"+devicePort, "device_"+MqttClient.generateClientId(), new MemoryPersistence());
			logger.info("Client connected by id {}", client.getClientId());

			MqttConnectOptions options = new MqttConnectOptions();

			options.setUserName(deviceUser);
			options.setPassword(devicePassword.toCharArray());
			options.setAutomaticReconnect(true);

			this.client.connect(options);
			logger.info("deviceClient Connected");
		} catch (MqttException e) {
			logger.error("The Mqtt connection throwed an exception {}", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void subscribe(BiFunction<MqttMessage, String, Boolean> treatmentFunction, String mqttTopic) throws MqttException {
		logger.info("== START SUBSCRIBER ==");
		if (client==null || !client.isConnected())
			postConstruct();
		IMqttMessageListener customizedMessageListener = new DefaultMqttMessageListener(treatmentFunction);
		client.subscribe(mqttTopic, 2, customizedMessageListener);
		logger.info("== Finish Subscription == ");
	}
	
	public void publish(String topic, MqttMessage message) throws MqttPersistenceException, MqttException {
		logger.info("Sending message {} to the topic {} in the cloud/devices", message.toString(), topic);
		if (this.client==null || !this.client.isConnected())
			postConstruct();
		message.setQos(2);
		client.publish(topic, message);
		logger.info("== Finish Publishing == ");
	}
	
	@PreDestroy
	private void preDestroy() throws MqttException {
		logger.info("== DISCONNECTING AND DESTROYING THE CONNECTION ==");
		client.disconnect();
		client.close(true);
		client = null;
		logger.info("== UNSUBSCRIBED ==");
	}

}
