package com.uangel.ktiscp.nscp.common.asn1;

import com.uangel.asn1.Asn1Object;
import com.uangel.ktiscp.nscp.common.json.JsonType;

/**
 * 
 * Asn1Message 파라미터를 다루기 위한 인터페이스
 *
 */
public interface Asn1Message {
	/**
	 * 파라미터 값 설정
	 */
	void setValue(String name, Object value);
	
	/**
	 * 파라미터 값 조회
	 */
	Object getValue(String name);
	
	String getStringValue(String name);
	
	/**
	 * 최상위 파라미터 이름
	 */
	String getName();
	
	/**
	 * uaLib의 Asn1Object를 wrapping 한 인터페이스이기 때문에 Asn1Object에 값을 반영하기 위한 함수.
	 * 패키지 내부에서만 사용.
	 */
	void encode(Asn1Object asn1Object);
	/**
	 * uaLib의 Asn1Object를 wrapping 한 인터페이스이기 때문에 Asn1Object으로부터 값을 가져오기 위한 함수.
	 * 패키지 내부에서만 사용.
	 */
	void decode(Asn1Object asn1Object);
	/**
	 * 이쁘게 출력
	 */
	String getTraceString();
	/**
	 * JsonType에 반영
	 */
	void writeToJsonType(JsonType jsonType);
}
