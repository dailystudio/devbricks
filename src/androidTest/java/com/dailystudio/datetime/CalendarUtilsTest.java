package com.dailystudio.datetime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.dailystudio.test.ActivityTestCase;
import com.dailystudio.development.Logger;

public class CalendarUtilsTest extends ActivityTestCase {

	private final static String PARSE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	private final static int SEC_MILLIS = 1000;
	private final static int MINUTE_MILLS = (SEC_MILLIS * 60);
	private final static int HOUR_MILLIS = (MINUTE_MILLS * 60);
	private final static int DAY_MILLIS = (HOUR_MILLIS * 24);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetStartOfDay() {
		Date date = null;
		Date expd = null;
		long actual = 0;
		long expected = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("1982-07-03 08:08:08.888");
			expd = sdf.parse("1982-07-03 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		actual = CalendarUtils.getStartOfDay(date.getTime());
		expected = expd.getTime();
		
		assertEquals(expected, actual);
		
		try {
			date = sdf.parse("1982-07-03 00:00:00.000");
			expd = sdf.parse("1982-07-03 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		expected = expd.getTime();

		final long baseTime = date.getTime();
		final Random r = new Random();
		
		long time = 0;
		for (int i = 0; i < 25; i++) {
			time = baseTime + r.nextInt(DAY_MILLIS);
			
			actual = CalendarUtils.getStartOfDay(time);
			assertEquals(expected, actual);
		}
		
	}
	
	public void testGetEndOfDay() {
		Date date = null;
		Date expd = null;
		long actual = 0;
		long expected = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("1982-08-04 01:02:03.456");
			expd = sdf.parse("1982-08-04 23:59:59.999");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		actual = CalendarUtils.getEndOfDay(date.getTime());
		expected = expd.getTime();
		
		assertEquals(expected, actual);
		
		try {
			date = sdf.parse("1982-08-04 00:00:00.000");
			expd = sdf.parse("1982-08-04 23:59:59.999");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		expected = expd.getTime();

		final long baseTime = date.getTime();
		final Random r = new Random();
		
		long time = 0;
		for (int i = 0; i < 25; i++) {
			time = baseTime + r.nextInt(DAY_MILLIS);
			
			actual = CalendarUtils.getEndOfDay(time);
			assertEquals(expected, actual);
		}
	}
	
	public void testGetStartOfWeek() {
		Date date = null;
		Date expd = null;
		long actual = 0;
		long expected = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2011-08-17 08:08:08.888");
			expd = sdf.parse("2011-08-15 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		actual = CalendarUtils.getStartOfWeek(date.getTime());
		expected = expd.getTime();
		
		assertEquals(expected, actual);
		
		try {
			date = sdf.parse("2011-08-15 00:00:00.000");
			expd = sdf.parse("2011-08-15 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		expected = expd.getTime();

		final long baseTime = date.getTime();
		
		long time = 0;
		for (int i = 0; i < 7; i++) {
			time = baseTime + i * DAY_MILLIS;
			
			actual = CalendarUtils.getStartOfWeek(time);
			assertEquals(expected, actual);
		}
	}
	
	public void testGetEndOfWeek() {
		Date date = null;
		Date expd = null;
		long actual = 0;
		long expected = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2011-08-17 08:08:08.888");
			expd = sdf.parse("2011-08-21 23:59:59.999");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		actual = CalendarUtils.getEndOfWeek(date.getTime());
		expected = expd.getTime();
		
		assertEquals(expected, actual);
		
		try {
			date = sdf.parse("2011-08-15 00:00:00.000");
			expd = sdf.parse("2011-08-21 23:59:59.999");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		expected = expd.getTime();

		final long baseTime = date.getTime();
		
		long time = 0;
		for (int i = 0; i < 7; i++) {
			time = baseTime + i * DAY_MILLIS;
			
			actual = CalendarUtils.getEndOfWeek(time);
			assertEquals(expected, actual);
		}
	}
	
	public void testGetStartOfMonth() {
		Date date = null;
		Date expd = null;
		long actual = 0;
		long expected = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2011-11-17 12:34:56.789");
			expd = sdf.parse("2011-11-01 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		actual = CalendarUtils.getStartOfMonth(date.getTime());
		expected = expd.getTime();
		
		assertEquals(expected, actual);

		String dateStr = null;
		String expdStr = null;
		for (int i = 0; i < 12; i++) {
			dateStr = String.format("2012-%d-15 12:34:56.789", i + 1);
			expdStr = String.format("2012-%d-01 00:00:00.000", i + 1);
			
			try {
				date = sdf.parse(dateStr);
				expd = sdf.parse(expdStr);
			} catch (ParseException e) {
				e.printStackTrace();
				
				date = null;
				expd = null;
			}
			assertNotNull(date);
			assertNotNull(expd);

			actual = CalendarUtils.getStartOfMonth(date.getTime());
			expected = expd.getTime();
			
			assertEquals(expected, actual);
		}
	}

	public void testGetEndOfMonth() {
		Date date = null;
		Date expd = null;
		long actual = 0;
		long expected = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2011-11-17 12:34:56.789");
			expd = sdf.parse("2011-11-30 23:59:59.999");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		actual = CalendarUtils.getEndOfMonth(date.getTime());
		expected = expd.getTime();
		
		assertEquals(expected, actual);

		String dateStr = null;
		String expdStr = null;
		int[] endDay = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		
		for (int i = 0; i < 12; i++) {
			dateStr = String.format("2012-%d-15 12:34:56.789", i + 1);
			expdStr = String.format("2012-%d-%d 23:59:59.999", 
					i + 1,
					endDay[i]);
			
			try {
				date = sdf.parse(dateStr);
				expd = sdf.parse(expdStr);
			} catch (ParseException e) {
				e.printStackTrace();
				
				date = null;
				expd = null;
			}
			assertNotNull(date);
			assertNotNull(expd);

			actual = CalendarUtils.getEndOfMonth(date.getTime());
			expected = expd.getTime();
			
			assertEquals(expected, actual);
		}
	}

	public void testGetStartOfYear() {
		Date date = null;
		Date expd = null;
		long actual = 0;
		long expected = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2011-08-17 08:08:08.888");
			expd = sdf.parse("2011-01-01 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		actual = CalendarUtils.getStartOfYear(date.getTime());
		expected = expd.getTime();
		
		assertEquals(expected, actual);
		
		try {
			date = sdf.parse("2011-01-01 00:00:00.000");
			expd = sdf.parse("2011-01-01 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		expected = expd.getTime();

		final long baseTime = date.getTime();
		
		long time = 0;
		for (long i = 0; i < 365; i++) {
			time = baseTime + i * DAY_MILLIS;

			actual = CalendarUtils.getStartOfYear(time);
			assertEquals(expected, actual);
		}
	}
	

	public void testGetEndOfYear() {
		Date date = null;
		Date expd = null;
		long actual = 0;
		long expected = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2011-08-17 08:08:08.888");
			expd = sdf.parse("2011-12-31 23:59:59.999");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		actual = CalendarUtils.getEndOfYear(date.getTime());
		expected = expd.getTime();
		
		assertEquals(expected, actual);
		
		try {
			date = sdf.parse("2011-01-01 00:00:00.000");
			expd = sdf.parse("2011-12-31 23:59:59.999");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
			expd = null;
		}
		assertNotNull(date);
		assertNotNull(expd);
		
		expected = expd.getTime();

		final long baseTime = date.getTime();
		
		long time = 0;
		for (long i = 0; i < 365; i++) {
			time = baseTime + i * DAY_MILLIS;

			actual = CalendarUtils.getEndOfYear(time);
			assertEquals(expected, actual);
		}
	}
	
	public void testGetSecond() {
		Date date = null;
		int actual = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2012-12-21 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
		}
		assertNotNull(date);

		final long baseTime = date.getTime();
		
		long time = 0;
		for (int i = 0; i < 60; i++) {
			time = baseTime + i * SEC_MILLIS;
			
			actual = CalendarUtils.getSecond(time);
			assertEquals(i, actual);
		}
	}
	
	public void testGetMinute() {
		Date date = null;
		int actual = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2012-12-21 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
		}
		assertNotNull(date);

		final long baseTime = date.getTime();
		
		long time = 0;
		for (int i = 0; i < 60; i++) {
			time = baseTime + i * MINUTE_MILLS;
			
			actual = CalendarUtils.getMinute(time);
			assertEquals(i, actual);
		}
	}
	
	public void testGetHour() {
		Date date = null;
		int actual = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2012-12-21 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
		}
		assertNotNull(date);

		final long baseTime = date.getTime();
		
		long time = 0;
		for (int i = 0; i < 24; i++) {
			time = baseTime + i * HOUR_MILLIS;
			
			actual = CalendarUtils.getHour(time);
			assertEquals(i, actual);
		}
	}
	
	public void testGetDay() {
		Date date = null;
		int actual = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2012-12-01 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
		}
		assertNotNull(date);

		final long baseTime = date.getTime();
		
		long time = 0;
		for (long i = 0; i < 31; i++) {
			time = baseTime + i * DAY_MILLIS;
			
			actual = CalendarUtils.getDay(time);
			assertEquals(i + 1, actual);
		}
	}
	
	public void testGetWeekday() {
		Date date = null;
		int actual = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2011-12-19 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
		}
		assertNotNull(date);

		final long baseTime = date.getTime();
		
		long time = 0;
		for (int i = 0; i < 7; i++) {
			time = baseTime + i * DAY_MILLIS;
			
			actual = CalendarUtils.getWeekDay(time);
			Logger.debug("time = %s, actual = %d", 
					CalendarUtils.timeToReadableString(time), actual);
			assertEquals(i + 1, actual);
		}
	}
	
	public void testGetWeek() {
		Date date = null;
		int actual = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		String[] dstrs = {
				"2011-12-31 00:00:00.000",
				"2012-01-08 00:00:00.000",
				"2012-12-25 00:00:00.000",
				"2012-12-31 00:00:00.000",
				"2013-01-01 00:00:00.000",
				"2013-01-08 00:00:00.000",
		};
		
		int[] weeks = {
				52,
				1,
				52,
				1,
				1,
				2,
		};
		
		assertEquals(dstrs.length, weeks.length);

		long time = 0;
		for (int i = 0; i < dstrs.length; i++) {
			try {
				date = sdf.parse(dstrs[i]);
			} catch (ParseException e) {
				e.printStackTrace();
				
				date = null;
			}
			assertNotNull(date);

			time = date.getTime();
			
			actual = CalendarUtils.getWeek(time);
			Logger.debug("time = %s, actual = %d", 
					CalendarUtils.timeToReadableString(time), actual);
			assertEquals(weeks[i], actual);
		}
	}

	public void testGetMonth() {
		Date date = null;
		int actual = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2012-01-01 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
		}
		assertNotNull(date);

		long time = 0;
		for (int i = 0; i < 12; i++) {
			date.setMonth(i);
			
			time = date.getTime();
			
			actual = CalendarUtils.getMonth(time);
			assertEquals(i, actual);
		}
	}
	
	public void testGetYear() {
		Date date = null;
		int actual = 0;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("1900-01-01 00:00:00.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
		}
		assertNotNull(date);

		long time = 0;
		for (int i = 0; i < 100; i++) {
			date.setYear(i);
			
			time = date.getTime();
			
			actual = CalendarUtils.getYear(time);
			assertEquals(1900 + i, actual);
		}
	}

	public void testIsCurrentDay() {
		final long now = System.currentTimeMillis();
		
		final long baseTime = CalendarUtils.getStartOfDay(now);
		
		long time = 0;
		
		for (int i = 0; i < 24; i++) {
			time = baseTime + (i + 1) * CalendarUtils.HOUR_IN_MILLIS - 1;
			
			assertTrue(CalendarUtils.isCurrentDay(time));
		}
		
		for (int i = 0; i < 24; i++) {
			time = baseTime - (i + 1) * CalendarUtils.HOUR_IN_MILLIS + 1;
			
			assertFalse(CalendarUtils.isCurrentDay(time));
		}
	}

	public void testIsCurrentWeek() {
		final long now = System.currentTimeMillis();
		
		final long baseTime = CalendarUtils.getStartOfWeek(now);
		
		long time = 0;
		
		for (int i = 0; i < 7; i++) {
			time = baseTime + (i + 1) * CalendarUtils.DAY_IN_MILLIS - 1;
			
			assertTrue(CalendarUtils.isCurrentWeek(time));
		}
		
		for (int i = 0; i < 7; i++) {
			time = baseTime - (i + 1) * CalendarUtils.DAY_IN_MILLIS + 1;
			
			assertFalse(CalendarUtils.isCurrentWeek(time));
		}
	}

	public void testIsCurrentMonth() {
		final long now = System.currentTimeMillis();
		
		final long baseTime = CalendarUtils.getStartOfMonth(now);
		final long maxTime = CalendarUtils.getEndOfMonth(now);
		
		long time = 0;
		
		for (int i = 0; i < 31; i++) {
			time = baseTime + (i + 1) * CalendarUtils.DAY_IN_MILLIS - 1;
			
			assertEquals((time <= maxTime), CalendarUtils.isCurrentMonth(time));
		}
		
		for (int i = 0; i < 31; i++) {
			time = baseTime - (i + 1) * CalendarUtils.DAY_IN_MILLIS + 1;
			
			assertFalse(CalendarUtils.isCurrentMonth(time));
		}
	}

	public void testIsCurrentYear() {
		final long now = System.currentTimeMillis();
		
		final long baseTime = CalendarUtils.getStartOfYear(now);
		final long maxTime = CalendarUtils.getEndOfYear(now);
		
		long time = 0;
		
		for (long i = baseTime; i <= maxTime; i += CalendarUtils.DAY_IN_MILLIS) {
			assertTrue(CalendarUtils.isCurrentYear(i));
		}
		
		for (int i = 0; i < 10; i++) {
			time = baseTime - (i + 1) * CalendarUtils.DAY_IN_MILLIS * 31 + 1;
			
			assertFalse(CalendarUtils.isCurrentYear(time));
		}
	}

	public void testTimeToReadableString() {
		Date date = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse("2012-08-04 13:14:52.000");
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
		}
		assertNotNull(date);

		final long time = date.getTime();
		final String strAmPm = new SimpleDateFormat("aa").format(date);
		assertNotNull(strAmPm);

		/*
		 * XXX: following asserts depends on localization.
		 * 		PM will be translated to different words
		 */
		assertEquals("2012/08/04 01:14:52:000 " + strAmPm, CalendarUtils.timeToReadableString(time));
		assertEquals("2012/08/04 01:14:52:000 " + strAmPm, CalendarUtils.timeToReadableString(time, true, true));
		assertEquals("2012/08/04", CalendarUtils.timeToReadableString(time, true, false));
		assertEquals("01:14:52:000 " + strAmPm, CalendarUtils.timeToReadableString(time, false, true));
		assertEquals("", CalendarUtils.timeToReadableString(time, false, false));
		assertEquals("2012/08/04", CalendarUtils.timeToReadableStringWithoutTime(time));
		assertEquals("01:14:52:000 " + strAmPm, CalendarUtils.timeToReadableStringWithoutDate(time));
	}
	
	public void testDurationToReadableString() {
		long duration = 0;
		
		duration = 123;
		
		assertEquals("0h 00\' 00\" 123", 
				CalendarUtils.durationToReadableString(duration));

		duration = 1 * SEC_MILLIS + 314;
		assertEquals("0h 00\' 01\" 314", 
				CalendarUtils.durationToReadableString(duration));

		duration = 52 * MINUTE_MILLS + 11 * SEC_MILLIS + 314;
		assertEquals("0h 52\' 11\" 314", 
				CalendarUtils.durationToReadableString(duration));

		duration = 11 * HOUR_MILLIS + 52 * MINUTE_MILLS + 11 * SEC_MILLIS + 314;
		assertEquals("11h 52\' 11\" 314", 
				CalendarUtils.durationToReadableString(duration));
	}
	
}
