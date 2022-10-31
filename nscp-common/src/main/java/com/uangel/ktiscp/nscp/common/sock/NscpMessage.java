package com.uangel.ktiscp.nscp.common.sock;

import java.nio.ByteBuffer;
import java.util.Date;

import com.uangel.ktiscp.nscp.common.asn1.Asn1Message;
import com.uangel.ktiscp.nscp.common.json.JsonType;
import com.uangel.ktiscp.nscp.common.util.TBCDUtil;
import com.uangel.ktiscp.nscp.common.util.TimeUtil;
import com.uangel.utms.uTMS_Util.ByteUtil;

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
	
	public void setValue(String name, Object value) {
		if ( value instanceof String ) {
			String strValue = (String)value;
			if ( strValue.startsWith("0x") ) {
				asn1Message.setValue(name, ByteUtil.hexStrToOctet(strValue));
				return;
			}
		}
		asn1Message.setValue(name, value);
	}
	
	public Object getValue(String name) {
		return asn1Message.getValue(name);
	}
	
	public String getStringValue(String name) {
		return asn1Message.getStringValue(name); 
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
			case NONE:
				return false;
			default:
				return true;
		}
	}
	
	private void setRoutingInfo(String npa, String prefix) {
		routingInformation = new byte[6];
		ByteBuffer buf = ByteBuffer.wrap(routingInformation);
		buf.put((byte)1);
		buf.put(TBCDUtil.parseTBCD(prefix));
		buf.put((byte)0xff);
		buf.put(TBCDUtil.parseTBCD(npa));
	}
	
	public void setRoutingInfoFromMdn(String mdn) {
		String npa = mdn.substring(0, 3);
		String prefix = mdn.substring(3, 7);
		this.setRoutingInfo(npa, prefix);
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
		sb.append("\n\t\t").append("routingInformation : ").append(bytesToHex(routingInformation) 
				+ "(" + routingInfoString(routingInformation) + ")");
		sb.append("\n\t\t").append("oTID               : ").append(oTID);
		sb.append("\n\t\t").append("dTID               : ").append(dTID);
		sb.append("\n\t\t").append("timeStamp          : ").append(timeStamp)
				.append("(" + TimeUtil.dateToString(new Date(new Long(timeStamp)*1000), "yyyy/MM/dd HH:mm:ss")+ ")");
		if ( asn1Message != null ) {
			sb.append("\n\t\t").append("ASN.1              : ").append(asn1Message.getTraceString());
		}
		return sb.toString();
	}
	
	private static String toHex(int n) {
		return String.format("0x%02x", n);
	}
	
	private static String bytesToHex(byte[] a) {
		if ( a == null ) {
			return "";
		}
		StringBuilder sb = new StringBuilder(a.length * 2);
		for(byte b: a) {
			sb.append(String.format("%02x ", b));
		}
		return sb.toString();
	}
	
	public static String routingInfoString(byte[] routingInfo) {
		if ( routingInfo == null ) {
			return "";
		}
		ByteBuffer buffer = ByteBuffer.wrap(routingInfo);
		byte flag = buffer.get();
		if ( flag == 0 ) {
			return "NONE";
		}
		byte[] prefix = new byte[3];
		buffer.get(prefix);
		byte[] npa = new byte[2];
		buffer.get(npa);
		
		String strPrefix = TBCDUtil.toTBCD(prefix);
		String strNpa = TBCDUtil.toTBCD(npa);
		
		
		return String.format("Prefix:%s, NPA:%s", strPrefix, strNpa);
	}
	
	public String toJson() {
		JsonType json = JsonType.makeJsonObject();
		
		JsonType header = json.addObject("header");
		header.setValue("messageVersion", this.messageVersion);
		header.setValue("linkedId", this.linkedId);
		header.setValue("messageId", this.messageId);
		header.setValue("messageType", this.messageType);
		header.setValue("serviceId", this.serviceId);
		header.setValue("operationCode", this.operationCode);
		header.setValue("oTID", this.oTID);
		header.setValue("dTID", this.dTID);
		header.setValue("timeStamp", this.timeStamp);
		header.setValue("dataLength", this.dataLength);
		
		
		if ( asn1Message != null ) {
			JsonType body = json.addObject("body");
			asn1Message.writeToJsonType(body);
		}
		return json.toString();
	}
}
