package com.iotbackend.producer.envc.topics.service;

import com.iotbackend.commons.envc.service.IMessageDrivenService;

public interface ISlaveTopicService extends IMessageDrivenService {

	void publishPingToSlave();
	
	void subscribeToSlaveCache();
}
