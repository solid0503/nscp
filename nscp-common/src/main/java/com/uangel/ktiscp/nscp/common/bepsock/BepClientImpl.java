package com.uangel.ktiscp.nscp.common.bepsock;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.sock.NscpMessage;
import com.uangel.ktiscp.nscp.common.transaction.Transaction;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.nio.NioEventLoopGroup;

@Component
@Slf4j
public class BepClientImpl implements BepClient {
	
	@Value("${tcp.client.ip:127.0.0.1}")
	protected String ip;
	@Value("${tcp.client.port:36000}")
	protected int port;
	@Value("${tcp.client.worker-thread-count:4}")
	protected int workerThreadCount;
	
	private Channel channel;
	
	@Autowired
	CallbackTrManager callbackTrManager;
	
	@Autowired(required = false)
	RecvCallback recvCallback;
	
	Bootstrap b = new Bootstrap();
	
	public BepClientImpl() {
		
	}
	
	@PostConstruct
	public void init() {
		log.info("init called!!");
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadCount);

        try {
        	BepClientImpl tcpClient = this;
            b.group(workerGroup).channel(NioSocketChannel.class).remoteAddress(ip, port)
                    .handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
						      ChannelPipeline pipeline = ch.pipeline();

						      pipeline.addLast(new BepMsgDecoder());
						      pipeline.addLast(new BepMsgEncoder());
						      pipeline.addLast(new BepClientHandler(tcpClient));
						}
                    });
            b.option(ChannelOption.TCP_NODELAY, true);
            
            this.connect();
        }
        catch (Exception e) {
            log.error("Exception!!", e);
        }
        log.info("return init()");
	}
	
	@Override
	public void connect() {
		b.connect().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if ( future.cause() != null ) {
					log.error("Failed to connect: " + future.cause());
				}
				channel = future.channel();
			}
		});
	}
	
	@Override
	public void send(BepMessage msg) {
		if( channel != null && channel.isActive() ) {
			channel.writeAndFlush(msg);
			printSendMessage(msg);
		} else {
			throw new RuntimeException( "Can't send message to inactive connection");
		}
	}
	
	@Override
	public void sendWithCallback(BepMessage msg, RecvCallback callback, RecvTimeoutCallback timeoutCallback) {
		Transaction tr = new Transaction();
		if ( callback != null ) {
			tr.putData("callback", callback);
		}
		if ( timeoutCallback != null ) {
			tr.putData("timeoutCallback", timeoutCallback);
		}
		this.send(msg);
	}
	
	void printSendMessage(BepMessage msg) {
		log.info("\n=>>=======================>>============================================================================"
				   + "\n    Client => BEP"
			       + "\n=>>=======================>>============================================================================"
			       + "{}"
			       + "\n=>>=======================>>============================================================================",
					msg.getTraceString());
	}
	
	void printRecvMessage(BepMessage msg) {
		log.info("\n=<<=======================<<============================================================================"
				   + "\n    Client <= BEP"
			       + "\n=<<=======================<<============================================================================"
			       + "{}"
			       + "\n=<<=======================<<============================================================================",
					msg.getTraceString());
		
	}
	
	boolean isConnected() {
		if( channel != null && channel.isActive() ) {
			return true;
		} else {
			return false;
		}
	}
}
