package com.iotbackend.producer.envc.topics.service;

import com.iotbackend.commons.envc.service.IMessageDrivenService;

public interface IAlarmsTopicService extends IMessageDrivenService {

	void subscribeToDeviceAlarmCacheReadySignal();
	
	void publishPingToDeviceAlarmCache();
	
	void publishRequestToDeviceAlarmCache(String deviceID);
	
	void subscribeToResponseDeviceAlarmCache();
	
	void subscribeToAlarmChanges();
}
