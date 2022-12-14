package com.uangel.ktiscp.nscp.nscpsim.sock;

import java.util.concurrent.TimeUnit;

import com.uangel.ktiscp.nscp.common.sock.MessageId;
import com.uangel.ktiscp.nscp.common.sock.MessageType;
import com.uangel.ktiscp.nscp.common.sock.NscpMessage;
import com.uangel.ktiscp.nscp.common.transaction.Transaction;
import com.uangel.ktiscp.nscp.nscpsim.SimScenario;

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
		System.out.println("Connected.");
		tcpClient.sendConnReq(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("channelInactive()");
		System.out.println("Disconnected.");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.info("channelRead()");
		NscpMessage nscpMessage = (NscpMessage)msg;
		tcpClient.printRecvMessage(nscpMessage);
		
		if ( nscpMessage.getMessageId() == MessageId.CONNECTION_CHECK_REQUEST.getValue() ) {
			if ( this.tcpClient.getSimContext().getSysQueryRes() == 1 ) {
				NscpMessage pingRes = nscpMessage.getResponse(MessageType.NONE);
				tcpClient.send(pingRes);
			}
			return;
		}
		
		Transaction tr = this.tcpClient.getTrManager().removeTransaction(nscpMessage.getTransactionId());
		if ( tr == null ) {
			log.error("Not found tr. key:{}", nscpMessage.getTransactionId());
			return;
		}
		tcpClient.getCounter().incResponseTimeCounter(tr.getDiffTime());
		tcpClient.getCounter().incMsgRecv();
		
		SimScenario scen = (SimScenario)tr.getData("scen");
		scen.runNextAction(nscpMessage);
		while ( scen.getNextActionName().equals("send") ) {
			scen.runNextAction(null);
			return;
		}
		scen.runNextAction(null);
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
				System.out.println("Reconnecting...");
				tcpClient.connect();
			}
		}, 1, TimeUnit.SECONDS);
	}
}
