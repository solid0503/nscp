package com.uangel.ktiscp.nscp.common.bepsock;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BepMsgEncoder extends MessageToByteEncoder<BepMessage> {
	
	public static final String FORMAT_SUBS_SYSTEM_ID = "%-" + BepMessage.LENGTH_OF_SUB_SYSTEM_ID + "s";
	public static final String FORMAT_MESSAGE_TYPE = "%-" + BepMessage.LENGTH_OF_MESSAGE_TYPE + "s";
	public static final String FORMAT_REQUEST_TYPE = "%-" + BepMessage.LENGTH_OF_REQUEST_TYPE + "s";
	public static final String FORMAT_COMMAND = "%-" + BepMessage.LENGTH_OF_COMMAND + "s";
	public static final String FORMAT_TRANSACTION_ID = "%-" + BepMessage.LENGTH_OF_TRANSACTION_ID + "s";
	public static final String FORMAT_SERVICE_ID = "%-" + BepMessage.LENGTH_OF_SERVICE_ID + "s";
	public static final String FORMAT_ROUTING_KEY = "%-" + BepMessage.LENGTH_OF_ROUTING_KEY + "s";
	public static final String FORMAT_BODY_LENGTH = "%-" + BepMessage.LENGTH_OF_BODY_LENGTH + "s";
	
	
	public BepMsgEncoder() {
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, BepMessage msg, ByteBuf out) throws Exception {
		out.writeBytes(String.format(FORMAT_SUBS_SYSTEM_ID, msg.subSystemId).getBytes(BepMessage.CHARSET));
		out.writeBytes(String.format(FORMAT_MESSAGE_TYPE, msg.messageType).getBytes(BepMessage.CHARSET));
		out.writeBytes(String.format(FORMAT_REQUEST_TYPE, msg.requestType).getBytes(BepMessage.CHARSET));
		out.writeBytes(String.format(FORMAT_COMMAND, msg.command).getBytes(BepMessage.CHARSET));
		out.writeBytes(String.format(FORMAT_TRANSACTION_ID, msg.transactionId).getBytes(BepMessage.CHARSET));
		out.writeBytes(String.format(FORMAT_SERVICE_ID, msg.serviceId).getBytes(BepMessage.CHARSET));
		out.writeBytes(String.format(FORMAT_ROUTING_KEY, msg.routingKey).getBytes(BepMessage.CHARSET));
		
		if ( msg.json != null ) {
			String jsonString = msg.json.toString();
			byte[] jsonBytes = jsonString.getBytes(BepMessage.CHARSET);
			msg.bodyLength = "" + jsonBytes.length;
			out.writeBytes(String.format(FORMAT_BODY_LENGTH, msg.bodyLength).getBytes(BepMessage.CHARSET));
			out.writeBytes(jsonBytes);
		} else {
			msg.bodyLength = "0";
			out.writeBytes(String.format(FORMAT_BODY_LENGTH, msg.bodyLength).getBytes(BepMessage.CHARSET));
		}
	}
}
