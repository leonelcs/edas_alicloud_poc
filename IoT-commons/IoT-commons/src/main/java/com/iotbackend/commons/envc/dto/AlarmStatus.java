package com.iotbackend.commons.envc.dto;

import com.google.gson.annotations.SerializedName;

public enum AlarmStatus {
	
	@SerializedName("0") DELETE("0"), @SerializedName("1") WARNING("1"), @SerializedName("2") ACTIVE("2");
	
	private String value;
	
	private AlarmStatus(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return this.value;
	}

}
