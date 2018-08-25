package com.iotbackend.producer.envc.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Embeddable
@Data
@EqualsAndHashCode(of= {"argosId","deviceId"})
public class SlaveId implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "argos_id")
	private String argosId;
	
	@Column(name = "device_id")
	private String deviceId;

}
