package com.iotbackend.commons.envc.service.dto;

import java.util.List;

@lombok.Data
public class RPCSlaveInfo {
	
	private String argosDeviceId;
	
	private String argosId;
	
	private String name;
	
	private String type;
	
	private String version;
	
	private int areaId;
	
	private int buildingId;
	
	private List<Integer> farmId;
	
	private int departmentId;
	
	private int compartmentId;
	
	private int animalId;
	

}
