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
		msg.subSystemId = in.readCharSequence(20, Charset.forName("utf-8")).toString().trim();
		msg.messageType = in.readCharSequence(10, Charset.forName("utf-8")).toString().trim();
		msg.requestType = in.readCharSequence(8, Charset.forName("utf-8")).toString().trim();
		msg.command = in.readCharSequence(20, Charset.forName("utf-8")).toString().trim();
		msg.transactionId = in.readCharSequence(20, Charset.forName("utf-8")).toString().trim();
		msg.serviceId = in.readCharSequence(10, Charset.forName("utf-8")).toString().trim();
		msg.routingKey = in.readCharSequence(20, Charset.forName("utf-8")).toString().trim();
		msg.bodyLength = in.readCharSequence(5, Charset.forName("utf-8")).toString().trim();
		
		
		log.info("msg.subSystemId={}", msg.subSystemId);
		log.info("msg.messageType={}", msg.messageType);
		log.info("msg.requestType={}", msg.requestType);
		log.info("msg.command={}", msg.command);
		log.info("msg.transactionId={}", msg.transactionId);
		log.info("msg.serviceId={}", msg.serviceId);
		log.info("msg.routingKey={}", msg.routingKey);
		log.info("msg.bodyLength={}", msg.bodyLength);
		int nBodyLength = Integer.parseInt(msg.bodyLength, 10);
		
		if ( in.readableBytes() < nBodyLength ) { // data가 모두 도착했는지 체크
			in.resetReaderIndex(); // mark했던 위치로 되돌리고 return
			return;
		}
		
		if ( nBodyLength > 0 ) {
			byte[] body = new byte[nBodyLength];
			in.readBytes(body);
			String strBody = new String(body, "utf-8");
			log.info("strBody={}", strBody);
			msg.json = JsonType.getJsonObject(strBody);
		}
		
		out.add(msg);
	}
}
