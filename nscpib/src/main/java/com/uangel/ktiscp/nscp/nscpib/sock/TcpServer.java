package com.uangel.ktiscp.nscp.nscpib.sock;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.nio.NioEventLoopGroup;

@Component
@Slf4j
public class TcpServer extends Thread {
	
	@Value("${tcp.server.port:35000}")
	private int serverPort;
	@Value("${tcp.server.boss-thread-count:1}")
	private int bossThreadCount;
	@Value("${tcp.server.worker-thread-count:4}")
	private int workerThreadCount;
	
	
	@Autowired
	TcpServerInitializer tcpServerInitializer;
	
	public TcpServer() {
		super("NSCP-Tcp-Server");
	}
	
	@PostConstruct
	public void initServer() {
		this.start();
	}
	
	@Override
	public void run() {	
		EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreadCount);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadCount);
        ChannelFuture channelFuture = null;

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(tcpServerInitializer);
            
            b.childOption(ChannelOption.TCP_NODELAY, true);

            Channel ch = b.bind(serverPort).sync().channel();

            channelFuture = ch.closeFuture();
            channelFuture.sync();
        }
        catch (InterruptedException e) {
            log.error("InterruptedException!!", e);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        log.info("initServer return!!");
	}
}
