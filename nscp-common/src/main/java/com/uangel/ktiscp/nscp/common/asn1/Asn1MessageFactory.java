package com.uangel.ktiscp.nscp.common.asn1;

/**
 * Asn1Message Factory 인터페이스
 *
 */
public interface Asn1MessageFactory {
	/**
	 * Operation 이름으로 메시지 생성 (ex:CS-Roaming-Noti-Invoke)
	 */
	Asn1Message newMessage(String name);
	
	
	/**
	 * Client로부터 메시지를 수신했을때 사용하는 메시지 생성 인터페이스. Service-Id + OpCode로 생성
	 */
	Asn1Message newRecvMessage(int serviceId, int opCode);

	/**
	 * serviceId + opCode로 이름(ex:CS-Roaming-Noti-Invoke) 구하는 함수
	 */
	String getOperationName(int serviceId, int opCode);
	
	
	/**
	 * 이름으로 Opcode 구하는 함수 (시뮬레이터용)
	 */
	Integer getOpcodeByName(String name);
	/**
	 * 이름으로 ServiceID 구하는 함수 (시뮬레이터용)
	 */
	Integer getServiceIdByName(String name);
}
