package com.iotbackend.producer.envc.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Parent;

import lombok.Data;

@Embeddable
@Data
public class MappingVO {
	
	@Parent
	private ArgosSlaveEntity slave;
	
	@Column(name="area_id")
	private String areaId;
	
	@Column(name="building_id")
	private String buildingId;
	
	@Column(name="farm_id")
	private String farmId;
	
	@Column(name="department_id")
	private String departmentId;
	
	@Column(name="compartment_id")
	private String compartmentId;

}
