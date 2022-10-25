package com.uangel.ktiscp.nscp.nscpib.sock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.asn1.Asn1Codec;
import com.uangel.ktiscp.nscp.common.asn1.Asn1MessageFactory;
import com.uangel.ktiscp.nscp.common.sock.NscpMessageFactory;
import com.uangel.ktiscp.nscp.common.sock.NscpMsgDecoder;
import com.uangel.ktiscp.nscp.common.sock.NscpMsgEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * TCP 채널 연결 시 해당 채널을 처리하기 위한 파이프라인 정의 
 */
@Component
public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {
	
	@Autowired
	Asn1MessageFactory asn1MessageFactory;
	@Autowired
	Asn1Codec asn1Codec;
	@Autowired
	NscpMessageFactory nscpMessageFactory;
	@Autowired
	NscpibTrManager nscpibTrManager;
	
	@Value("${tcp.server.idle-time:60}")
	private int idleTime; // ping-pong idle time
	
	@Value("${tcp.server.conn-req-wait-time:10}")
	private int connReqWaitTime;
	
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new NscpMsgDecoder(asn1MessageFactory, asn1Codec));
		pipeline.addLast(new NscpMsgEncoder(asn1Codec));
		pipeline.addLast("idleStateHandler", new IdleStateHandler(idleTime, 0, 0));
		pipeline.addLast(new TcpServerHandler(nscpMessageFactory, nscpibTrManager, connReqWaitTime));
	}
}
