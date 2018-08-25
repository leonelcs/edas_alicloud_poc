package com.iotbackend.producer.envc.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="argos_slave")
public class ArgosSlaveEntity {
	
	@EmbeddedId
	private SlaveId slaveId;
	
	@ManyToOne
	@JoinColumn(name="device_id", insertable=false, updatable=false)
	private ArgosDeviceEntity device;
	
	@Column(name="name")
	private String name;
	
	@Column(name="hardware_type")
	private String hardwareType;
	
	@Column(name="control_type")
	private String controlType;
	
	@Column(name="version")
	private String version;
	
	@ElementCollection
	private List<MappingVO> mappings;

}
