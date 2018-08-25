package com.iotbackend.commons.envc.service;

import java.util.List;

import com.iotbackend.commons.envc.dto.ArgosDeviceDto;
import com.iotbackend.commons.envc.realtime.dto.DeviceAdviceDto;

public interface AnnounceRPCService {
	
	List<ArgosDeviceDto> listAllArgosDevices();
	
	List<DeviceAdviceDto> getAllDeviceStatus();

}
