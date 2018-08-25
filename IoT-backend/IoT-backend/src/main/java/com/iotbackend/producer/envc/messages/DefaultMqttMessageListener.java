package com.iotbackend.producer.envc.messages;

import java.util.function.BiFunction;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotbackend.producer.envc.messages.exceptions.NoMessageTreatmentFunctionException;

public class DefaultMqttMessageListener implements IMqttMessageListener {

	private static final Logger logger = LoggerFactory.getLogger(DefaultMqttMessageListener.class);

	private BiFunction<MqttMessage, String, Boolean> treatmentFunction;

	public DefaultMqttMessageListener(BiFunction<MqttMessage, String, Boolean> treatmentFunction)
			throws NoMessageTreatmentFunctionException {
		if (treatmentFunction == null)
			throw new NoMessageTreatmentFunctionException();
		this.treatmentFunction = treatmentFunction;
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		logger.debug("The message {} just arrived at topic {} using the handler {}", message.toString(), topic,
				treatmentFunction.getClass());
		treatmentFunction.apply(message, topic);
		logger.debug("The message was treated");
	}

}
