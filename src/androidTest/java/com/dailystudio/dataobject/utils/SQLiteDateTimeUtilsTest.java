package com.dailystudio.dataobject.utils;

import android.test.AndroidTestCase;

public class SQLiteDateTimeUtilsTest extends AndroidTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testToDateTimeString() {
		String format = "%Y-%m-%d %H:%M:%S";
		String time = "time_column";
		
		assertEquals("CAST ( strftime('%Y-%m-%d %H:%M:%S', (time_column / 1000), 'unixepoch', 'localtime') AS INTEGER )",
				SQLiteDateTimeUtils.toDateTimeString(format, time));
	}

	public void testHourOf() {
		String expected = null;
		String time = null;
		long now = 0;
		
		time = "time_column";
		expected = "CAST ( strftime('%H', (time_column / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, SQLiteDateTimeUtils.hourOf(time));
		
		now = System.currentTimeMillis();
		expected = String.format("CAST ( strftime('%%H', (%d / 1000), 'unixepoch', 'localtime') AS INTEGER )",
				now);
		assertEquals(expected, SQLiteDateTimeUtils.hourOf(now));
	}

	public void testMinuteOf() {
		String expected = null;
		String time = null;
		long now = 0;
		
		time = "time_column";
		expected = "CAST ( strftime('%M', (time_column / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, SQLiteDateTimeUtils.minuteOf(time));
		
		now = System.currentTimeMillis();
		expected = String.format("CAST ( strftime('%%M', (%d / 1000), 'unixepoch', 'localtime') AS INTEGER )",
				now);
		assertEquals(expected, SQLiteDateTimeUtils.minuteOf(now));
	}

	public void testSecondOf() {
		String expected = null;
		String time = null;
		long now = 0;
		
		time = "time_column";
		expected = "CAST ( strftime('%S', (time_column / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, SQLiteDateTimeUtils.secondOf(time));
		
		now = System.currentTimeMillis();
		expected = String.format("CAST ( strftime('%%S', (%d / 1000), 'unixepoch', 'localtime') AS INTEGER )",
				now);
		assertEquals(expected, SQLiteDateTimeUtils.secondOf(now));
	}

	public void testDayOf() {
		String expected = null;
		String time = null;
		long now = 0;
		
		time = "time_column";
		expected = "CAST ( strftime('%d', (time_column / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, SQLiteDateTimeUtils.dayOf(time));
		
		now = System.currentTimeMillis();
		expected = String.format("CAST ( strftime('%%d', (%d / 1000), 'unixepoch', 'localtime') AS INTEGER )",
				now);
		assertEquals(expected, SQLiteDateTimeUtils.dayOf(now));
	}

	public void testWeekdayOf() {
		String expected = null;
		String time = null;
		long now = 0;
		
		time = "time_column";
		expected = "CAST ( strftime('%w', (time_column / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, SQLiteDateTimeUtils.weekdayOf(time));
		
		now = System.currentTimeMillis();
		expected = String.format("CAST ( strftime('%%w', (%d / 1000), 'unixepoch', 'localtime') AS INTEGER )",
				now);
		assertEquals(expected, SQLiteDateTimeUtils.weekdayOf(now));
	}

	public void testWeekOf() {
		String expected = null;
		String time = null;
		long now = 0;
		
		time = "time_column";
		expected = "CAST ( strftime('%W', (time_column / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, SQLiteDateTimeUtils.weekOf(time));
		
		now = System.currentTimeMillis();
		expected = String.format("CAST ( strftime('%%W', (%d / 1000), 'unixepoch', 'localtime') AS INTEGER )",
				now);
		assertEquals(expected, SQLiteDateTimeUtils.weekOf(now));
	}
	
	public void testMonthOf() {
		String expected = null;
		String time = null;
		long now = 0;
		
		time = "time_column";
		expected = "CAST ( strftime('%m', (time_column / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, SQLiteDateTimeUtils.monthOf(time));
		
		now = System.currentTimeMillis();
		expected = String.format("CAST ( strftime('%%m', (%d / 1000), 'unixepoch', 'localtime') AS INTEGER )",
				now);
		assertEquals(expected, SQLiteDateTimeUtils.monthOf(now));
	}

	public void testYearOf() {
		String expected = null;
		String time = null;
		long now = 0;
		
		time = "time_column";
		expected = "CAST ( strftime('%Y', (time_column / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, SQLiteDateTimeUtils.yearOf(time));
		
		now = System.currentTimeMillis();
		expected = String.format("CAST ( strftime('%%Y', (%d / 1000), 'unixepoch', 'localtime') AS INTEGER )",
				now);
		assertEquals(expected, SQLiteDateTimeUtils.yearOf(now));
	}
	
}
