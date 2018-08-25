package com.iotbackend.app.envc.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.iotbackend.commons.envc.realtime.dto.DeviceAdviceDto;

@Controller
@CrossOrigin
public class DeviceAnnounceWSController {
	
    @MessageMapping("/request-device-status")
    @SendTo("/device-status-broker/device-status")
    public DeviceAdviceDto getDeviceStatus(DeviceAdviceDto message) {
    	//"Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!"
    	return message;
    }

}
