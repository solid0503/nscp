package com.uangel.ktiscp.nscp.common.bepsock;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BepMsgEncoder extends MessageToByteEncoder<BepMessage> {
	
	public BepMsgEncoder() {
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, BepMessage msg, ByteBuf out) throws Exception {
		out.writeBytes(String.format("%-20s", msg.subSystemId).getBytes("utf-8"));
		out.writeBytes(String.format("%-10s", msg.messageType).getBytes("utf-8"));
		out.writeBytes(String.format("%-8s", msg.requestType).getBytes("utf-8"));
		out.writeBytes(String.format("%-20s", msg.command).getBytes("utf-8"));
		out.writeBytes(String.format("%-20s", msg.transactionId).getBytes("utf-8"));
		out.writeBytes(String.format("%-10s", msg.serviceId).getBytes("utf-8"));
		out.writeBytes(String.format("%-20s", msg.routingKey).getBytes("utf-8"));
		
		if ( msg.json != null ) {
			String jsonString = msg.json.toString();
			byte[] jsonBytes = jsonString.getBytes("utf-8");
			msg.bodyLength = "" + jsonBytes.length;
			out.writeBytes(String.format("%-5s", msg.bodyLength).getBytes("utf-8"));
			out.writeBytes(jsonBytes);
		} else {
			msg.bodyLength = "0";
			out.writeBytes(String.format("%-5s", msg.bodyLength).getBytes("utf-8"));
		}
	}
}
