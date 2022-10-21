package com.uangel.ktiscp.nscp.common.sock;

import java.nio.ByteBuffer;
import java.util.List;

import com.uangel.ktiscp.nscp.common.asn1.Asn1Codec;
import com.uangel.ktiscp.nscp.common.asn1.Asn1Message;
import com.uangel.ktiscp.nscp.common.asn1.Asn1MessageFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class NscpMsgDecoder extends ByteToMessageDecoder {
	
	public NscpMsgDecoder(Asn1MessageFactory asn1MessageFactory, Asn1Codec asn1Codec) {
		this.asn1MessageFactory = asn1MessageFactory;
		this.asn1Codec = asn1Codec;
	}
	
	Asn1MessageFactory asn1MessageFactory;
	Asn1Codec asn1Codec;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if ( in.readableBytes() < NscpMessage.HEADER_SIZE ) {
			return;
		}
		
		in.markReaderIndex(); // 데이터가 끝까지 오지 않은 경우를 대비해 mark
		
		NscpMessage msg = new NscpMessage();
		msg.messageVersion = in.readUnsignedByte();
		msg.linkedId = in.readUnsignedByte();
		msg.messageId = in.readUnsignedByte();
		msg.messageType = in.readUnsignedByte();
		msg.serviceId = in.readUnsignedByte();
		msg.operationCode = in.readUnsignedByte();
		msg.routingInformation = new byte[6];
		in.readBytes(msg.routingInformation);
		msg.oTID = in.readInt();
		msg.dTID = in.readInt();
		msg.timeStamp = in.readInt();
		msg.dataLength = in.readInt();
		
		if ( in.readableBytes() < msg.dataLength ) { // data가 모두 도착했는지 체크
			in.resetReaderIndex(); // mark했던 위치로 되돌리고 return
			return;
		}
		
		if ( msg.dataLength > 0 ) {
			byte[] data = new byte[msg.dataLength];
			in.readBytes(data);
			
			Asn1Message asn1Message = asn1MessageFactory.newRecvMessage(msg.serviceId, msg.operationCode);
			
			asn1Codec.decode(asn1Message, ByteBuffer.wrap(data));
			msg.setAsn1Message(asn1Message);
		}
		
		out.add(msg);
	}
}
