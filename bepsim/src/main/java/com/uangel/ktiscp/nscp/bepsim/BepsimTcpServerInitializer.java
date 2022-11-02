package com.uangel.ktiscp.nscp.bepsim;

import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.bepsock.BepMsgDecoder;
import com.uangel.ktiscp.nscp.common.bepsock.BepMsgEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * TCP 채널 연결 시 해당 채널을 처리하기 위한 파이프라인 정의 
 */
@Component
public class BepsimTcpServerInitializer extends ChannelInitializer<SocketChannel> {
	
	
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new BepMsgDecoder());
		pipeline.addLast(new BepMsgEncoder());
		pipeline.addLast("idleStateHandler", new IdleStateHandler(60, 0, 0));
		pipeline.addLast(new BepsimTcpServerHandler());
	}
}
