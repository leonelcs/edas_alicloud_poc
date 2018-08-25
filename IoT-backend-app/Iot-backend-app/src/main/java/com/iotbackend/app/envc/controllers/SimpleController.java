package com.iotbackend.app.envc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iotbackend.app.envc.messages.MqttMiddlewareClient;
import com.iotbackend.commons.envc.service.EchoService;
import com.iotbackend.commons.envc.service.MQttSubscriberService;

/**
 * Created by Leonel on 16/05/2018.
 */
@RestController
@CrossOrigin
public class SimpleController {

	private static final Logger logger = LoggerFactory.getLogger(SimpleController.class);

	private SimpMessagingTemplate template;

	@Autowired
	MqttMiddlewareClient middlewareClient;

	@Autowired
	public SimpleController(SimpMessagingTemplate template) {
		this.template = template;
	}

	@Autowired
	private EchoService echoService;

	@Autowired
	private MQttSubscriberService mqttService;

	@GetMapping("/test")
	public String test() {
		return "test";
	}

	@RequestMapping(value = "/hsf-echo/{str}", method = RequestMethod.GET)
	public String echo(@PathVariable String str) {
		logger.debug("calling echo service");

		//template.convertAndSend("/topic/greetings", new Greeting(str));

		// if (str.equals("start") ) {
		// subscriber.subscribe();
		// }
		//
		// if(str.equals("stop")) {
		// subscriber.unSubscribe();
		// }

		return echoService.echo(str);
	}

	@RequestMapping(value = "/mqtt-subscribe", method = RequestMethod.GET)
	public String mqttSubscribe() {
		return mqttService.mqttSubscribe();
	}

	// @RequestMapping(value = "/realtime/{key}", method = RequestMethod.GET)
	// public Message realtimeReader(@PathVariable String key) {
	// return realtimeService.retrieveMessage(key);
	//
	// }
}
