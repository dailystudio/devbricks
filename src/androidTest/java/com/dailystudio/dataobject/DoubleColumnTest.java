package com.dailystudio.dataobject;

import android.content.ContentValues;
import android.content.Intent;
import android.test.AndroidTestCase;

import com.dailystudio.dataobject.Column;

public class DoubleColumnTest extends AndroidTestCase {
	
	private final static String COL_TYPE_DOUBLE = "DOUBLE";
	
	private static class SimpleDoubleCursor extends SimpleCursor<Double> {
		
		@Override
		public int getInt(int columnIndex) {
			throw new IllegalArgumentException("not supported");
		}

		@Override
		public long getLong(int columnIndex) {
			throw new IllegalArgumentException("not supported");
		}

		@Override
		public double getDouble(int columnIndex) {
			if (mValues == null) {
				return 0.f;
			}
			
			final int N = mValues.size();
			if (columnIndex < 0 || columnIndex >= N) {
				return 0.f;
			}
			
			return mValues.get(columnIndex);
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
	
	public void testCreateADoubleColumn() {
		Column column = null;
		
		column = new DoubleColumn("fp");
		assertNotNull(column);
		assertEquals("fp", column.getName());
		assertEquals(COL_TYPE_DOUBLE, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
	
		column = new DoubleColumn("fp", false);
		assertNotNull(column);
		assertEquals("fp", column.getName());
		assertEquals(COL_TYPE_DOUBLE, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new DoubleColumn("fp",  false, true);
		assertNotNull(column);
		assertEquals("fp", column.getName());
		assertEquals(COL_TYPE_DOUBLE, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new DoubleColumn("fp", 2);
		assertNotNull(column);
		assertEquals("fp", column.getName());
		assertEquals(COL_TYPE_DOUBLE, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(2, column.getVerion());
	
		column = new DoubleColumn("fp", false, 3);
		assertNotNull(column);
		assertEquals("fp", column.getName());
		assertEquals(COL_TYPE_DOUBLE, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(3, column.getVerion());
		
		column = new DoubleColumn("fp",  false, true, 4);
		assertNotNull(column);
		assertEquals("fp", column.getName());
		assertEquals(COL_TYPE_DOUBLE, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(4, column.getVerion());
	}
	
	public void testColumnToString() {
		Column column = null;
		
		column = new DoubleColumn("fp");
		assertNotNull(column);
		assertEquals("fp DOUBLE", column.toString());
	
		column = new DoubleColumn("fp", false);
		assertNotNull(column);
		assertEquals("fp DOUBLE NOT NULL", column.toString());
		
		column = new DoubleColumn("fp", false, true);
		assertNotNull(column);
		assertEquals("fp DOUBLE NOT NULL PRIMARY KEY", column.toString());
	}

	public void testSetValue() {
		Column column = null;
		
		column = new DoubleColumn("columnA");
		assertNotNull(column);
		
		ContentValues values = new ContentValues();
		
		column.setValue(values, 3.141562965354);
		assertEquals(new Double(3.141562965354), values.getAsDouble("columnA"));
		
		column.setValue(values, -0.1234567890);
		assertEquals(new Double(-0.1234567890), values.getAsDouble("columnA"));
		
		boolean exceptionCacthed = false;
		try {
			column.setValue(values, 123);
			exceptionCacthed = false;
		} catch (ClassCastException e) {
			exceptionCacthed = true;
		} 
		
		assertEquals(true, exceptionCacthed);
	}

	public void testGetValue() {
		Column columnA = new DoubleColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new DoubleColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		
		columnA.setValue(values, 3.141562965354);
		assertEquals(new Double(3.141562965354), columnA.getValue(values));
		
		columnB.setValue(values, -0.1234567890);
		assertEquals(new Double(-0.1234567890), columnB.getValue(values));
	}

	public void testMatchValueType() {
		Column column = null;
		
		column = new DoubleColumn("columnA");
		assertNotNull(column);

		assertEquals(false, column.matchColumnType(987654321012345678l));
		assertEquals(false, column.matchColumnType(-12345678l));
		assertEquals(false, column.matchColumnType(-2012));
		assertEquals(true, column.matchColumnType(1.414));
		assertEquals(false, column.matchColumnType("String"));
	}

	public void testAttachValueTo() {
		Column columnA = new DoubleColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new DoubleColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.setValue(values, 3.141562965354);
		columnB.setValue(values, -0.1234567890);
		
		Intent i = new Intent();
		assertNotNull(i);
		
		columnA.attachValueTo(i, values);
		columnB.attachValueTo(i, values);
		
		double actual = 0.f;
		
		actual = i.getDoubleExtra(columnA.getName(), .0f);
		assertEquals(3.141562965354, actual);
		actual = i.getDoubleExtra(columnB.getName(), .0f);
		assertEquals(-0.1234567890, actual);
	}

	public void testParseValueFrom() {
		Column columnA = new DoubleColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new DoubleColumn("columnB");
		assertNotNull(columnB);
		
		SimpleDoubleCursor c = new SimpleDoubleCursor();
		c.putColumnValue(columnA.getName(), 3.14159265354);
		c.putColumnValue(columnB.getName(), -0.1234567890);
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.parseValueFrom(c, values);
		columnB.parseValueFrom(c, values);
		
		double actual = 0.f;
		
		actual = values.getAsDouble(columnA.getName());
		assertEquals(3.14159265354, actual);
		actual = values.getAsDouble(columnB.getName());
		assertEquals(-0.1234567890, actual);
	}

	public void testConvertValueToString() {
		Column column = new DoubleColumn("column");
		assertNotNull(column);

		String expected = null;
		String actual = null;
		
		expected = "3.14159265354";
		actual = column.convertValueToString(3.14159265354);
		assertEquals(expected, actual);
		
		expected = "-0.123456789";
		actual = column.convertValueToString(-0.123456789);
		assertEquals(expected, actual);
	}

}
