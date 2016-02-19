package com.dailystudio.dataobject;

import android.content.ContentValues;
import android.content.Intent;
import android.test.AndroidTestCase;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;

public class TimeColumnTest extends AndroidTestCase {
	
	private final static String COL_TYPE_TIME = "LONG";
	
	private static class SimpleLongCursor extends SimpleCursor<Long> {
		
		@Override
		public int getInt(int columnIndex) {
			throw new IllegalArgumentException("not supported");
		}

		@Override
		public long getLong(int columnIndex) {
			if (mValues == null) {
				return -1;
			}
			
			final int N = mValues.size();
			if (columnIndex < 0 || columnIndex >= N) {
				return -1;
			}
			
			return mValues.get(columnIndex);
		}

		@Override
		public double getDouble(int columnIndex) {
			throw new IllegalArgumentException("not supported");
		}
		
		@Override
		public String getString(int columnIndex) {
			throw new IllegalArgumentException("not supported");
		}
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateATimeColumn() {
		Column column = null;
		
		column = new TimeColumn("time");
		assertNotNull(column);
		assertEquals("time", column.getName());
		assertEquals(COL_TYPE_TIME, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
	
		column = new TimeColumn("time", false);
		assertNotNull(column);
		assertEquals("time", column.getName());
		assertEquals(COL_TYPE_TIME, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new TimeColumn("time",  false, true);
		assertNotNull(column);
		assertEquals("time", column.getName());
		assertEquals(COL_TYPE_TIME, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new TimeColumn("time", 2);
		assertNotNull(column);
		assertEquals("time", column.getName());
		assertEquals(COL_TYPE_TIME, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(2, column.getVerion());
	
		column = new TimeColumn("time", false, 3);
		assertNotNull(column);
		assertEquals("time", column.getName());
		assertEquals(COL_TYPE_TIME, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(3, column.getVerion());
		
		column = new TimeColumn("time",  false, true, 4);
		assertNotNull(column);
		assertEquals("time", column.getName());
		assertEquals(COL_TYPE_TIME, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(4, column.getVerion());
	}
	
	public void testColumnToString() {
		Column column = null;
		
		column = new TimeColumn("time");
		assertNotNull(column);
		assertEquals("time LONG", column.toString());
	
		column = new TimeColumn("time", false);
		assertNotNull(column);
		assertEquals("time LONG NOT NULL", column.toString());
		
		column = new TimeColumn("time", false, true);
		assertNotNull(column);
		assertEquals("time LONG NOT NULL PRIMARY KEY", column.toString());
	}

	public void testSetValue() {
		Column column = null;
		
		column = new TimeColumn("columnA");
		assertNotNull(column);
		
		ContentValues values = new ContentValues();
		
		column.setValue(values, 987654321012345678l);
		assertEquals(Long.valueOf(987654321012345678l), values.getAsLong("columnA"));
		
		column.setValue(values, -12345678l);
		assertEquals(Long.valueOf(-12345678l), values.getAsLong("columnA"));
		
		boolean exceptionCacthed = false;
		try {
			column.setValue(values, 123);
			exceptionCacthed = false;
		} catch (ClassCastException e) {
			exceptionCacthed = true;
		} 
		
		assertEquals(true, exceptionCacthed);
		
		try {
			column.setValue(values, 3.1415);
			exceptionCacthed = false;
		} catch (ClassCastException e) {
			exceptionCacthed = true;
		} 
		
		assertEquals(true, exceptionCacthed);
	}

	public void testGetValue() {
		Column columnA = new TimeColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new TimeColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		
		columnA.setValue(values, 987654321012345678l);
		assertEquals(Long.valueOf(987654321012345678l), columnA.getValue(values));
		
		columnB.setValue(values, -12345678l);
		assertEquals(Long.valueOf(-12345678l), columnB.getValue(values));
	}

	public void testMatchValueType() {
		Column column = null;
		
		column = new TimeColumn("columnA");
		assertNotNull(column);

		assertEquals(true, column.matchColumnType(987654321012345678l));
		assertEquals(true, column.matchColumnType(-12345678l));
		assertEquals(false, column.matchColumnType(-2012));
		assertEquals(false, column.matchColumnType(3.1415));
		assertEquals(false, column.matchColumnType("String"));
	}

	public void testAttachValueTo() {
		Column columnA = new TimeColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new TimeColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.setValue(values, 987654321012345678l);
		columnB.setValue(values, -1234567654321l);
		
		Intent i = new Intent();
		assertNotNull(i);
		
		columnA.attachValueTo(i, values);
		columnB.attachValueTo(i, values);
		
		long actual = 0;
		
		actual = i.getLongExtra(columnA.getName(), -1l);
		assertEquals(987654321012345678l, actual);
		actual = i.getLongExtra(columnB.getName(), -1l);
		assertEquals(-1234567654321l, actual);
	}

	public void testParseValueFrom() {
		Column columnA = new TimeColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new TimeColumn("columnB");
		assertNotNull(columnB);
		
		SimpleLongCursor c = new SimpleLongCursor();
		c.putColumnValue(columnA.getName(), 987654321012345678l);
		c.putColumnValue(columnB.getName(), -1234567654321l);
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.parseValueFrom(c, values);
		columnB.parseValueFrom(c, values);
		
		long actual = 0;
		
		actual = values.getAsLong(columnA.getName());
		assertEquals(987654321012345678l, actual);
		actual = values.getAsLong(columnB.getName());
		assertEquals(-1234567654321l, actual);
	}

	public void testConvertValueToString() {
		Column column = new TimeColumn("column");
		assertNotNull(column);
		
		String expected = null;
		String actual = null;
		
		expected = "987654321012345678";
		actual = column.convertValueToString(987654321012345678l);
		assertEquals(expected, actual);
		
		expected = "-1234567654321";
		actual = column.convertValueToString(-1234567654321l);
		assertEquals(expected, actual);
	}

	public void testTimeInDay() {
		TimeColumn column = new TimeColumn("time");
		assertNotNull(column);
		
		ExpressionToken exp = column.timeInDay();
		String expected = null;;
		
		expected = "( time % 86400000 )";
		assertEquals(expected, exp.toString());
	}
	
	public void testGroupBy() {
		TimeColumn column = new TimeColumn("time");
		assertNotNull(column);
		
		OrderingToken token = null;
		String expected = null;
		
		token = column.groupByHour();
		expected = "CAST ( strftime('%H', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, token.toString());
		
		token = column.groupByMinute();
		expected = "CAST ( strftime('%M', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, token.toString());
		
		token = column.groupBySecond();
		expected = "CAST ( strftime('%S', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, token.toString());
		
		token = column.groupByDay();
		expected = "CAST ( strftime('%d', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, token.toString());
		
		token = column.groupByWeekday();
		expected = "CAST ( strftime('%w', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, token.toString());
		
		token = column.groupByWeek();
		expected = "CAST ( strftime('%W', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, token.toString());
		
		token = column.groupByMonth();
		expected = "CAST ( strftime('%m', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, token.toString());
		
		token = column.groupByYear();
		expected = "CAST ( strftime('%Y', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertEquals(expected, token.toString());
	}
	
	public void testTimeColumns() {
		TimeColumn source = new TimeColumn("time");
		assertNotNull(source);
		
		Column column = null;
		String expected = null;
		
		column = source.HOUR();
		expected = "CAST ( strftime('%H', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertNotNull(column);
		assertTrue((column instanceof IntegerColumn));
		assertEquals(expected, column.getName());
		
		column = source.MINUTE();
		expected = "CAST ( strftime('%M', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertNotNull(column);
		assertTrue((column instanceof IntegerColumn));
		assertEquals(expected, column.getName());
		
		column = source.SECOND();
		expected = "CAST ( strftime('%S', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertNotNull(column);
		assertTrue((column instanceof IntegerColumn));
		assertEquals(expected, column.getName());
		
		column = source.DAY();
		expected = "CAST ( strftime('%d', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertNotNull(column);
		assertTrue((column instanceof IntegerColumn));
		assertEquals(expected, column.getName());
		
		column = source.WEEKDAY();
		expected = "CAST ( strftime('%w', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertNotNull(column);
		assertTrue((column instanceof IntegerColumn));
		assertEquals(expected, column.getName());
		
		column = source.MONTH();
		expected = "CAST ( strftime('%m', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertNotNull(column);
		assertTrue((column instanceof IntegerColumn));
		assertEquals(expected, column.getName());
		
		column = source.YEAR();
		expected = "CAST ( strftime('%Y', (time / 1000), 'unixepoch', 'localtime') AS INTEGER )";
		assertNotNull(column);
		assertTrue((column instanceof IntegerColumn));
		assertEquals(expected, column.getName());
	}
	
}
