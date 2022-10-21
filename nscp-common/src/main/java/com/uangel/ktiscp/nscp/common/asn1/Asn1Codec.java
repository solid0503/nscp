package com.uangel.ktiscp.nscp.common.asn1;

import java.nio.ByteBuffer;

public interface Asn1Codec {
	ByteBuffer encode(Asn1Message msg);
	void decode(Asn1Message msg, ByteBuffer buf);
}
