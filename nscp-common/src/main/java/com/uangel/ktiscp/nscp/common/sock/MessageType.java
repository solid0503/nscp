package com.uangel.ktiscp.nscp.common.sock;

import java.util.HashMap;
import java.util.Map;

public enum MessageType {
	NONE(0x00),
	INITIAL(0x01),
	CONTINUE(0x02),
	TERMINATION(0x03),
	ERROR(0x10),
	REJECT(0x11),
	ABORT(0x12);
	
	private static final Map<Integer, MessageType> intToTypeMap = new HashMap<Integer, MessageType>();
	static {
		for (MessageType messageType : MessageType.values()) {
			intToTypeMap.put(messageType.getValue(), messageType);
		}
	}
	
	private int value;
	private MessageType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static MessageType fromInt(int id) {
		MessageType messageType = intToTypeMap.get(Integer.valueOf(id));
	    if (messageType == null) 
	        return null;
	    return messageType;
	}
}
