package com.uangel.ktiscp.nscp.common.sock;

import java.util.Date;

import com.uangel.ktiscp.nscp.common.asn1.Asn1Message;
import com.uangel.ktiscp.nscp.common.util.TimeUtil;

import lombok.Data;

@Data
public class NscpMessage {
	public static final int HEADER_SIZE = 28;
	
	int messageVersion;	// 1byte
	int linkedId;		// 1byte
	int messageId;		// 1byte
	int messageType;	// 1byte
	int serviceId;		// 1byte
	int operationCode;	// 1byte
	
	byte[] routingInformation;  // 6Byte : flag(1) + prefix(3) + npa(2)
	
	int oTID;  // 4byte
	int dTID;  // 4byte
	
	int timeStamp;  // 4byte
	int dataLength; // 4byte (body 데이터 길이)
	
	Asn1Message asn1Message;  // asn1 body내용
	
	public NscpMessage() {
		this.timeStamp = (int)(System.currentTimeMillis()/1000);
	}
	
	public void setParameter(String name, Object value) {
		asn1Message.setValue(name, value);
	}
	
	public Object getParameter(String name) {
		return asn1Message.getValue(name);
	}
	
	public String getTransactionId() {
		if ( isRequest() ) {
			return String.format("%d:%d:%d", this.oTID, this.linkedId, this.timeStamp);
		} else {
			return String.format("%d:%d:%d", this.dTID, this.linkedId, this.timeStamp);
		}
	}
	
	public boolean isRequest() {
		switch(MessageType.fromInt(messageType)) {
			case INITIAL:
			case CONTINUE:
			case TERMINATION:
				return true;
			case ERROR:
			case REJECT:
			case ABORT:
				return false;
			default:
				return true;
		}
	}
	
	public NscpMessage getResponse(MessageType messageType) {
		NscpMessage res = new NscpMessage();
		res.messageVersion = this.messageVersion;
		res.linkedId = this.linkedId;
		res.messageId = MessageId.fromInt(this.messageId).getResponse().getValue();
		res.messageType = messageType.getValue();
		res.serviceId = this.serviceId;
		res.operationCode = this.operationCode;
		res.routingInformation = this.routingInformation;
		res.oTID = this.dTID;
		res.dTID = this.oTID;
		res.timeStamp = this.timeStamp;
		
		// TODO : oTID가 0인 경우 할당 처리 필요
		
		return res;
	}
	
	public String getTraceString() {
		StringBuilder sb = new StringBuilder(1024);
		
		sb.append("\n\t\t").append("messageVersion     : ").append(messageVersion);
		sb.append("\n\t\t").append("linkedId           : ").append(linkedId);
		sb.append("\n\t\t").append("messageId          : ").append(MessageId.fromInt(messageId)).append("("+toHex(messageId)+")");
		sb.append("\n\t\t").append("messageType        : ").append(MessageType.fromInt(messageType)).append("("+toHex(messageType)+")");
		sb.append("\n\t\t").append("serviceId          : ").append(ServiceId.fromInt(serviceId)).append("("+toHex(serviceId)+")");
		sb.append("\n\t\t").append("operationCode      : ").append(toHex(operationCode));
		sb.append("\n\t\t").append("routingInformation : ").append(routingInformation);
		sb.append("\n\t\t").append("oTID               : ").append(oTID);
		sb.append("\n\t\t").append("dTID               : ").append(dTID);
		sb.append("\n\t\t").append("timeStamp          : ").append(timeStamp)
				.append("(" + TimeUtil.dateToString(new Date(new Long(timeStamp)*1000), "yyyy/MM/dd HH:mm:ss")+ ")");
		sb.append("\n\t\t").append("ASN.1              : ").append(asn1Message.getTraceString());
		return sb.toString();
	}
	
	private static String toHex(int n) {
		return String.format("0x%02x", n);
	}
}
