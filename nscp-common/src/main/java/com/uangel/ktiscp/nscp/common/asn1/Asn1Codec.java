package com.uangel.ktiscp.nscp.common.asn1;

import java.nio.ByteBuffer;

/**
 * ASN.1 메시지를 ByteBuffer로 Encode/Decode하는 모듈 interface
 */
public interface Asn1Codec {
	/**
	 * encode 함수
	 * @param msg : encode할 대상
	 * @return : encoding된 ByteBuffer
	 */
	ByteBuffer encode(Asn1Message msg);
	
	/**
	 * decode 함수
	 * @param msg : decode 결과를 받을 메시지 객체. 이걸 return으로하지 않고 input으로 받는 이유는 어떤 종류의 메시지인지 알아야 decode가 가능하기 때문이다.
	 *              이 파라미터를 넘기기 위해서는 Asn1MessageFactory를 통해서 객체를 생성한다. (serviceId, int opCode 필요)
	 * @param buf : decode할 대상
	 */
	void decode(Asn1Message msg, ByteBuffer buf); 
}
