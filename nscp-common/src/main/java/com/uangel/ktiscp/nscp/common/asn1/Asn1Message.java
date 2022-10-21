package com.uangel.ktiscp.nscp.common.asn1;

import com.uangel.asn1.Asn1Object;

public interface Asn1Message {
	void setValue(String name, Object value);
	Object getValue(String name);
	String getName();
	
	void encode(Asn1Object asn1Object);
	void decode(Asn1Object asn1Object);
	String getTraceString();
}
