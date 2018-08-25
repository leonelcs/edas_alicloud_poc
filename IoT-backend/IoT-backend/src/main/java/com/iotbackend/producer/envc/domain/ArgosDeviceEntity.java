package com.iotbackend.producer.envc.domain;


import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="argos_device")
@Data
public class ArgosDeviceEntity {

	@Id
	@Column(name="device_id")
	private String deviceId;
	
	@Column(name="pin")
	private String pin;
	
	@Column(name="name")
	private String name;
	
	@Column(name="location")
	private String location;
	
	@Column(name="type")
	private String type;
	
	@Column(name="version")
	private String version;
	
	@Column(name="ip")
	private String ip;
	
	@Column(name="local_ip")
	private String localIp;
	
	@Column(name="registered_on")
	private Timestamp registeredOn;	
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="device")
	private List<ArgosSlaveEntity> slaves;
	
	
}
