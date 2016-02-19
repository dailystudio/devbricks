package com.dailystudio.dataobject;

import android.content.ContentValues;
import android.content.Intent;
import android.test.AndroidTestCase;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.query.QueryToken;

public class TextColumnTest extends AndroidTestCase {
	
	private final static String COL_TYPE_TEXT = "TEXT";
	
	private static class SimpleStringCursor extends SimpleCursor<String> {
		
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
			throw new IllegalArgumentException("not supported");
		}
		
		@Override
		public String getString(int columnIndex) {
			if (mValues == null) {
				return null;
			}
			
			final int N = mValues.size();
			if (columnIndex < 0 || columnIndex >= N) {
				return null;
			}
			
			return mValues.get(columnIndex);
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
	
	public void testCreateATextColumn() {
		Column column = null;
		
		column = new TextColumn("content");
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_TEXT, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
	
		column = new TextColumn("content", false);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_TEXT, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new TextColumn("content",  false, true);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_TEXT, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new TextColumn("content", 2);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_TEXT, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(2, column.getVerion());
	
		column = new TextColumn("content", false, 3);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_TEXT, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(3, column.getVerion());
		
		column = new TextColumn("content",  false, true, 4);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_TEXT, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(4, column.getVerion());
	}
	
	public void testColumnToString() {
		Column column = null;
		
		column = new TextColumn("content");
		assertNotNull(column);
		assertEquals("content TEXT", column.toString());
	
		column = new TextColumn("content", false);
		assertNotNull(column);
		assertEquals("content TEXT NOT NULL", column.toString());
		
		column = new TextColumn("content", false, true);
		assertNotNull(column);
		assertEquals("content TEXT NOT NULL PRIMARY KEY", column.toString());
	}

	public void testSetValue() {
		Column column = null;
		
		column = new TextColumn("columnA");
		assertNotNull(column);
		
		ContentValues values = new ContentValues();
		
		column.setValue(values, "Sample Text 1");
		assertEquals("Sample Text 1", values.getAsString("columnA"));
		
		column.setValue(values, "Hello world!");
		assertEquals("Hello world!", values.getAsString("columnA"));
		
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
		Column columnA = new TextColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new TextColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		
		columnA.setValue(values, "Sample Text 1");
		assertEquals("Sample Text 1", columnA.getValue(values));
		
		columnB.setValue(values, "Hello world!");
		assertEquals("Hello world!", columnB.getValue(values));
	}

	public void testMatchValueType() {
		Column column = null;
		
		column = new TextColumn("columnA");
		assertNotNull(column);

		assertEquals(false, column.matchColumnType(987654321012345678l));
		assertEquals(false, column.matchColumnType(-12345678l));
		assertEquals(false, column.matchColumnType(-2012));
		assertEquals(false, column.matchColumnType(3.1415));
		assertEquals(true, column.matchColumnType("String"));
	}

	public void testAttachValueTo() {
		Column columnA = new TextColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new TextColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.setValue(values, "Sample Text 1");
		columnB.setValue(values, "Hello world!");
		
		Intent i = new Intent();
		assertNotNull(i);
		
		columnA.attachValueTo(i, values);
		columnB.attachValueTo(i, values);
		
		String actual = null;
		
		actual = i.getStringExtra(columnA.getName());
		assertEquals("Sample Text 1", actual);
		actual = i.getStringExtra(columnB.getName());
		assertEquals("Hello world!", actual);
	}

	public void testParseValueFrom() {
		Column columnA = new TextColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new TextColumn("columnB");
		assertNotNull(columnB);
		
		SimpleStringCursor c = new SimpleStringCursor();
		c.putColumnValue(columnA.getName(), "Sample Text 1");
		c.putColumnValue(columnB.getName(), "Hello world!");
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.parseValueFrom(c, values);
		columnB.parseValueFrom(c, values);
		
		String actual = null;
		
		actual = values.getAsString(columnA.getName());
		assertEquals("Sample Text 1", actual);
		actual = values.getAsString(columnB.getName());
		assertEquals("Hello world!", actual);
	}

	public void testConvertValueToString() {
		Column column = new TextColumn("column");
		assertNotNull(column);

		String expected = null;
		String actual = null;
		
		expected = "\'Sample Text 1\'";
		actual = column.convertValueToString("Sample Text 1");
		assertEquals(expected, actual);
		
		expected = "\'Hello world!\'";
		actual = column.convertValueToString("Hello world!");
		assertEquals(expected, actual);
	}

	public void testLikeOperator() {
		TextColumn column = null;
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal LIKE \'%abc%\'"), column.like("%abc%"));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.eq(1000));
	}

}
