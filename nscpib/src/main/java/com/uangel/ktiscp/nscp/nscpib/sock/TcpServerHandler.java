package com.uangel.ktiscp.nscp.nscpib.sock;

import com.uangel.ktiscp.nscp.common.sock.MessageId;
import com.uangel.ktiscp.nscp.common.sock.MessageType;
import com.uangel.ktiscp.nscp.common.sock.NscpMessage;
import com.uangel.ktiscp.nscp.common.sock.NscpMessageFactory;
import com.uangel.ktiscp.nscp.common.transaction.Transaction;
import com.uangel.ktiscp.nscp.common.transaction.TransactionTimeoutHandler;

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
public class TcpServerHandler extends ChannelInboundHandlerAdapter {
	
	NscpMessageFactory nscpMessageFactory;
	NscpibTrManager nscpibTrManager;
	boolean isAuthorized = false;
	int connReqWaitTime;
	
	/**
	 * 생성자. TcpServerHandler는 Component가 아니라서 new하는 곳에서 필요한 instance를 주입받는다.
	 * @param nscpMessageFactory
	 * @param nscpibTrManager
	 */
	public TcpServerHandler(NscpMessageFactory nscpMessageFactory, NscpibTrManager nscpibTrManager, int connReqWaitTime) {
		this.nscpMessageFactory = nscpMessageFactory;
		this.nscpibTrManager = nscpibTrManager;
		this.connReqWaitTime = connReqWaitTime;
	}

	/**
	 * 클라이언트 접속 시 호출되는 함수
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug("channelActive()");
		log.info("Client connected. RemoteAddress={}, LocalAddress={}", ctx.channel().remoteAddress(), ctx.channel().localAddress());
		
		Transaction tr = new Transaction();
		tr.setTimeoutTime(connReqWaitTime*1000);
		// 접속 요청 메시지가 없는 경우 timeout 처리
		tr.setTimeoutHandler(new TransactionTimeoutHandler() {
			@Override
			public void handleTimeout(Transaction tr) {
				log.info("A client did not send a CONREQ. RemoteAddress={}", ctx.channel().remoteAddress());
				ctx.close();
			}
		});
		nscpibTrManager.addTransaction("CONNREQ:"+ctx.hashCode(), tr);
	}

	/**
	 * 클라이언트와 연결 해제 시 호출되는 함수
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug("channelInactive()");
		log.info("Client disconnected. RemoteAddress={}, LocalAddress={}", ctx.channel().remoteAddress(), ctx.channel().localAddress());

		// 연결요청 대기중인 트랜젝션이 있다면 제거한다.
		if ( this.isAuthorized == false ) {
			nscpibTrManager.removeTransaction("CONNREQ:"+ctx.hashCode()); 
		}
	}

	/**
	 * 클라이언트가 메시지를 전송했을때 호출되는 함수
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		log.debug("channelRead()");
		
		NscpMessage nscpMessage = (NscpMessage)msg;
		this.printRecvMsg(ctx, nscpMessage);  // 이쁘게 출력
		
		// 연결 요청 메시지
		if ( nscpMessage.getMessageId() == MessageId.CONNECTION_REQUEST.getValue() ) {
			// TODO : IP 인증 등을 수행한다. (기존 노드 관리 기능 확인 필요)
			this.isAuthorized = true;
			nscpibTrManager.removeTransaction("CONNREQ:"+ctx.hashCode()); // 연결 요청이 왔으므로 해당 트렌젝션은 제거한다. 
			this.sendResponse(ctx, nscpMessage, MessageType.NONE);
			return;
		} 
		// 연결 해제 요청 메시지
		else if (nscpMessage.getMessageId() == MessageId.RELEASE_REQUEST.getValue() ) {
			this.sendResponse(ctx, nscpMessage, MessageType.NONE);
			ctx.close();
			return;
		}
		
		// Ping 메시지가 온 경우 응답한다.
		if ( nscpMessage.getMessageId() == MessageId.CONNECTION_CHECK_REQUEST.getValue() ) {
			this.sendResponse(ctx, nscpMessage, MessageType.NONE);
			return;
		}
		// Ping 메시지에 대한 응답 처리
		else if ( nscpMessage.getMessageId() == MessageId.CONNECTION_CHECK_RESPONSE.getValue() ) {
			nscpibTrManager.removeTransaction(nscpMessage.getTransactionId()); // Tr에서 제거만 하면된다.
			return;
		}
		
		// Connection 관련 외의 요청은 인증 후에만 가능하다.
		if( nscpMessage.isRequest() && this.isAuthorized == false ) {
			this.sendResponse(ctx, nscpMessage, MessageType.REJECT);
			return;
		}
		
		// TODO : BEPIB로 던지는 로직 개발 필요
		NscpMessage res = nscpMessage.getResponse(MessageType.NONE);
		ctx.writeAndFlush(res);
		this.printSendMsg(ctx, res);
	}
	
	private void sendResponse(ChannelHandlerContext ctx, NscpMessage req, MessageType messageType) {
		NscpMessage res = req.getResponse(messageType);
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
		    	log.info("Recv idle... RemoteAddress={}", ctx.channel().remoteAddress());
		    	NscpMessage pingMessage = this.nscpMessageFactory.createPingMessage(); // ping 메시지 생성
		    	Transaction tr = new Transaction();
		    	// ping에 대한 timeout handle은 특수하므로 여기에서 정의한다.
		    	tr.setTimeoutHandler(new TransactionTimeoutHandler() {
					@Override
					public void handleTimeout(Transaction tr) {
						log.info("Ping message timeout!!. RemoteAddress={}", ctx.channel().remoteAddress());
						ctx.close();
					}
		    	});
		    	nscpibTrManager.addTransaction(pingMessage.getTransactionId(), tr); // 트랜젝션 등록
		    	
		    	ctx.writeAndFlush(pingMessage); // ping 메시지 전송
		    	this.printSendMsg(ctx, pingMessage); // 이쁘게 출력
		    }		            
	    }
	}
	
	private void printRecvMsg(ChannelHandlerContext ctx, NscpMessage nscpMessage) {
		if ( log.isInfoEnabled() ) {
			log.info("\n=>>=======================>>============================================================================"
				   + "\n    client({}) => NSCP({})"
			       + "\n=>>=======================>>============================================================================"
			       + "{}"
			       + "\n=>>=======================>>============================================================================",
					ctx.channel().remoteAddress(), ctx.channel().localAddress(),nscpMessage.getTraceString());
		}
	}
	
	private void printSendMsg(ChannelHandlerContext ctx, NscpMessage nscpMessage) {
		if ( log.isInfoEnabled() ) {
			log.info("\n=<<=======================<<============================================================================"
					   + "\n    client({}) <= NSCP({})"
				       + "\n=<<=======================<<============================================================================"
				       + "{}"
				       + "\n=<<=======================<<============================================================================",
						ctx.channel().remoteAddress(), ctx.channel().localAddress(),nscpMessage.getTraceString());
		}
	}
}
