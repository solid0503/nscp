package com.uangel.ktiscp.nscp.common.util;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

import com.uangel.ktiscp.nscp.common.sock.NscpMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TBCDUtilTest {
	@Test
	public void tbcdTest() throws Exception {
		byte[] bytes = TBCDUtil.parseTBCD("45008");
		log.debug("{}", TBCDUtil.dumpBytes(bytes));
		log.debug("{}", TBCDUtil.toTBCD(bytes));
	}
	
	@Test
	public void tmpTest() {
		byte[] bytes = new byte[6];
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		buf.put((byte)1);
		buf.put(TBCDUtil.parseTBCD("2921"));
		buf.put((byte)0xff);
		buf.put(TBCDUtil.parseTBCD("010"));
		log.debug("{}", TBCDUtil.dumpBytes(bytes));
		log.debug("{}", NscpMessage.routingInfoString(bytes));
	}
}
