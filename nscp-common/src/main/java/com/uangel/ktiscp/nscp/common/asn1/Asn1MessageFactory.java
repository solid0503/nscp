package com.uangel.ktiscp.nscp.common.asn1;

public interface Asn1MessageFactory {
	// Operation 이름으로 메시지 생성 (ex:CS-Roaming-Noti-Invoke)
	Asn1Message newMessage(String name);
	
	// Service-Id + OpCode로 생성
	Asn1Message newRecvMessage(int serviceId, int opCode);
	
	Integer getOpcodeByName(String name);
	Integer getServiceIdByName(String name);
	String getOperationName(int serviceId, int opCode);
}
