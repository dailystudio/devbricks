package com.dailystudio.datetime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dailystudio.test.ActivityTestCase;
import com.dailystudio.test.Asserts;

public class TimeSpanUtilsTest extends ActivityTestCase {

	private final static String PARSE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	private final static int[] FILTER_WEEKDAYS = {
		1, 2, 3, 4, 5,
	};
		
	private final static int[] FILTER_WEEKEND = {
		0, 6,
	};


	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	protected long parseDateTime(String dstr) {
		if (dstr == null) {
			return 0l;
		}
		
		Date date = null;
		
		SimpleDateFormat sdf = new SimpleDateFormat(PARSE_FORMAT);
		
		try {
			date = sdf.parse(dstr);
		} catch (ParseException e) {
			e.printStackTrace();
			
			date = null;
		}
		assertNotNull(date);

		return date.getTime();
	}
	
	public void testCalculateErrorHourDistrib() {
		final long start = parseDateTime("2012-12-12 00:00:00.000");
		final long end = parseDateTime("2010-12-13 00:00:00.000");
		
		long[] actual = null;
		long[] expected = null;
		
		actual = TimeSpanUtils.calculateHourDistribution(start, end);
		assertNull(actual);
		
		actual = TimeSpanUtils.calculateHourDistribution(null, start, end);
		assertNull(actual);
		
		actual = new long[20]; 
		expected = TimeSpanUtils.calculateHourDistribution(actual, start, end);
		assertEquals(expected, actual);
	}

	public void testCalculateOneDayHourDistrib() {
		final long start = parseDateTime("2012-12-12 00:00:00.000");
		final long end = parseDateTime("2012-12-13 00:00:00.000");
		
		long[] actual = TimeSpanUtils.calculateHourDistribution(start, end);
		long[] expected = {
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 00 ~ Hour 01 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 01 ~ Hour 02 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 02 ~ Hour 03 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 03 ~ Hour 04 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 04 ~ Hour 05 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 05 ~ Hour 06 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 06 ~ Hour 07 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 07 ~ Hour 08 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 08 ~ Hour 09 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 09 ~ Hour 10 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 10 ~ Hour 11 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 11 ~ Hour 12 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 12 ~ Hour 13 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 13 ~ Hour 14 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 14 ~ Hour 15 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 15 ~ Hour 16 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 16 ~ Hour 17 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 17 ~ Hour 18 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 18 ~ Hour 19 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 19 ~ Hour 20 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 20 ~ Hour 21 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 21 ~ Hour 22 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 22 ~ Hour 23 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 23 ~ Hour 24 */
		};
		
		Asserts.assertEquals(expected, actual);
	}
	
