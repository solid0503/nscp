package com.uangel.ktiscp.nscp.nscpsim.sock;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpClientHandler extends ChannelInboundHandlerAdapter {
	TcpClientImpl tcpClient;
	
	public TcpClientHandler(TcpClientImpl tcpClient) {
		this.tcpClient = tcpClient;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("channelActive()");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("channelInactive()");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.info("channelRead()");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("exceptionCaught()", cause);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log.info("channelUnregistered");
		
		ctx.channel().eventLoop().schedule(new Runnable() {
			@Override
			public void run() {
				log.info("Reconnecting...");;
				tcpClient.connect();
			}
		}, 1, TimeUnit.SECONDS);
	}
}
