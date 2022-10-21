package com.uangel.ktiscp.nscp.common.sock;

import org.springframework.stereotype.Component;

@Component
public class NscpMessageFactoryImpl implements NscpMessageFactory {

	@Override
	public NscpMessage createMessage() {
		return new NscpMessage();
	}	
}
