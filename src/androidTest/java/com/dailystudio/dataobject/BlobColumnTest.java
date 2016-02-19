package com.dailystudio.dataobject;

import android.content.ContentValues;
import android.content.Intent;
import android.test.AndroidTestCase;

import com.dailystudio.dataobject.Column;
import com.dailystudio.test.Asserts;

public class BlobColumnTest extends AndroidTestCase {
	
	private final static String COL_TYPE_BLOB = "BLOB";
	
	private static class SimpleStringCursor extends SimpleCursor<byte[]> {
		
		@Override
		public byte[] getBlob(int columnIndex) {
			if (mValues == null) {
				return null;
			}
			
			final int N = mValues.size();
			if (columnIndex < 0 || columnIndex >= N) {
				return null;
			}
			
			return mValues.get(columnIndex);
		}
		
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
			throw new IllegalArgumentException("not supported");
		}
		
	}
	
	private final static byte[] BINARY_A = {
		0x00, 0x01, 0x02, 0x03,
		0x04, 0x05, 0x06, 0x07,
		0x08, 0x09, 0x0A, 0x0B,
		0x0C, 0x0D, 0x0E, 0x0F,
	};
	
	private final static byte[] BINARY_B = {
		(byte)0xFF, (byte)0xFE, (byte)0xFD, (byte)0xFC,
		(byte)0xFB, (byte)0xFA, (byte)0xF9, (byte)0xF8,
		(byte)0xF7, (byte)0xF6, (byte)0xF5, (byte)0xF4,
		(byte)0xF3, (byte)0xF2, (byte)0xF1, (byte)0xF0,
	};
	
	private final static byte[] BINARY_C = {};
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateABlobColumn() {
		Column column = null;
		
		column = new BlobColumn("content");
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_BLOB, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
	
		column = new BlobColumn("content", false);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_BLOB, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new BlobColumn("content",  false, true);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_BLOB, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new BlobColumn("content", 2);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_BLOB, column.getType());
		assertEquals(true, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(2, column.getVerion());
	
		column = new BlobColumn("content", false, 3);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_BLOB, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(false, column.isPrimary());
		assertEquals(3, column.getVerion());
		
		column = new BlobColumn("content",  false, true, 4);
		assertNotNull(column);
		assertEquals("content", column.getName());
		assertEquals(COL_TYPE_BLOB, column.getType());
		assertEquals(false, column.isAllowNull());
		assertEquals(true, column.isPrimary());
		assertEquals(4, column.getVerion());
	}
	
	public void testColumnToString() {
		Column column = null;
		
		column = new BlobColumn("content");
		assertNotNull(column);
		assertEquals("content BLOB", column.toString());
	
		column = new BlobColumn("content", false);
		assertNotNull(column);
		assertEquals("content BLOB NOT NULL", column.toString());
		
		column = new BlobColumn("content", false, true);
		assertNotNull(column);
		assertEquals("content BLOB NOT NULL PRIMARY KEY", column.toString());
	}

	public void testSetValue() {
		Column column = null;
		
		column = new BlobColumn("columnA");
		assertNotNull(column);
		
		ContentValues values = new ContentValues();
		
		column.setValue(values, BINARY_A);
		Asserts.assertEquals(BINARY_A, values.getAsByteArray("columnA"));
		
		column.setValue(values, BINARY_B);
		Asserts.assertEquals(BINARY_B, values.getAsByteArray("columnA"));
		
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
		
		try {
			column.setValue(values, "Text");
			exceptionCacthed = false;
		} catch (ClassCastException e) {
			exceptionCacthed = true;
		} 
		
		assertEquals(true, exceptionCacthed);
	}

	public void testGetValue() {
		Column columnA = new BlobColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new BlobColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		
		columnA.setValue(values, BINARY_A);
		Asserts.assertEquals(BINARY_A, (byte[])columnA.getValue(values));
		
		columnB.setValue(values, BINARY_B);
		Asserts.assertEquals(BINARY_B, (byte[])columnB.getValue(values));
	}

	public void testMatchValueType() {
		Column column = null;
		
		column = new BlobColumn("columnA");
		assertNotNull(column);

		assertEquals(false, column.matchColumnType(987654321012345678l));
		assertEquals(false, column.matchColumnType(-12345678l));
		assertEquals(false, column.matchColumnType(-2012));
		assertEquals(false, column.matchColumnType(3.1415));
		assertEquals(false, column.matchColumnType("String"));
		assertEquals(true, column.matchColumnType(BINARY_A));
	}

	public void testAttachValueTo() {
		Column columnA = new BlobColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new BlobColumn("columnB");
		assertNotNull(columnB);
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.setValue(values, BINARY_A);
		columnB.setValue(values, BINARY_B);
		
		Intent i = new Intent();
		assertNotNull(i);
		
		columnA.attachValueTo(i, values);
		columnB.attachValueTo(i, values);
		
		byte[] actual = null;
		
		actual = i.getByteArrayExtra(columnA.getName());
		Asserts.assertEquals(BINARY_A, actual);
		actual = i.getByteArrayExtra(columnB.getName());
		Asserts.assertEquals(BINARY_B, actual);
	}

	public void testParseValueFrom() {
		Column columnA = new BlobColumn("columnA");
		assertNotNull(columnA);
		
		Column columnB = new BlobColumn("columnB");
		assertNotNull(columnB);
		
		SimpleStringCursor c = new SimpleStringCursor();
		c.putColumnValue(columnA.getName(), BINARY_A);
		c.putColumnValue(columnB.getName(), BINARY_B);
		
		ContentValues values = new ContentValues();
		assertNotNull(values);
		
		columnA.parseValueFrom(c, values);
		columnB.parseValueFrom(c, values);
		
		byte[] actual = null;
		
		actual = values.getAsByteArray(columnA.getName());
		Asserts.assertEquals(BINARY_A, actual);
		actual = values.getAsByteArray(columnB.getName());
		Asserts.assertEquals(BINARY_B, actual);
	}

	public void testConvertValueToString() {
		Column column = new BlobColumn("column");
		assertNotNull(column);

		String expected = null;
		String actual = null;
		
		expected = "X\'000102030405060708090A0B0C0D0E0F\'";
		actual = column.convertValueToString(BINARY_A);
		assertEquals(expected, actual);
		
		expected = "X\'FFFEFDFCFBFAF9F8F7F6F5F4F3F2F1F0\'";
		actual = column.convertValueToString(BINARY_B);
		assertEquals(expected, actual);

		actual = column.convertValueToString(BINARY_C);
		assertNull(actual);
	}

}
