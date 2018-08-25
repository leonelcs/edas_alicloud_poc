package com.iotbackend.producer.envc.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.boot.hsf.annotation.HSFProvider;
import com.iotbackend.commons.envc.dto.ArgosDeviceDto;
import com.iotbackend.commons.envc.realtime.dto.DeviceAdviceDto;
import com.iotbackend.commons.envc.service.AnnounceRPCService;
import com.iotbackend.producer.envc.cache.domain.DeviceAdviceCache;
import com.iotbackend.producer.envc.cache.repository.DeviceAdviceRepository;
import com.iotbackend.producer.envc.domain.ArgosDeviceEntity;
import com.iotbackend.producer.envc.domain.repository.ArgosDeviceRepository;

@HSFProvider(serviceInterface = AnnounceRPCService.class, serviceVersion = "1.0.0")
public class AnnounceRPCServiceImpl implements AnnounceRPCService {
	
	ArgosDeviceRepository argosDeviceRepository;
	
	@Autowired
	DozerBeanMapper mapper;
	
	@Autowired
	public void setArgosDeviceRepository(ArgosDeviceRepository repo) {
		this.argosDeviceRepository = repo;
	}
	
	@Autowired
	DeviceAdviceRepository<String, DeviceAdviceCache> deviceAdviceRepository;

	@Override
	public List<ArgosDeviceDto> listAllArgosDevices() {
		List<ArgosDeviceEntity> listDeviceEntities = argosDeviceRepository.findAll();
		List<ArgosDeviceDto> listDeviceDtos = listDeviceEntities
				.stream()
				.map( entity -> mapper.map(entity, ArgosDeviceDto.class) )
				.collect(Collectors.toList());
		return listDeviceDtos;
	}
	
	@Override
	public List<DeviceAdviceDto> getAllDeviceStatus() {

		List<DeviceAdviceDto> deviceList = deviceAdviceRepository.findAll()
			.entrySet()
			.stream()
			.map(d -> mapper.map(d.getValue(), DeviceAdviceDto.class))
			.collect(Collectors.toList());

		return deviceList;
	}

}
