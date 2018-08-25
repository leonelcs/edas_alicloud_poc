package com.iotbackend.producer.envc.topics.service;

import com.iotbackend.commons.envc.dto.MappingJsonDto;

/**
 * this interface doesn't extends IMessageDrivenService because it doesn't have subscribers
 * @author lsilva
 *
 */
public interface IMappingTopicService {

	void publishCreateModifyMapping(MappingJsonDto dto);
	
	void publishDeleteMapping(MappingJsonDto dto);
}
