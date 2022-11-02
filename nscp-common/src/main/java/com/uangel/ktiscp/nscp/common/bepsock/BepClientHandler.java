package com.uangel.ktiscp.nscp.common.bepsock;

import java.util.concurrent.TimeUnit;
import com.uangel.ktiscp.nscp.common.transaction.Transaction;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BepClientHandler extends ChannelInboundHandlerAdapter {
	
	BepClientImpl bepClient;
	
	public BepClientHandler(BepClientImpl bepClient) {
		this.bepClient = bepClient;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("channelActive() remoteAddress={}", ctx.channel().remoteAddress());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("channelInactive() remoteAddress={}", ctx.channel().remoteAddress());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		BepMessage message = (BepMessage)msg;
		bepClient.printRecvMessage(message);
		
		Transaction tr = bepClient.callbackTrManager.removeTransaction(message.getTransactionId());
		if ( tr != null ) {
			RecvCallback cb = (RecvCallback)tr.getData("callback");
			if ( cb != null ) {
				cb.channelRead(ctx, message);
				return;
			}
		}
		
		if ( bepClient.recvCallback != null ) {
			bepClient.recvCallback.channelRead(ctx, message);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("exceptionCaught() remoteAddress=" + ctx.channel().remoteAddress(), cause);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log.info("channelUnregistered.");
		
		ctx.channel().eventLoop().schedule(new Runnable() {
			@Override
			public void run() {
				log.info("Reconnecting...");
				bepClient.connect();
			}
		}, 1, TimeUnit.SECONDS);
	}
}
