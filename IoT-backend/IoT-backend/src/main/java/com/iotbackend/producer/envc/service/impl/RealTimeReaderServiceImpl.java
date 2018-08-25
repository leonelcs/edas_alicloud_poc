package com.iotbackend.producer.envc.service.impl;

import com.alibaba.boot.hsf.annotation.HSFProvider;
import com.iotbackend.commons.envc.service.RealTimeReaderService;

@HSFProvider(serviceInterface = RealTimeReaderService.class, serviceVersion = "1.0.0")
public class RealTimeReaderServiceImpl implements RealTimeReaderService {
	
//	@Autowired
//	MessageRepositoryImpl repository;
//
//	@Override
//	public com.yingzi.commons.envc.realtime.dto.Message retrieveMessage(String text) {
//		MessageCache message = repository.findById(text);
//		DozerBeanMapper mapper = new DozerBeanMapper();
//		com.yingzi.commons.envc.realtime.dto.Message messageDto = mapper.map(message, com.yingzi.commons.envc.realtime.dto.Message.class);
//		return messageDto;
//	}

}
