package com.uangel.ktiscp.nscp.nscpib.sock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uangel.ktiscp.nscp.common.asn1.Asn1Codec;
import com.uangel.ktiscp.nscp.common.asn1.Asn1MessageFactory;
import com.uangel.ktiscp.nscp.common.sock.NscpMsgDecoder;
import com.uangel.ktiscp.nscp.common.sock.NscpMsgEncoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

@Component
public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {
	
	@Autowired
	Asn1MessageFactory asn1MessageFactory;
	@Autowired
	Asn1Codec asn1Codec;
	
	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		pipeline.addLast(new NscpMsgDecoder(asn1MessageFactory, asn1Codec));
		pipeline.addLast(new NscpMsgEncoder(asn1Codec));
		pipeline.addLast(new TcpServerHandler());
	}
}
