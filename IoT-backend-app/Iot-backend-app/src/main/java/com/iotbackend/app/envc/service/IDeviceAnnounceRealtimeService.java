package com.iotbackend.app.envc.service;

import com.iotbackend.commons.envc.service.IMessageDrivenService;

public interface IDeviceAnnounceRealtimeService extends IMessageDrivenService {
	
	void subscribeToDeviceStatus();	

}
