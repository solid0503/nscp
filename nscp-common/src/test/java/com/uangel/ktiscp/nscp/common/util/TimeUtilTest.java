package com.uangel.ktiscp.nscp.common.util;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimeUtilTest {

	@Test
	public void timeUtilTest() {
		Date date = TimeUtil.stringToDate("20221025165800", "yyyyMMddHHmmss");
		String strDate = TimeUtil.dateToString(date, "yyyyMMddHHmmss");
		Assertions.assertEquals("20221025165800", strDate);
		
		TimeUtil.getCurrentTimeAs14Format();
		TimeUtil.getCurrentTimeAs14Format(1);
		TimeUtil.getCurrentTimeAs14Format(1, 0);
	}
}
