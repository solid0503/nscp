package com.uangel.ktiscp.nscp.common.sock;


public interface NscpMessageFactory {
	NscpMessage createMessage();
	NscpMessage createPingMessage();
}
