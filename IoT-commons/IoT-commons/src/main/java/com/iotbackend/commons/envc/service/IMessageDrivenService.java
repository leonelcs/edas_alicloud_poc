package com.iotbackend.commons.envc.service;

/**
 * This interface must be implemented for every single service that subscribes to a topic
 * either to the device connection or to middleware connection
 * @author lsilva
 *
 */
public interface IMessageDrivenService {

	/**
	 * This method must be annotated with @PostConstruct annotation in the implementation
	 * And all methods that subscribes to any topic of mqtt must be called inside the method
	 */
	public abstract void startAllSubscriptions();

}
