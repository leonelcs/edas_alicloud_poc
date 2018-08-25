package com.iotbackend.producer.envc.messages;

import java.util.function.BiFunction;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Connect to the MQtt instance that communicate with the application
 * 
 * @author lsilva
 *
 */
@Component
public class MqttMiddlewareClient {

	private static final Logger logger = LoggerFactory.getLogger(MqttMiddlewareClient.class);

	@Value("${mqtt.middlewareInstance.server}")
	String middlewareServer;

	@Value("${mqtt.middlewareInstance.port}")
	String middlewarePort;

	@Value("${mqtt.middlewareInstance.user}")
	String middlewareUser;

	@Value("${mqtt.middlewareInstance.password}")
	String middlewarePassword;

	MqttClient client;

	@PostConstruct
	public void postConstruct() {
		try {
			logger.info("Connecting middlewareClient");

			client = new MqttClient("tcp://" + middlewareServer + ":" + middlewarePort,
					"middleware_" + MqttClient.generateClientId());

			MqttConnectOptions options = new MqttConnectOptions();

			options.setUserName(middlewareUser);
			options.setPassword(middlewarePassword.toCharArray());
			options.setAutomaticReconnect(true);

			this.client.connect(options);
			logger.info("middlewareClient Connected");
		} catch (MqttException e) {
			logger.error("The Mqtt connection throwed an exception {}", e.getMessage());
			e.printStackTrace();
		}
	}

	public void subscribe(BiFunction<MqttMessage, String, Boolean> treatmentFunction, String mqttTopic)
			throws MqttException {

		logger.debug("== START SUBSCRIBER ==");
		if (client == null || !client.isConnected())
			postConstruct();
		IMqttMessageListener customizedMessageListener = new DefaultMqttMessageListener(treatmentFunction);
		client.subscribe(mqttTopic, customizedMessageListener);
		logger.debug("== Finish Subscription == ");
	}

	public void publish(String topic, MqttMessage message) throws MqttPersistenceException, MqttException {
		if (client == null || !client.isConnected())
			postConstruct();
		client.publish(topic, message);
		logger.debug("== Finish Publishing == ");
	}

}