	public void testCalculateOneDayHourDistribWithFilters() {
		final long start = parseDateTime("2012-12-12 00:00:00.000");
		final long end = parseDateTime("2012-12-13 00:00:00.000");
		
		long[] actual1 = TimeSpanUtils.calculateHourDistribution(start, end,
				FILTER_WEEKDAYS);
		long[] expected1 = {
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 00 ~ Hour 01 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 01 ~ Hour 02 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 02 ~ Hour 03 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 03 ~ Hour 04 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 04 ~ Hour 05 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 05 ~ Hour 06 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 06 ~ Hour 07 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 07 ~ Hour 08 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 08 ~ Hour 09 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 09 ~ Hour 10 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 10 ~ Hour 11 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 11 ~ Hour 12 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 12 ~ Hour 13 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 13 ~ Hour 14 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 14 ~ Hour 15 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 15 ~ Hour 16 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 16 ~ Hour 17 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 17 ~ Hour 18 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 18 ~ Hour 19 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 19 ~ Hour 20 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 20 ~ Hour 21 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 21 ~ Hour 22 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 22 ~ Hour 23 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 23 ~ Hour 24 */
		};
		Asserts.assertEquals(expected1, actual1);

		long[] actual2 = TimeSpanUtils.calculateHourDistribution(start, end,
				FILTER_WEEKEND);
		long[] expected2 = {
				0, /* Hour 00 ~ Hour 01 */
				0, /* Hour 01 ~ Hour 02 */
				0, /* Hour 02 ~ Hour 03 */
				0, /* Hour 03 ~ Hour 04 */
				0, /* Hour 04 ~ Hour 05 */
				0, /* Hour 05 ~ Hour 06 */
				0, /* Hour 06 ~ Hour 07 */
				0, /* Hour 07 ~ Hour 08 */
				0, /* Hour 08 ~ Hour 09 */
				0, /* Hour 09 ~ Hour 10 */
				0, /* Hour 10 ~ Hour 11 */
				0, /* Hour 11 ~ Hour 12 */
				0, /* Hour 12 ~ Hour 13 */
				0, /* Hour 13 ~ Hour 14 */
				0, /* Hour 14 ~ Hour 15 */
				0, /* Hour 15 ~ Hour 16 */
				0, /* Hour 16 ~ Hour 17 */
				0, /* Hour 17 ~ Hour 18 */
				0, /* Hour 18 ~ Hour 19 */
				0, /* Hour 19 ~ Hour 20 */
				0, /* Hour 20 ~ Hour 21 */
				0, /* Hour 21 ~ Hour 22 */
				0, /* Hour 22 ~ Hour 23 */
				0, /* Hour 23 ~ Hour 24 */
		};
		Asserts.assertEquals(expected2, actual2);
	}
	
	public void testCalculatePartDay1HourDistrib() {
		final long start = parseDateTime("2012-01-01 00:00:00.000");
		final long end = parseDateTime("2012-01-01 22:00:00.000");
		
		long[] actual = TimeSpanUtils.calculateHourDistribution(start, end);
		long[] expected = {
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 00 ~ Hour 01 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 01 ~ Hour 02 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 02 ~ Hour 03 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 03 ~ Hour 04 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 04 ~ Hour 05 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 05 ~ Hour 06 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 06 ~ Hour 07 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 07 ~ Hour 08 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 08 ~ Hour 09 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 09 ~ Hour 10 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 10 ~ Hour 11 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 11 ~ Hour 12 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 12 ~ Hour 13 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 13 ~ Hour 14 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 14 ~ Hour 15 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 15 ~ Hour 16 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 16 ~ Hour 17 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 17 ~ Hour 18 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 18 ~ Hour 19 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 19 ~ Hour 20 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 20 ~ Hour 21 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 21 ~ Hour 22 */
				0, /* Hour 22 ~ Hour 23 */
				0, /* Hour 23 ~ Hour 24 */
		};
		
		Asserts.assertEquals(expected, actual);
	}
	
	public void testCalculatePartDay2HourDistrib() {
		final long start = parseDateTime("2012-01-01 00:21:00.000");
		final long end = parseDateTime("2012-01-01 12:30:00.000");
		
		long[] actual = TimeSpanUtils.calculateHourDistribution(start, end);
		long[] expected = {
				(39 * CalendarUtils.MINUTE_IN_MILLIS), /* Hour 00 ~ Hour 01 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 01 ~ Hour 02 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 02 ~ Hour 03 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 03 ~ Hour 04 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 04 ~ Hour 05 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 05 ~ Hour 06 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 06 ~ Hour 07 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 07 ~ Hour 08 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 08 ~ Hour 09 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 09 ~ Hour 10 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 10 ~ Hour 11 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 11 ~ Hour 12 */
				(30 * CalendarUtils.MINUTE_IN_MILLIS), /* Hour 12 ~ Hour 13 */
				0, /* Hour 13 ~ Hour 14 */
				0, /* Hour 14 ~ Hour 15 */
				0, /* Hour 15 ~ Hour 16 */
				0, /* Hour 16 ~ Hour 17 */
				0, /* Hour 17 ~ Hour 18 */
				0, /* Hour 18 ~ Hour 19 */
				0, /* Hour 19 ~ Hour 20 */
				0, /* Hour 20 ~ Hour 21 */
				0, /* Hour 21 ~ Hour 22 */
				0, /* Hour 22 ~ Hour 23 */
				0, /* Hour 23 ~ Hour 24 */
		};
		
		Asserts.assertEquals(expected, actual);
	}
	
