package com.uangel.ktiscp.nscp.common.sock;

import java.util.HashMap;
import java.util.Map;

public enum ServiceId {
	TES(0x01),
	USSD(0x02),
	CLICK_TO_CALL(0x03),
	AUTHENTICATION(0x04),
	PC_PHONE(0x05),
	ROAMING_LOCATION_INFO(0x06),
	I_SEARCH_PREMIUM(0x07),
	BUSINESS_LTE(0x08)
	;
	
	private static final Map<Integer, ServiceId> intToTypeMap = new HashMap<Integer, ServiceId>();
	static {
		for (ServiceId serviceId : ServiceId.values()) {
			intToTypeMap.put(serviceId.getValue(), serviceId);
		}
	}
	
	private int value;
	
	private ServiceId(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static ServiceId fromInt(int id) {
		ServiceId serviceId = intToTypeMap.get(Integer.valueOf(id));
	    if (serviceId == null) 
	        return null;
	    return serviceId;
	}
}
