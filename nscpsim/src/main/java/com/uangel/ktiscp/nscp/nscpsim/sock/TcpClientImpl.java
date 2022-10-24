package com.uangel.ktiscp.nscp.nscpsim.sock;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.asn1.Asn1Codec;
import com.uangel.ktiscp.nscp.common.asn1.Asn1MessageFactory;
import com.uangel.ktiscp.nscp.common.sock.NscpMessage;
import com.uangel.ktiscp.nscp.common.sock.NscpMsgDecoder;
import com.uangel.ktiscp.nscp.common.sock.NscpMsgEncoder;
import com.uangel.ktiscp.nscp.nscpsim.NscpSimContext;
import com.uangel.ktiscp.nscp.nscpsim.NscpSimCounter;
import com.uangel.ktiscp.nscp.nscpsim.NscpSimTrManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
public class TcpClientImpl implements TcpClient {
	
	@Value("${tcp.client.ip:127.0.0.1}")
	private String ip;
	@Value("${tcp.client.port:35000}")
	private int port;
	@Value("${tcp.client.worker-thread-count:4}")
	private int workerThreadCount;
	
	@Autowired
	Asn1MessageFactory asn1MessageFactory;
	@Autowired
	Asn1Codec asn1Codec;
	@Autowired
	NscpSimContext context;
	@Autowired
	NscpSimTrManager trManager;
	@Autowired
	NscpSimCounter counter;
	
	private Channel channel;
	
	Bootstrap b = new Bootstrap();
	
	public TcpClientImpl() {
		
	}
	
	@PostConstruct
	public void init() {
		log.info("init called!!");
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadCount);

        try {
        	TcpClientImpl tcpClient = this;
            b.group(workerGroup).channel(NioSocketChannel.class).remoteAddress(ip, port)
                    .handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
						      ChannelPipeline pipeline = ch.pipeline();

						      pipeline.addLast(new NscpMsgDecoder(asn1MessageFactory, asn1Codec));
						      pipeline.addLast(new NscpMsgEncoder(asn1Codec));
						      pipeline.addLast(new TcpClientHandler(tcpClient));
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
	
	void connect() {
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
	
	public void send(NscpMessage msg) {
		if( channel != null && channel.isActive() ) {
			channel.writeAndFlush(msg);
			if ( !context.isPrefMode() ) {
//				log.info("\n=>>=======================>>============================================================================"
//						   + "\n    SIM => NSCP"
//					       + "\n=>>=======================>>============================================================================"
//					       + "{}"
//					       + "\n=>>=======================>>============================================================================",
//							msg.getTraceString());
				System.out.println(String.format("\n=>>=======================>>============================================================================"
						   + "\n    SIM => NSCP"
					       + "\n=>>=======================>>============================================================================"
					       + "%s"
					       + "\n=>>=======================>>============================================================================",
							msg.getTraceString()));
			}
		} else {
			throw new RuntimeException( "Can't send message to inactive connection");
		}
	}
	
	public void printRecvMessage(NscpMessage msg) {
		if ( !context.isPrefMode() ) {
//			log.info("\n=<<=======================<<============================================================================"
//					   + "\n    SIM <= NSCP"
//				       + "\n=<<=======================<<============================================================================"
//				       + "{}"
//				       + "\n=<<=======================<<============================================================================",
//						msg.getTraceString());
			System.out.println(String.format("\n=<<=======================<<============================================================================"
					   + "\n    SIM <= NSCP"
				       + "\n=<<=======================<<============================================================================"
				       + "%s"
				       + "\n=<<=======================<<============================================================================",
						msg.getTraceString()));
		}
	}
	
	public boolean isConnected() {
		if( channel != null && channel.isActive() ) {
			return true;
		} else {
			return false;
		}
	}
	
	public NscpSimTrManager getTrManager() {
		return this.trManager;
	}
	
	public NscpSimCounter getCounter() {
		return counter;
	}
}