	public void testCalculateOverOneDay1HourDistrib() {
		final long start = parseDateTime("2010-01-22 00:35:12.123");
		final long end = parseDateTime("2010-01-23 14:35:22.000");
		
		long[] actual = TimeSpanUtils.calculateHourDistribution(start, end);
		long[] expected = {
				(CalendarUtils.HOUR_IN_MILLIS 
						+ 24 * CalendarUtils.MINUTE_IN_MILLIS 
						+ 47 * CalendarUtils.SECOND_IN_MILLIS 
						+ 877), /* Hour 00 ~ Hour 01 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 01 ~ Hour 02 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 02 ~ Hour 03 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 03 ~ Hour 04 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 04 ~ Hour 05 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 05 ~ Hour 06 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 06 ~ Hour 07 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 07 ~ Hour 08 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 08 ~ Hour 09 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 09 ~ Hour 10 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 10 ~ Hour 11 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 11 ~ Hour 12 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 12 ~ Hour 13 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 13 ~ Hour 14 */
				(CalendarUtils.HOUR_IN_MILLIS 
						+ 35 * CalendarUtils.MINUTE_IN_MILLIS
						+ 22 * CalendarUtils.SECOND_IN_MILLIS), /* Hour 14 ~ Hour 15 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 15 ~ Hour 16 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 16 ~ Hour 17 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 17 ~ Hour 18 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 18 ~ Hour 19 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 19 ~ Hour 20 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 20 ~ Hour 21 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 21 ~ Hour 22 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 22 ~ Hour 23 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 23 ~ Hour 24 */
		};
		
		Asserts.assertEquals(expected, actual);
	}
	
