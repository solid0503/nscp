package com.uangel.ktiscp.nscp.common.sock;

import org.springframework.stereotype.Component;

@Component
public class NscpMessageFactoryImpl implements NscpMessageFactory {

	@Override
	public NscpMessage createMessage() {
		return new NscpMessage();
	}

	@Override
	public NscpMessage createPingMessage() {
		NscpMessage ping = new NscpMessage();
		ping.messageVersion = 1;
		ping.linkedId = 0;
		ping.messageId = MessageId.CONNECTION_CHECK_REQUEST.getValue();
		ping.messageType = MessageType.TERMINATION.getValue();
		ping.serviceId = ServiceId.NONE.getValue();
		ping.operationCode = OperationCode.NONE.getValue();
		return ping;
	}	
}
