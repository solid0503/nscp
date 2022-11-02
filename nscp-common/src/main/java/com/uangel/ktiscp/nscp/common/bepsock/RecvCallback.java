package com.uangel.ktiscp.nscp.common.bepsock;

import io.netty.channel.ChannelHandlerContext;

public interface RecvCallback {
	public void channelRead(ChannelHandlerContext ctx, BepMessage msg) throws Exception;
}
