package com.iotbackend.producer.envc.topics.service;

import com.iotbackend.commons.envc.service.IMessageDrivenService;

public interface IAnnounceTopicService extends IMessageDrivenService {
	
	void subscribeToDeviceAnnounce();
	
	String pingDeviceForAnnounce();
	
	void subscribeDeviceStatus();

}
