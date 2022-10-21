package com.uangel.ktiscp.nscp.nscpib.sock;

import com.uangel.ktiscp.nscp.common.sock.NscpMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug("channelActive()");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug("channelInactive()");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.debug("channelRead()");
		
		NscpMessage nscpMessage = (NscpMessage)msg;
		if ( log.isInfoEnabled() ) {
			log.info("recvMsg:{}", nscpMessage.getTraceString());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("exceptionCaught()", cause);
	}

}
