package com.uangel.ktiscp.nscp.common.bepsock;

import java.nio.charset.Charset;
import java.util.List;

import com.uangel.ktiscp.nscp.common.json.JsonType;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BepMsgDecoder extends ByteToMessageDecoder {
	
	public BepMsgDecoder() {
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if ( in.readableBytes() < BepMessage.HEADER_SIZE ) {
			return;
		}
		
		in.markReaderIndex(); // 데이터가 끝까지 오지 않은 경우를 대비해 mark
		
		BepMessage msg = new BepMessage();
		msg.subSystemId = in.readCharSequence(BepMessage.LENGTH_OF_SUB_SYSTEM_ID, BepMessage.CHARSET).toString().trim();
		msg.messageType = in.readCharSequence(BepMessage.LENGTH_OF_MESSAGE_TYPE, BepMessage.CHARSET).toString().trim();
		msg.requestType = in.readCharSequence(BepMessage.LENGTH_OF_REQUEST_TYPE, BepMessage.CHARSET).toString().trim();
		msg.command = in.readCharSequence(BepMessage.LENGTH_OF_COMMAND, BepMessage.CHARSET).toString().trim();
		msg.transactionId = in.readCharSequence(BepMessage.LENGTH_OF_TRANSACTION_ID, BepMessage.CHARSET).toString().trim();
		msg.serviceId = in.readCharSequence(BepMessage.LENGTH_OF_SERVICE_ID, BepMessage.CHARSET).toString().trim();
		msg.routingKey = in.readCharSequence(BepMessage.LENGTH_OF_ROUTING_KEY, BepMessage.CHARSET).toString().trim();
		msg.bodyLength = in.readCharSequence(BepMessage.LENGTH_OF_BODY_LENGTH, BepMessage.CHARSET).toString().trim();
		
		int nBodyLength = Integer.parseInt(msg.bodyLength, 10);
		
		if ( in.readableBytes() < nBodyLength ) { // data가 모두 도착했는지 체크
			in.resetReaderIndex(); // mark했던 위치로 되돌리고 return
			return;
		}
		
		if ( nBodyLength > 0 ) {
			byte[] body = new byte[nBodyLength];
			in.readBytes(body);
			String strBody = new String(body, BepMessage.CHARSET);
			msg.json = JsonType.getJsonObject(strBody);
		}
		
		out.add(msg);
	}
}
