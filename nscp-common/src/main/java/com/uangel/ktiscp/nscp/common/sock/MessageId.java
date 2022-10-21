package com.uangel.ktiscp.nscp.common.sock;

import java.util.HashMap;
import java.util.Map;

public enum MessageId {
	CONNECTION_CHECK_REQUEST(0x01),
	CONNECTION_CHECK_RESPONSE(0x02),
	CONNECTION_REQUEST(0x03),
	CONNECTION_RESPONSE(0x04),
	RELEASE_REQUEST(0x05),
	RELEASE_RESPONSE(0x06),
	RESET_TIMER(0x07),
	SERVICE_REQUEST(0x10),
	SERVICE_RESPONSE(0x11),
	SERVICE_DIRECTIVE(0x12),
	SERVICE_DIRECTIVE_RESPONSE(0x13),
	SERVICE_REPORT(0x14),
	SERVICE_REPORT_RESPONSE(0x15),
	SERVICE_NOTIFICATION(0x16);
	
	private static final Map<Integer, MessageId> intToTypeMap = new HashMap<Integer, MessageId>();
	static {
		for (MessageId messageId : MessageId.values()) {
			intToTypeMap.put(messageId.getValue(), messageId);
		}
	}
	
	public static MessageId fromInt(int id) {
		MessageId messageId = intToTypeMap.get(Integer.valueOf(id));
	    if (messageId == null) 
	        return null;
	    return messageId;
	}
	
	private int value;
	
	private MessageId(int id) {
		this.value = id;
	}
	
	public int getValue() {
		return value;
	}
	
	public MessageId getResponse() {
		switch ( this ) {
		case CONNECTION_CHECK_REQUEST:
			return CONNECTION_CHECK_RESPONSE;
		case CONNECTION_REQUEST:
			return CONNECTION_RESPONSE;
		case RELEASE_REQUEST:
			return RELEASE_RESPONSE;
		case SERVICE_REQUEST:
			return SERVICE_RESPONSE;
		case SERVICE_DIRECTIVE:
			return SERVICE_DIRECTIVE_RESPONSE;
		case SERVICE_REPORT:
			return SERVICE_REPORT_RESPONSE;
		default:
			throw new RuntimeException("Not found response id.");
		}
	}
}
