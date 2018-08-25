package com.iotbackend.commons.envc.realtime.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String text;

}
