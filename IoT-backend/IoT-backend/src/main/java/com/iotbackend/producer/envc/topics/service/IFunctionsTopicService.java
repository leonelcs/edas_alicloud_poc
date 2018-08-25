package com.iotbackend.producer.envc.topics.service;

import com.iotbackend.commons.envc.service.IMessageDrivenService;

public interface IFunctionsTopicService extends IMessageDrivenService {
	
	void subscribeToDeviceCacheReadSignal();
	
	void publishPingToDeviceCache();
	
	void publishRequestToDeviceCache(String deviceID);
	
	void subscribeToResponseDeviceCache();
	
	void subscribeToFunctionChanges();

}
