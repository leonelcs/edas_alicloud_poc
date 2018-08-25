package com.iotbackend.producer.envc.messages.exceptions;

import org.eclipse.paho.client.mqttv3.MqttException;

public class NoCallbackMqttException extends MqttException {
	
	private static final long serialVersionUID = -53361389809328112L;
	
	public static final short REASON_CODE_NO_SERVICE_CALLBACK_PASSED	= 32305;
	
	String cause;

	public NoCallbackMqttException() {
		super(REASON_CODE_NO_SERVICE_CALLBACK_PASSED);
		this.cause = "And callback service which implement the interface IMessageListenerService must be passed";
	}
	

}
