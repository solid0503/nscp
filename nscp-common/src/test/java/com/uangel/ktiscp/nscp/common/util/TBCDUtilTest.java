package com.uangel.ktiscp.nscp.common.util;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.uangel.ktiscp.nscp.common.sock.NscpMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TBCDUtilTest {
	@Test
	public void tbcdTest() throws Exception {
		byte[] bytes = new byte[6];
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		buf.put((byte)1);
		buf.put(TBCDUtil.parseTBCD("2921"));
		buf.put((byte)0xff);
		buf.put(TBCDUtil.parseTBCD("010"));
		
		Assertions.assertTrue(TBCDUtil.dumpBytes(bytes).startsWith("0x01 0x92 0x12 0xFF 0x10 0xF0"));
		Assertions.assertTrue(NscpMessage.routingInfoString(bytes).startsWith("Prefix:2921, NPA:010"));
		
		
		TBCDUtil.parseTBCD("abc*#1");
	}
}
