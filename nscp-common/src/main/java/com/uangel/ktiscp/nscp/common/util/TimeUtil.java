package com.uangel.ktiscp.nscp.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * 시간 관련 함수 모음 클래스
 *
 */
public class TimeUtil {

	/**
	 * @return
	 */
	public static String getCurrentTimeAs14Format() {
		return getCurrentTimeAs14Format(0);
	}

	/**
	 * @param dayDuration
	 * @return
	 */
	public static String getCurrentTimeAs14Format(int dayDuration) {
		long tick = System.currentTimeMillis();
		return getCurrentTimeAs14Format(dayDuration, tick);
	}

	/**
	 * @param dayDuration
	 * @param tick
	 * @return
	 */
	public static String getCurrentTimeAs14Format(int dayDuration, long tick) {
		tick += (long)dayDuration * 24 * 60 * 60 * 1000;
		SimpleDateFormat fmt14 = new SimpleDateFormat("yyyyMMddHHmmss");
		return fmt14.format(new java.util.Date(tick));
	}

	/**
	 * @return
	 */
	public static String getCurrentTimeAs12Format() {
		return getCurrentTimeAs12Format(0);
	}

	/**
	 * @param dayDuration
	 * @return
	 */
	public static String getCurrentTimeAs12Format(int dayDuration) {
		long tick = System.currentTimeMillis();
		tick += (long)dayDuration * 24 * 60 * 60 * 1000;
		SimpleDateFormat fmt12 = new SimpleDateFormat("yyyyMMddHHmm");
		return fmt12.format(new java.util.Date(tick));
	}

	/**
	 * @param minDuration
	 * @return
	 */
	public static String getCurrentTimeAs12FormatByMin(int minDuration) {
		long tick = System.currentTimeMillis();
		tick += (long)minDuration * 60 * 1000;
		SimpleDateFormat fmt12 = new SimpleDateFormat("yyyyMMddHHmm");
		return fmt12.format(new java.util.Date(tick));
	}

	/**
	 * @param time
	 * @param minDuration
	 * @return
	 */
	public static String get14StrFormatFrom14FormatByMin(String time, int minDuration) {
		long tick = getTickFrom14StrFormat(time);
		tick += (long)minDuration * 60 * 1000;
		SimpleDateFormat fmt14 = new SimpleDateFormat("yyyyMMddHHmmss");
		return fmt14.format(new java.util.Date(tick));
	}

	/**
	 * @param time
	 * @param hourDuration
	 * @return
	 */
	public static String get14StrFormatFrom14FormatByHour(String time, int hourDuration) {
		long tick = getTickFrom14StrFormat(time);
		tick += (long)hourDuration * 60 * 60 * 1000;
		SimpleDateFormat fmt14 = new SimpleDateFormat("yyyyMMddHHmmss");
		return fmt14.format(new java.util.Date(tick));
	}

	/**
	 * @param time
	 * @param dayDuration
	 * @return
	 */
	public static String get14StrFormatFrom14FormatByDay(String time, int dayDuration) {
		long tick = getTickFrom14StrFormat(time);
		tick += (long)dayDuration * 24 * 60 * 60 * 1000;
		SimpleDateFormat fmt14 = new SimpleDateFormat("yyyyMMddHHmmss");
		return fmt14.format(new java.util.Date(tick));
	}

	/**
	 * @return
	 */
	public static String getCurrentTimeAsDiameterFormat() {
		long tick = System.currentTimeMillis();
		SimpleDateFormat fmtDiameter = new SimpleDateFormat("MM/dd HH:mm:ss.SS");
		return fmtDiameter.format(new java.util.Date(tick));
	}

	/**
	 * @return
	 */
	public static String getCurrentTimeAsLongFormat() {
		long tick = System.currentTimeMillis();
		SimpleDateFormat fmtlong = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return fmtlong.format(new java.util.Date(tick));
	}

	/**
	 * @return
	 */
	public static String getCurrentTimeAs8Format() {
		return getCurrentTimeAs8Format(0);
	}

	/**
	 * @param dayDuration
	 * @return
	 */
	public static String getCurrentTimeAs8Format(int dayDuration) {
		long tick = System.currentTimeMillis();
		tick += (long)dayDuration * 24 * 60 * 60 * 1000;
		SimpleDateFormat fmt8 = new SimpleDateFormat("yyyyMMdd");
		return fmt8.format(new java.util.Date(tick));
	}

	/**
	 * @param dayDuration
	 * @param tick
	 * @return
	 */
	public static String getCurrentTimeAs8Format(int dayDuration, long tick) {
		tick += (long)dayDuration * 24 * 60 * 60 * 1000;
		SimpleDateFormat fmt8 = new SimpleDateFormat("yyyyMMdd");
		return fmt8.format(new java.util.Date(tick));
	}

	/**
	 * @param date
	 * @param dayDuration
	 * @return
	 */
	public static String getTimeAs14Format(java.util.Date date, long dayDuration) {
		if (date == null)
			return null;
		SimpleDateFormat fmt14 = new SimpleDateFormat("yyyyMMddHHmmss");
		String strDate = fmt14.format(date);
		long retDate = Long.parseLong(strDate) + (dayDuration * 1000000);
		return Long.toString(retDate);
	}


	/**
	 * @param date
	 * @return
	 */
	public static String getTimeAs14Format(java.util.Date date) {
		if (date == null)
			return null;
		SimpleDateFormat fmt14 = new SimpleDateFormat("yyyyMMddHHmmss");
		return fmt14.format(date);
	}

	/**
	 * @param date
	 * @return
	 */
	public static String getTimeAs8Format(java.util.Date date) {
		if (date == null)
			return null;
		SimpleDateFormat fmt8 = new SimpleDateFormat("yyyyMMdd");
		return fmt8.format(date);
	}

