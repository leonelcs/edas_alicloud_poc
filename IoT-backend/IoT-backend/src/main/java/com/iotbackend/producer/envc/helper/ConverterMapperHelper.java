package com.iotbackend.producer.envc.helper;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
public class ConverterMapperHelper {
	
	private Gson gsonInstance;
	
	private DozerBeanMapper mapper;
	
	@Bean
	public Gson gson() {
		gsonInstance = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gsonInstance;
	}
	
	@Bean
	public DozerBeanMapper mapper() {
		mapper = new DozerBeanMapper();
		return mapper;
	}

}
