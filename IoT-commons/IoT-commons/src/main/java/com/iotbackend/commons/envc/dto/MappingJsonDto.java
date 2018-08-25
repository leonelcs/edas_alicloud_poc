package com.iotbackend.commons.envc.dto;

import lombok.Data;

@Data
public class MappingJsonDto {
	
	public static enum LocalType {
		AREA(0), BUILDING(1), FARM(2), DEPARTMENT(3), COMPARTMENT(4);
		
		private Integer value;
		
		LocalType(Integer value) {
			this.value = value;
		}
		
		public Integer getValue() {
			return this.value;
		}
	}
	
	private String areaId;
	
	private String buildingId;
	
	private String farmId;
	
	private String departmentId;
	
	private String compartmentId;

}
