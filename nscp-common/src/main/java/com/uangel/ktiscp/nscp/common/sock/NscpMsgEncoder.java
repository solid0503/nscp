package com.uangel.ktiscp.nscp.common.sock;

import java.nio.ByteBuffer;

import com.uangel.ktiscp.nscp.common.asn1.Asn1Codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NscpMsgEncoder extends MessageToByteEncoder<NscpMessage> {
	
	public NscpMsgEncoder(Asn1Codec asn1Codec) {
		this.asn1Codec = asn1Codec;
	}
	
	Asn1Codec asn1Codec;

	@Override
	protected void encode(ChannelHandlerContext ctx, NscpMessage msg, ByteBuf out) throws Exception {
		out.writeByte(msg.messageVersion);
		out.writeByte(msg.linkedId);
		out.writeByte(msg.messageId);
		out.writeByte(msg.messageType);
		out.writeByte(msg.serviceId);
		out.writeByte(msg.operationCode);
		if ( msg.routingInformation != null ) {
			out.writeBytes(msg.routingInformation);
		} else {
			out.writeBytes(new byte[6]);
		}
		out.writeInt(msg.oTID);
		out.writeInt(msg.dTID);
		out.writeInt(msg.timeStamp);
		
		if ( msg.asn1Message != null ) {
			ByteBuffer body = asn1Codec.encode(msg.asn1Message);
			msg.dataLength = body.limit();
			out.writeInt(msg.dataLength);
			out.writeBytes(body);
		} else {
			msg.dataLength = 0;
			out.writeInt(msg.dataLength);
		}
	}
}