	public static Date stringToDate(String date, String format) {
		try {
			SimpleDateFormat fmt = new SimpleDateFormat(format);
			return fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String dateToString(Date date, String format) {
		SimpleDateFormat fmt = new SimpleDateFormat(format);
		return fmt.format(date);
	}

	/**
	 * @param time
	 * @return
	 */
	public static long getTickFrom16StrFormat(String time) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new java.text.SimpleDateFormat("yyyyMMddHHmmssSS").parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	/**
	 * @param time
	 * @return
	 */
	public static long getTickFrom14StrFormat(String time) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new java.text.SimpleDateFormat("yyyyMMddHHmmss").parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	/**
	 * @param time
	 * @return
	 */
	public static long getTickFrom8StrFormat(String time) {
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(new java.text.SimpleDateFormat("yyyyMMdd").parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return c.getTimeInMillis();
	}

	/**
	 * @param tick
	 * @return
	 */
	public static String get16StrFormatFromTick(long tick) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(tick);

		return new java.text.SimpleDateFormat("yyyyMMddHHmmssSS").format(c.getTime());
	}

	/**
	 * @param tick
	 * @return
	 */
	public static String get14StrFormatFromTick(long tick) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(tick);

		return new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(c.getTime());
	}

	/**
	 * @param tick
	 * @return
	 */
	public static String get8StrFormatFromTick(long tick) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(tick);

		return new java.text.SimpleDateFormat("yyyyMMdd").format(c.getTime());
	}

	public static int getYear() {
		return getYear(null);
	}

	public static int getYear(Date date) {
		long tick = (date == null) ? System.currentTimeMillis() : date.getTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(tick);

		return c.get(Calendar.YEAR);
	}

	public static int getMonthOfYear() {
		return getMonthOfYear(null);
	}

	public static int getMonthOfYear(Date date) {
		long tick = (date == null) ? System.currentTimeMillis() : date.getTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(tick);

		return c.get(Calendar.MONTH) + 1;
	}

	public static int getDayOfWeek() {
		return getMonthOfYear(null);
	}

	/**
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
		long tick = (date == null) ? System.currentTimeMillis() : date.getTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(tick);

		return c.get(Calendar.DAY_OF_WEEK);
	}

	public static int getDayOfMonth() {
		return getDayOfMonth(null);
	}

	/**
	 * @return
	 */
	public static int getDayOfMonth(Date date) {
		long tick = (date == null) ? System.currentTimeMillis() : date.getTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(tick);

		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static int getDayOfYear() {
		return getMonthOfYear(null);
	}

	/**
	 * @return
	 */
	public static int getDayOfYear(Date date) {
		long tick = (date == null) ? System.currentTimeMillis() : date.getTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(tick);

		return c.get(Calendar.DAY_OF_YEAR);
	}

	public static int getLastDayOfMonth() {
		return getLastDayOfMonth(null);
	}

	public static int getLastDayOfMonth(Date date) {
		long tick = (date == null) ? System.currentTimeMillis() : date.getTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(tick);
		c.getMaximum(Calendar.DAY_OF_MONTH);

		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * @param date
	 * @return
	 */
	public static String strDate(String date) {
		if (date != null) {
			if (date.length() == 8)
				return date.substring(0, 4) + "." + date.substring(4, 6) + "." + date.substring(6, 8);
			else if (date.length() > 8 && date.length() <= 10)
				return date.substring(0, 4) + "." + date.substring(4, 6) + "." + date.substring(6, 8) + " "
						+ date.substring(8, 10);
			else if (date.length() > 10)
				return date.substring(0, 4) + "." + date.substring(4, 6) + "." + date.substring(6, 8) + " "
						+ date.substring(8, 10) + ":" + date.substring(10, 12);
			else
				return date;
		} else {
			return "";
		}
	}

	/**
	 * @param day
	 * @return
	 */
	public static String getPreviousDay(String day) {
		return getPreviousDay(getTickFrom8StrFormat(day));
	}

	/**
	 * @param day
	 * @return
	 */
	public static String getNextDay(String day) {
		return getNextDay(getTickFrom8StrFormat(day));
	}

	/**
	 * @param tick
	 * @return
	 */
	public static String getPreviousDay(long tick) {
		return get8StrFormatFromTick(tick - (24 * (60 * (60 * 1000))));
	}

	/**
	 * @param tick
	 * @return
	 */
	public static String getNextDay(long tick) {
		return get8StrFormatFromTick(tick + (24 * (60 * (60 * 1000))));
	}

	/**
	 * @param monthDuration
	 * @return
	 */
	public static String getPreviousMonth(int monthDuration) {
		String month;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, monthDuration);

		month = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1);

		return month;
	}

	/**
	 * @param month
	 * @return
	 */
	public static String beforeMonth(int month) {
		String date = getToday("");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date now;

		try {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1, Integer
					.parseInt(date.substring(6)));
			calendar.add(Calendar.MONTH, -1 * month);
			now = calendar.getTime();

		} catch (Exception ie) {
			return "";
		}

		return formatter.format(now);
	}

	/**
	 * @param month
	 * @return
	 */
	public static String afterMonth(int month) {
		String date = getToday("");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date now;

		try {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)) - 1, Integer
					.parseInt(date.substring(6)));
			calendar.add(Calendar.MONTH, month);
			now = calendar.getTime();

		} catch (Exception ie) {
			return "";
		}

		return formatter.format(now);
	}

	/**
	 * @param division
	 * @return
	 */
	public static String getToday(String division) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
		today = today.substring(0, 4) + division + today.substring(4, 6) + division + today.substring(6, 8);

		return today;
	}
}