package com.iotbackend.app.envc.controllers.ws;

import java.util.function.BiFunction;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.iotbackend.app.envc.messages.MqttMiddlewareClient;
import com.iotbackend.commons.envc.service.IMessageDrivenService;

@CrossOrigin
@Controller
public class DeviceFunctionsWSController implements IMessageDrivenService {
	private static final Logger logger = LoggerFactory.getLogger(DeviceFunctionsWSController.class);
	
	@Autowired
	MqttMiddlewareClient middlewareClient;
	
	@Autowired
    private SimpMessageSendingOperations template;
	
	@PostConstruct
	@Override
	public void startAllSubscriptions() {
		logger.info("Starting all subscriptions");
		subscribeToFunctionChanges();
	}	

	public void publishFunctionUpdates(String functionChannel, String data) {
		logger.debug("Sending WS message: ");
		logger.debug(functionChannel);
		template.convertAndSend(functionChannel, data);
	}
	
	private BiFunction<MqttMessage, String, Boolean> handleFunctionChange = (message, topic) -> {
		
		String[] parts = topic.split("/");
		StringBuffer channelBuffer = new StringBuffer();
		
		// Mapping
		channelBuffer.append(parts[3]).append("/");
		channelBuffer.append(parts[4]).append("/");
		channelBuffer.append(parts[5]).append("/");
		channelBuffer.append(parts[6]).append("/");
		channelBuffer.append(parts[7]).append("/");
		// Type
		channelBuffer.append(parts[8].toLowerCase()).append("/");
		// Device
		channelBuffer.append(parts[9].replaceAll(":", "")).append("/");
		channelBuffer.append(parts[10]);
		
		String updateMessage = parts[11] + "=" + new String(message.getPayload()); 
		
		logger.debug("Sending update " + updateMessage);

		publishFunctionUpdates("/functions/update/" + channelBuffer.toString(), updateMessage);
		
		return true;
	};

	public void subscribeToFunctionChanges() {
		logger.info("Subscribing to EC/Function/Update/#");
		try {
			middlewareClient.subscribe(handleFunctionChange, "EC/Function/Update/#");
		} catch (MqttException e) {
			logger.error("Unable to start all subscriptions");
			e.printStackTrace();
		}		
	}
}
