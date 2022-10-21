package com.uangel.ktiscp.nscp.common.sock;

public enum OperationCode {
	// 비기 서비스
	DIS_TES_SUBS(0x10),
	CHG_TES_SUBS(0x07),
	CHG_TES_REMAINS(0x0B),
	SEND_TES_R(0x24),
	
	// USSD 서비스
	PROCESS_UNSTRUNCTURED_SS_REQUEST(0x3B),
	UNSTRUNCTURED_SS_REQUEST(0x3C),
	UNSTRUNCTURED_SS_NOTIFICATION(0x3D),
	
	// ClickToCall 서비스
	NETWORK_INITIATE_CALL_REQUEST(0x01),
	REPORT_CALL_STATUS(0x02),
	CHECK_CALL_STATUS(0x03),
	
	// Authentication 서비스
	SEND_AUTH_SMS(0x01),
	ANSWER_AUTH_SMS(0x02),
	
	// PC Phone
	CALL_TERMINATION_NOTIFICATION(0x01),
	CALL_TERMINATION_RESULT_NOTIFICATION(0x02),
	DRAG_AND_DROP_CALL_REQUEST(0x03),
	DRAG_AND_DROP_CALL_REPORT(0x04),
	
	// 해외 로머 위치 정보
	CS_ROAMING_NOTI(0x01),
	PS_ROAMING_NOTI(0x02),
	CANCELLATION_NOTI(0x03),
	ROAMING_INFOR_QUERY(0x04),
	EPS_ROAMING_NOTI(0x05),
	
	// 아이서치 서비스
	LOCATION_REPORT_TO_GUARDIAN_REQUEST(0x01),
	
	// 기업전용 LTE 서비스
	PLTE_CHECK_STATUS(0x01),
	PLTE_SETUP_REQUEST(0x02)
	;
	
	private int value;
	
	private OperationCode(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}
