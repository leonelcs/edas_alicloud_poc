package com.iotbackend.commons.envc.helper;

public class ValidatorConverterHelper {
	
	public static String cleanDeviceNumber(String deviceNumber) {
		String[] deviceTokens = deviceNumber.split(":");
		String deviceId;
		if (deviceTokens.length==1)
			deviceId = deviceNumber;
		else {
			StringBuffer buffer = new StringBuffer();
			for (String token : deviceTokens) {
				buffer.append(token);
			}
			deviceId = buffer.toString();
		}
		return deviceId;
	}
	
	public static String formatToMacAddress(String s) {
		String[] tokens = s.split(":");
		if (tokens.length!=1)
			return s;
		StringBuffer buffer = new StringBuffer();
		for (int i=0;i<s.length();i+=2) {
			if (i<s.length()-2)
				buffer.append(s.substring(i, i+2)).append(":");
			else
				buffer.append(s.substring(i, i+2));
		}
		return buffer.toString();
	}

}