	public void testCalculateOverOneDay1HourDistribWithFilter() {
		final long start = parseDateTime("2010-01-22 00:35:12.123");
		final long end = parseDateTime("2010-01-23 14:35:22.000");
		
		long[] actual1 = TimeSpanUtils.calculateHourDistribution(start, end,
				FILTER_WEEKDAYS);
		long[] expected1 = {
				(24 * CalendarUtils.MINUTE_IN_MILLIS 
					+ 47 * CalendarUtils.SECOND_IN_MILLIS 
					+ 877), /* Hour 00 ~ Hour 01 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 01 ~ Hour 02 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 02 ~ Hour 03 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 03 ~ Hour 04 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 04 ~ Hour 05 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 05 ~ Hour 06 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 06 ~ Hour 07 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 07 ~ Hour 08 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 08 ~ Hour 09 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 09 ~ Hour 10 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 10 ~ Hour 11 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 11 ~ Hour 12 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 12 ~ Hour 13 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 13 ~ Hour 14 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 14 ~ Hour 15 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 15 ~ Hour 16 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 16 ~ Hour 17 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 17 ~ Hour 18 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 18 ~ Hour 19 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 19 ~ Hour 20 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 20 ~ Hour 21 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 21 ~ Hour 22 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 22 ~ Hour 23 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 23 ~ Hour 24 */
		};
		Asserts.assertEquals(expected1, actual1);
		
		long[] actual2 = TimeSpanUtils.calculateHourDistribution(start, end,
				FILTER_WEEKEND);
		long[] expected2 = {
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 00 ~ Hour 01 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 01 ~ Hour 02 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 02 ~ Hour 03 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 03 ~ Hour 04 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 04 ~ Hour 05 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 05 ~ Hour 06 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 06 ~ Hour 07 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 07 ~ Hour 08 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 08 ~ Hour 09 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 09 ~ Hour 10 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 10 ~ Hour 11 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 11 ~ Hour 12 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 12 ~ Hour 13 */
				(1 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 13 ~ Hour 14 */
				(35 * CalendarUtils.MINUTE_IN_MILLIS
						+ 22 * CalendarUtils.SECOND_IN_MILLIS), /* Hour 14 ~ Hour 15 */
				(0 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 15 ~ Hour 16 */
				(0 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 16 ~ Hour 17 */
				(0 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 17 ~ Hour 18 */
				(0 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 18 ~ Hour 19 */
				(0 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 19 ~ Hour 20 */
				(0 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 20 ~ Hour 21 */
				(0 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 21 ~ Hour 22 */
				(0 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 22 ~ Hour 23 */
				(0 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 23 ~ Hour 24 */
		};
		Asserts.assertEquals(expected2, actual2);
	}
	
	public void testCalculateOverOneDay2HourDistrib() {
		final long start = parseDateTime("2010-02-14 16:25:25.666");
		final long end = parseDateTime("2010-02-15 10:02:33.999");
		
		long[] actual = TimeSpanUtils.calculateHourDistribution(start, end);
		long[] expected = {
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 00 ~ Hour 01 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 01 ~ Hour 02 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 02 ~ Hour 03 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 03 ~ Hour 04 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 04 ~ Hour 05 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 05 ~ Hour 06 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 06 ~ Hour 07 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 07 ~ Hour 08 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 08 ~ Hour 09 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 09 ~ Hour 10 */
				(2 * CalendarUtils.MINUTE_IN_MILLIS
						+ 33 * CalendarUtils.SECOND_IN_MILLIS
						+ 999), /* Hour 10 ~ Hour 11 */
				0, /* Hour 11 ~ Hour 12 */
				0, /* Hour 12 ~ Hour 13 */
				0, /* Hour 13 ~ Hour 14 */
				0, /* Hour 14 ~ Hour 15 */
				0, /* Hour 15 ~ Hour 16 */
				(34 * CalendarUtils.MINUTE_IN_MILLIS
						+ 34 * CalendarUtils.SECOND_IN_MILLIS
						+ 334), /* Hour 16 ~ Hour 17 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 17 ~ Hour 18 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 18 ~ Hour 19 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 19 ~ Hour 20 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 20 ~ Hour 21 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 21 ~ Hour 22 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 22 ~ Hour 23 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 23 ~ Hour 24 */
		};
		
		Asserts.assertEquals(expected, actual);
	}

	public void testCalculateOverWeekHourDistrib() {
		final long start = parseDateTime("2012-12-31 17:20:33.555");
		final long end = parseDateTime("2013-01-08 19:36:24.111");
		
		long[] actual = TimeSpanUtils.calculateHourDistribution(start, end);
		long[] expected = {
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 00 ~ Hour 01 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 01 ~ Hour 02 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 02 ~ Hour 03 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 03 ~ Hour 04 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 04 ~ Hour 05 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 05 ~ Hour 06 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 06 ~ Hour 07 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 07 ~ Hour 08 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 08 ~ Hour 09 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 09 ~ Hour 10 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 10 ~ Hour 11 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 11 ~ Hour 12 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 12 ~ Hour 13 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 13 ~ Hour 14 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 14 ~ Hour 15 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 15 ~ Hour 16 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 16 ~ Hour 17 */
				(8 * CalendarUtils.HOUR_IN_MILLIS
						+ 39 * CalendarUtils.MINUTE_IN_MILLIS
						+ 26 * CalendarUtils.SECOND_IN_MILLIS
						+ 445), /* Hour 17 ~ Hour 18 */
				(9 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 18 ~ Hour 19 */
				(8 * CalendarUtils.HOUR_IN_MILLIS
						+ 36 * CalendarUtils.MINUTE_IN_MILLIS
						+ 24 * CalendarUtils.SECOND_IN_MILLIS
						+ 111), /* Hour 19 ~ Hour 20 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 20 ~ Hour 21 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 21 ~ Hour 22 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 22 ~ Hour 23 */
				(8 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 23 ~ Hour 24 */
		};
		
		Asserts.assertEquals(expected, actual);
	}
	
	public void testCalculateHourDistribOnExistedDistribArray1() {
		final long start = parseDateTime("2012-12-12 00:00:00.000");
		final long end = parseDateTime("2012-12-13 00:00:00.000");
		
		long[] existed = {
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 00 ~ Hour 01 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 01 ~ Hour 02 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 02 ~ Hour 03 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 03 ~ Hour 04 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 04 ~ Hour 05 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 05 ~ Hour 06 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 06 ~ Hour 07 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 07 ~ Hour 08 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 08 ~ Hour 09 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 09 ~ Hour 10 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 10 ~ Hour 11 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 11 ~ Hour 12 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 12 ~ Hour 13 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 13 ~ Hour 14 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 14 ~ Hour 15 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 15 ~ Hour 16 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 16 ~ Hour 17 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 17 ~ Hour 18 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 18 ~ Hour 19 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 19 ~ Hour 20 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 20 ~ Hour 21 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 21 ~ Hour 22 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 22 ~ Hour 23 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 23 ~ Hour 24 */
		};
		
		long[] actual = TimeSpanUtils.calculateHourDistribution(
				existed, start, end);
		long[] expected = {
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 00 ~ Hour 01 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 01 ~ Hour 02 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 02 ~ Hour 03 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 03 ~ Hour 04 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 04 ~ Hour 05 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 05 ~ Hour 06 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 06 ~ Hour 07 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 07 ~ Hour 08 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 08 ~ Hour 09 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 09 ~ Hour 10 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 10 ~ Hour 11 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 11 ~ Hour 12 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 12 ~ Hour 13 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 13 ~ Hour 14 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 14 ~ Hour 15 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 15 ~ Hour 16 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 16 ~ Hour 17 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 17 ~ Hour 18 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 18 ~ Hour 19 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 19 ~ Hour 20 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 20 ~ Hour 21 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 21 ~ Hour 22 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 22 ~ Hour 23 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 23 ~ Hour 24 */
		};
		
		Asserts.assertEquals(expected, actual);
	}

	public void testCalculateHourDistribOnExistedDistribArray2() {
		final long start1 = parseDateTime("2010-01-22 00:35:12.123");
		final long end1 = parseDateTime("2010-01-23 14:35:22.000");
		final long start2 = parseDateTime("2010-02-14 16:25:25.666");
		final long end2 = parseDateTime("2010-02-15 10:02:33.999");
		
		long[] actual = null;
		
		actual = TimeSpanUtils.calculateHourDistribution(
				actual, start1, end1);
		actual = TimeSpanUtils.calculateHourDistribution(
				actual, start2, end2);
		
		long[] expected = {
				(2 * CalendarUtils.HOUR_IN_MILLIS 
						+ 24 * CalendarUtils.MINUTE_IN_MILLIS 
						+ 47 * CalendarUtils.SECOND_IN_MILLIS 
						+ 877), /* Hour 00 ~ Hour 01 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 01 ~ Hour 02 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 02 ~ Hour 03 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 03 ~ Hour 04 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 04 ~ Hour 05 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 05 ~ Hour 06 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 06 ~ Hour 07 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 07 ~ Hour 08 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 08 ~ Hour 09 */
				(3 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 09 ~ Hour 10 */
				(2 * CalendarUtils.HOUR_IN_MILLIS) 
					+ (2 * CalendarUtils.MINUTE_IN_MILLIS
						+ 33 * CalendarUtils.SECOND_IN_MILLIS
						+ 999), /* Hour 10 ~ Hour 11 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 11 ~ Hour 12 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 12 ~ Hour 13 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 13 ~ Hour 14 */
				(CalendarUtils.HOUR_IN_MILLIS 
						+ 35 * CalendarUtils.MINUTE_IN_MILLIS
						+ 22 * CalendarUtils.SECOND_IN_MILLIS), /* Hour 14 ~ Hour 15 */
				CalendarUtils.HOUR_IN_MILLIS, /* Hour 15 ~ Hour 16 */
				CalendarUtils.HOUR_IN_MILLIS 
					+ (34 * CalendarUtils.MINUTE_IN_MILLIS
						+ 34 * CalendarUtils.SECOND_IN_MILLIS
						+ 334), /* Hour 16 ~ Hour 17 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 17 ~ Hour 18 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 18 ~ Hour 19 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 19 ~ Hour 20 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 20 ~ Hour 21 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 21 ~ Hour 22 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 22 ~ Hour 23 */
				(2 * CalendarUtils.HOUR_IN_MILLIS), /* Hour 23 ~ Hour 24 */
		};
		
		Asserts.assertEquals(expected, actual);
	}

	public void testCalculateDaysWithoutFilters() {
		long start = 0;
		long end = 0;
		long actual = 0;
		
		start = parseDateTime("2012-12-31 00:00:00.000");
		end = parseDateTime("2013-01-01 00:00:00.000");
		actual = TimeSpanUtils.calculateDays(start, end);
		assertEquals(1, actual);
		
		start = parseDateTime("2012-12-31 00:00:00.000");
		end = parseDateTime("2013-01-08 00:00:00.000");
		actual = TimeSpanUtils.calculateDays(start, end);
		assertEquals(8, actual);
		
		start = parseDateTime("2012-12-31 00:00:00.000");
		end = parseDateTime("2013-01-08 23:59:00.000");
		actual = TimeSpanUtils.calculateDays(start, end);
		assertEquals(9, actual);
		
		start = parseDateTime("2012-12-31 12:00:00.000");
		end = parseDateTime("2013-01-08 23:59:00.000");
		actual = TimeSpanUtils.calculateDays(start, end);
		assertEquals(9, actual);
	}
	
	public void testCalculateDaysWithFilters() {
		long start = 0;
		long end = 0;
		long actual = 0;
		
		start = parseDateTime("2012-12-31 00:00:00.000");
		end = parseDateTime("2013-01-01 00:00:00.000");
		actual = TimeSpanUtils.calculateDays(start, end, FILTER_WEEKDAYS);
		assertEquals(2, actual);
		actual = TimeSpanUtils.calculateDays(start, end, FILTER_WEEKEND);
		assertEquals(0, actual);
		
		start = parseDateTime("2012-12-31 00:00:00.000");
		end = parseDateTime("2013-01-08 00:00:00.000");
		actual = TimeSpanUtils.calculateDays(start, end, FILTER_WEEKDAYS);
		assertEquals(7, actual);
		actual = TimeSpanUtils.calculateDays(start, end, FILTER_WEEKEND);
		assertEquals(2, actual);
		
		start = parseDateTime("2012-12-31 00:00:00.000");
		end = parseDateTime("2013-01-08 23:59:00.000");
		actual = TimeSpanUtils.calculateDays(start, end, FILTER_WEEKDAYS);
		assertEquals(7, actual);
		actual = TimeSpanUtils.calculateDays(start, end, FILTER_WEEKEND);
		assertEquals(2, actual);
		
		start = parseDateTime("2012-12-31 12:00:00.000");
		end = parseDateTime("2013-01-05 23:59:00.000");
		actual = TimeSpanUtils.calculateDays(start, end, FILTER_WEEKDAYS);
		assertEquals(5, actual);
		actual = TimeSpanUtils.calculateDays(start, end, FILTER_WEEKEND);
		assertEquals(1, actual);
	}
	
	public void testCalculateOverlapDuration() {
		long start1 = 0;
		long end1 = 0;
		long start2 = 0;
		long end2 = 0;
		long actual = 0;
		
		start1 = parseDateTime("2012-12-31 00:00:00.000");
		end1 = parseDateTime("2012-12-31 06:00:00.000");
		start2 = parseDateTime("2012-12-31 02:00:00.000");
		end2 = parseDateTime("2012-12-31 12:00:00.000");
		actual = TimeSpanUtils.calculateOverlapDuration(
				start1, end1, start2, end2);
		assertEquals((4 * CalendarUtils.HOUR_IN_MILLIS), actual);
		actual = TimeSpanUtils.calculateOverlapDuration(
				start2, end2, start1, end1);
		assertEquals((4 * CalendarUtils.HOUR_IN_MILLIS), actual);
		
		start1 = parseDateTime("2012-12-31 00:00:00.000");
		end1 = parseDateTime("2013-01-01 00:00:00.000");
		start2 = parseDateTime("2012-12-31 12:00:00.000");
		end2 = parseDateTime("2013-01-01 12:00:00.000");
		actual = TimeSpanUtils.calculateOverlapDuration(
				start1, end1, start2, end2);
		assertEquals((12 * CalendarUtils.HOUR_IN_MILLIS), actual);
		actual = TimeSpanUtils.calculateOverlapDuration(
				start2, end2, start1, end1);
		assertEquals((12 * CalendarUtils.HOUR_IN_MILLIS), actual);
		
		start1 = parseDateTime("2013-01-22 09:30:05.888");
		end1 = parseDateTime("2013-01-25 15:26:00.234");
		start2 = parseDateTime("2013-01-23 12:16:00.000");
		end2 = parseDateTime("2013-01-26 12:00:00.000");
		actual = TimeSpanUtils.calculateOverlapDuration(
				start1, end1, start2, end2);
		assertEquals((2 * CalendarUtils.DAY_IN_MILLIS
				+ 3 * CalendarUtils.HOUR_IN_MILLIS
				+ 10 * CalendarUtils.MINUTE_IN_MILLIS
				+ 234), actual);
		actual = TimeSpanUtils.calculateOverlapDuration(
				start2, end2, start1, end1);
		assertEquals((2 * CalendarUtils.DAY_IN_MILLIS
				+ 3 * CalendarUtils.HOUR_IN_MILLIS
				+ 10 * CalendarUtils.MINUTE_IN_MILLIS
				+ 234), actual);
		
		start1 = parseDateTime("2012-10-17 00:00:00.000");
		end1 = parseDateTime("2013-01-04 00:00:00.000");
		start2 = parseDateTime("2013-02-14 12:00:00.000");
		end2 = parseDateTime("2013-02-28 12:00:00.000");
		actual = TimeSpanUtils.calculateOverlapDuration(
				start1, end1, start2, end2);
		assertEquals(0, actual);
		actual = TimeSpanUtils.calculateOverlapDuration(
				start2, end2, start1, end1);
		assertEquals(0, actual);
		
		start1 = parseDateTime("2013-01-04 00:00:00.000");
		end1 = parseDateTime("2012-10-17 00:00:00.000");
		start2 = parseDateTime("2013-02-14 12:00:00.000");
		end2 = parseDateTime("2013-02-28 12:00:00.000");
		actual = TimeSpanUtils.calculateOverlapDuration(
				start1, end1, start2, end2);
		assertEquals(0, actual);
		actual = TimeSpanUtils.calculateOverlapDuration(
				start2, end2, start1, end1);
		assertEquals(0, actual);
		
		start1 = parseDateTime("2012-10-17 00:00:00.000");
		end1 = parseDateTime("2013-01-04 00:00:00.000");
		start2 = parseDateTime("2013-02-14 12:00:00.000");
		end2 = parseDateTime("2013-02-28 12:00:00.000");
		actual = TimeSpanUtils.calculateOverlapDuration(
				start1, end1, start2, end2);
		assertEquals(0, actual);
		actual = TimeSpanUtils.calculateOverlapDuration(
				start2, end2, start1, end1);
		assertEquals(0, actual);
		
		start1 = parseDateTime("2013-01-04 00:00:00.000");
		end1 = parseDateTime("2012-10-17 00:00:00.000");
		start2 = parseDateTime("2013-02-28 12:00:00.000");
		end2 = parseDateTime("2013-02-14 12:00:00.000");
		actual = TimeSpanUtils.calculateOverlapDuration(
				start1, end1, start2, end2);
		assertEquals(0, actual);
		actual = TimeSpanUtils.calculateOverlapDuration(
				start2, end2, start1, end1);
		assertEquals(0, actual);
	}
	
}
