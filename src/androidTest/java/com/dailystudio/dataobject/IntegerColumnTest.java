package com.dailystudio.dataobject;

import android.content.ContentValues;
import android.content.Intent;
import android.test.AndroidTestCase;

import com.dailystudio.dataobject.Column;

public class IntegerColumnTest extends AndroidTestCase {
	
	private final static String COL_TYPE_INTEGER = "INTEGER";
	
	private static class SimpleIntegerCursor extends SimpleCursor<Integer> {
		
		@Override
		public int getInt(int columnIndex) {
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
		public long getLong(int columnIndex) {
			throw new IllegalArgumentException("not supported");
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
	
	public void testCreateAnIntegerColumn() {
		Column column = null;
		
		column = new IntegerColumn("_id");
		assertNotNull(column);
		assertEquals("_id", column.getName());
		assertEquals(COL_TYPE_INTEGER, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
	
		column = new IntegerColumn("_id", false);
		assertNotNull(column);
		assertEquals("_id", column.getName());
		assertEquals(COL_TYPE_INTEGER, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new IntegerColumn("_id",  false, true);
		assertNotNull(column);
		assertEquals("_id", column.getName());
		assertEquals(COL_TYPE_INTEGER, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new IntegerColumn("_id", 2);
		assertNotNull(column);
		assertEquals("_id", column.getName());
		assertEquals(COL_TYPE_INTEGER, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(2, column.getVerion());
	
		column = new IntegerColumn("_id", false, 3);
		assertNotNull(column);
		assertEquals("_id", column.getName());
		assertEquals(COL_TYPE_INTEGER, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(3, column.getVerion());
		
		column = new IntegerColumn("_id",  false, true, 4);
		assertNotNull(column);
		assertEquals("_id", column.getName());
		assertEquals(COL_TYPE_INTEGER, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(4, column.getVerion());
	}
	
	public void testColumnToString() {
		Column column = null;
		
		column = new IntegerColumn("_id");
		assertNotNull(column);
		assertEquals("_id INTEGER", column.toString());
	
		column = new IntegerColumn("_id", false);
		assertNotNull(column);
		assertEquals("_id INTEGER NOT NULL", column.toString());
		
		column = new IntegerColumn("_id", false, true);
		assertNotNull(column);
		assertEquals("_id INTEGER NOT NULL PRIMARY KEY", column.toString());
	}

	public void testSetValue() {
		Column column = null;
		
		column = new IntegerColumn("columnA");
		assertNotNull(column);
		
		ContentValues values = new ContentValues();
		
		column.setValue(values, 123);
		assertEquals(new Integer(123), values.getAsInteger("columnA"));
		
		column.setValue(values, -12345678);
		assertEquals(new Integer(-12345678), values.getAsInteger("columnA"));
		
		boolean exceptionCacthed = false;
		try {
			column.setValue(values, 3.1415);
			exceptionCacthed = false;
		} catch (ClassCastException e) {
			exceptionCacthed = true;
		} 
		
		assertEquals(true, exceptionCacthed);
	}

	public void testGetValue() {
		Column columnA = new IntegerColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new IntegerColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		
		columnA.setValue(values, 123);
		assertEquals(new Integer(123), columnA.getValue(values));
		
		columnB.setValue(values, -12345678);
		assertEquals(new Integer(-12345678), columnB.getValue(values));
	}

	public void testMatchValueType() {
		Column column = null;
		
		column = new IntegerColumn("columnA");
		assertNotNull(column);

		assertEquals(true, column.matchColumnType(123));
		assertEquals(true, column.matchColumnType(-12345678));
		assertEquals(false, column.matchColumnType(3.1415));
		assertEquals(false, column.matchColumnType(123456789l));
		assertEquals(false, column.matchColumnType("String"));
	}

	public void testAttachValueTo() {
		Column columnA = new IntegerColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new IntegerColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.setValue(values, 2011);
		columnB.setValue(values, 1982);
		
		Intent i = new Intent();
		assertNotNull(i);
		
		columnA.attachValueTo(i, values);
		columnB.attachValueTo(i, values);
		
		int expected = 0;
		
		expected = i.getIntExtra(columnA.getName(), -1);
		assertEquals(expected, 2011);
		expected = i.getIntExtra(columnB.getName(), -1);
		assertEquals(expected, 1982);
	}

	public void testParseValueFrom() {
		Column columnA = new IntegerColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new IntegerColumn("columnB");
		assertNotNull(columnB);
		
		SimpleIntegerCursor c = new SimpleIntegerCursor();
		c.putColumnValue(columnA.getName(), 2011);
		c.putColumnValue(columnB.getName(), 1982);
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.parseValueFrom(c, values);
		columnB.parseValueFrom(c, values);
		
		int expected = 0;
		
		expected = values.getAsInteger(columnA.getName());
		assertEquals(expected, 2011);
		expected = values.getAsInteger(columnB.getName());
		assertEquals(expected, 1982);
	}

	public void testConvertValueToString() {
		Column column = new IntegerColumn("column");
		assertNotNull(column);
		
		String expected = null;
		String actual = null;
		
		expected = "2012";
		actual = column.convertValueToString(2012);
		assertEquals(expected, actual);
		
		expected = "-123456789";
		actual = column.convertValueToString(-123456789);
		assertEquals(expected, actual);
	}

}
