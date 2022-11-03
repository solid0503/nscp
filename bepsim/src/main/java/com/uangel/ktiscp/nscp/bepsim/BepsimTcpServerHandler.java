package com.uangel.ktiscp.nscp.bepsim;

import com.uangel.ktiscp.nscp.common.bepsock.BepMessage;
import com.uangel.ktiscp.nscp.common.json.JsonType;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * NSCP 외부 연동에 대한 TCP 메시지 Handler
 *
 */
@Slf4j
public class BepsimTcpServerHandler extends ChannelInboundHandlerAdapter {
	
	
	public BepsimTcpServerHandler() {
	
	}

	/**
	 * 클라이언트 접속 시 호출되는 함수
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("Client connected. RemoteAddress={}, LocalAddress={}", ctx.channel().remoteAddress(), ctx.channel().localAddress());
	}

	/**
	 * 클라이언트와 연결 해제 시 호출되는 함수
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug("channelInactive()");
		log.info("Client disconnected. RemoteAddress={}, LocalAddress={}", ctx.channel().remoteAddress(), ctx.channel().localAddress());
	}

	/**
	 * 클라이언트가 메시지를 전송했을때 호출되는 함수
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.debug("channelRead()");
		
		BepMessage bepMessage = (BepMessage)msg;
		this.printRecvMsg(ctx, bepMessage);  // 이쁘게 출력
		
		JsonType json = bepMessage.getJson();
		
		BepMessage res = bepMessage.getResponse();
		
		json.removeObject("body");
		JsonType jsonHeader = json.get("header");
		jsonHeader.setValue("messageType", 0); 
		jsonHeader.setValue("dTID", jsonHeader.getValue("oTID"));
		jsonHeader.setValue("oTID", 1234); 
		res.setJson(json);
		
		ctx.writeAndFlush(res);
		this.printSendMsg(ctx, res);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("exceptionCaught() RemoteAddress=" + ctx.channel().remoteAddress() , cause);
	}

	/**
	 * 메시지 수신 Idle 처리를 위해 정의
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
		    IdleStateEvent e = (IdleStateEvent) evt;
		    // 수신 Idle 발생인 경우
		    if (e.state() == IdleState.READER_IDLE) {
		    	log.info("idle... RemoteAddress={}", ctx.channel().remoteAddress());
		    }		            
	    }
	}
	
	private void printRecvMsg(ChannelHandlerContext ctx, BepMessage bepMessage) {
		if ( log.isInfoEnabled() ) {
			log.info("\n=>>=======================>>============================================================================"
				   + "\n    NE({}) => SIM({})"
			       + "\n=>>=======================>>============================================================================"
			       + "{}"
			       + "\n=>>=======================>>============================================================================",
					ctx.channel().remoteAddress(), ctx.channel().localAddress(),bepMessage.getTraceString());
		}
	}
	
	private void printSendMsg(ChannelHandlerContext ctx, BepMessage bepMessage) {
		if ( log.isInfoEnabled() ) {
			log.info("\n=<<=======================<<============================================================================"
					   + "\n    NE({}) <= SIM({})"
				       + "\n=<<=======================<<============================================================================"
				       + "{}"
				       + "\n=<<=======================<<============================================================================",
						ctx.channel().remoteAddress(), ctx.channel().localAddress(),bepMessage.getTraceString());
		}
	}
}
