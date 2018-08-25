package com.iotbackend.producer.envc.messages;

public class MqttTopics {
	
	public static final String ARGOS_TOPIC_SEPARATOR = "/";
	
	public static final String ARGOS_ANNOUNCE_DEVICE_ID = "Argos/Announce/Device/+";
	
	public static final String ARGOS_ANNOUNCE_PING = "Argos/Announce/Ping";
	
	public static final String ARGOS_ONLINE_STATE_ID = "Argos/Online/State/+";
	
	public static final String ARGOS_FUNCTION_CACHE_READY_ID = "Argos/Function/Cache/Ready/Device/+";
		
	public static final String ARGOS_FUNCTION_CACHE_GET_ID = "Argos/Function/Cache/Get";
	
	public static final String ARGOS_FUNCTION_CACHE_READY_PING = "Argos/Function/Cache/Ready/Ping";
	
	public static final String ARGOS_FUNCTION_CACHE_RESPONSE_ID = "Argos/Function/Cache/Response/+";
	
	public static final String ARGOS_FUNCTION_CHANGE_DEVICEID_SLAVEID_FUNCTIONID = "Argos/Function/Change/#";
	
	public static final String ARGOS_ALARM_CACHE_READY_ID = "Argos/Alarm/Cache/Ready/Device/+";
	
	public static final String ARGOS_ALARM_CACHE_READY_PING = "Argos/Alarm/Cache/Ready/Ping";
	
	public static final String ARGOS_ALARM_CACHE_GET_ID = "Argos/Alarm/Cache/Get";
	
	public static final String ARGOS_ALARM_CACHE_RESPONSE_ID = "Argos/Alarm/Cache/Response/+";
	
	public static final String ARGOS_ALARM_CHANGE_DEVICEID_SLAVEID = "Argos/Alarm/Change/#";
	
	public static final String ARGOS_SLAVELIST_DEVICE_DEVICEID = "Argos/Slavelist/Device/+";
	
	public static final String ARGOS_SLAVELIST_PING = "Argos/Slavelist/Ping";
	
	public static final String ARGOS_MAPPING_MODIFY_ADD_DEVICEID = "Argos/Mapping/Modify/Add";
	
	public static final String ARGOS_MAPPING_MODIFY_REMOVE_DEVICEID = "Argos/Mapping/Modify/Remove";

}
