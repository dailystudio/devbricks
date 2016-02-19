package com.dailystudio.dataobject.database;

import android.test.MoreAsserts;

import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.test.ActivityTestCase;

public class CommandCursorTest extends ActivityTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		GlobalContextWrapper.bindContext(mContext);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		GlobalContextWrapper.unbindContext(mContext);
	}

	public void testGetCommand() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		assertEquals("cmd_test_1", cursor.getCommand());
	}
	
	public void testGetCount() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		assertEquals(1, cursor.getCount());
	}
	
	public void testAddColumns() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		cursor.addColumn("colName1");
		cursor.addColumn("colName2");
		cursor.addColumn("colName3");
		cursor.addColumn("colName4");
		cursor.addColumn("colName5");
		cursor.addColumn("colName6");
		
		String [] expected1 = {
			"colName1",	
			"colName2",	
			"colName3",	
			"colName4",	
			"colName5",	
			"colName6",	
		};
		
		MoreAsserts.assertEquals(expected1, cursor.getColumnNames());
		
		String [] newColumns = {
				"newColName1",	
				"newColName2",	
				"newColName3",	
		};
		
		cursor.addColumns(newColumns);
		
		String [] expected2 = {
				"colName1",	
				"colName2",	
				"colName3",	
				"colName4",	
				"colName5",	
				"colName6",	
				"newColName1",	
				"newColName2",	
				"newColName3",	
		};
			
		MoreAsserts.assertEquals(expected2, cursor.getColumnNames());
	}
	
	public void testRemoveColumns() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		cursor.addColumn("colName1");
		cursor.addColumn("colName2");
		cursor.addColumn("colName3");
		cursor.addColumn("colName4");
		cursor.addColumn("colName5");
		cursor.addColumn("colName6");
		
		String [] expected1 = {
			"colName1",	
			"colName2",	
			"colName3",	
			"colName4",	
			"colName5",	
			"colName6",	
		};
		
		MoreAsserts.assertEquals(expected1, cursor.getColumnNames());
		
		cursor.removeColumn("colName3");
		cursor.removeColumn("colName5");
		
		String [] expected2 = {
				"colName1",	
				"colName2",	
				"colName4",	
				"colName6",	
			};
			
		MoreAsserts.assertEquals(expected2, cursor.getColumnNames());
	}
	
	public void testGetColumns() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		cursor.addColumn("colName1");
		cursor.addColumn("colName2");
		cursor.addColumn("colName3");
		cursor.addColumn("colName4");
		cursor.addColumn("colName5");
		cursor.addColumn("colName6");
		
		String [] expected1 = {
			"colName1",	
			"colName2",	
			"colName3",	
			"colName4",	
			"colName5",	
			"colName6",	
		};
		
		MoreAsserts.assertEquals(expected1, cursor.getColumnNames());
		
		cursor.removeColumn("colName3");
		cursor.removeColumn("colName5");
		
		String [] expected2 = {
			"colName1",	
			"colName2",	
			"colName4",	
			"colName6",	
		};
		
		MoreAsserts.assertEquals(expected2, cursor.getColumnNames());
	}
	
	public void testGetColumnCount() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		cursor.addColumn("colName1");
		cursor.addColumn("colName2");
		cursor.addColumn("colName3");
		cursor.addColumn("colName4");
		cursor.addColumn("colName5");
		cursor.addColumn("colName6");
		
		assertEquals(6, cursor.getColumnCount());
		
		cursor.removeColumn("colName3");
		cursor.removeColumn("colName5");
		
		assertEquals(4, cursor.getColumnCount());
	}
	
	public void testGetColumnIndex() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		cursor.addColumn("colName1");
		cursor.addColumn("colName2");
		cursor.addColumn("colName3");
		cursor.addColumn("colName4");
		cursor.addColumn("colName5");
		cursor.addColumn("colName6");
		
		assertEquals(0, cursor.getColumnIndex("colName1"));
		assertEquals(1, cursor.getColumnIndex("colName2"));
		assertEquals(2, cursor.getColumnIndex("colName3"));
		assertEquals(3, cursor.getColumnIndex("colName4"));
		assertEquals(4, cursor.getColumnIndex("colName5"));
		assertEquals(5, cursor.getColumnIndex("colName6"));
		assertEquals(-1, cursor.getColumnIndex("abc"));
		
		assertEquals(0, cursor.getColumnIndexOrThrow("colName1"));
		assertEquals(1, cursor.getColumnIndexOrThrow("colName2"));
		assertEquals(2, cursor.getColumnIndexOrThrow("colName3"));
		assertEquals(3, cursor.getColumnIndexOrThrow("colName4"));
		assertEquals(4, cursor.getColumnIndexOrThrow("colName5"));
		assertEquals(5, cursor.getColumnIndexOrThrow("colName6"));
		
		boolean catched = false;
		try {
			assertEquals(-1, cursor.getColumnIndexOrThrow("abc"));
			catched = false;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			
			catched = true;
		}
		assertTrue(catched);
		
		cursor.removeColumn("colName3");
		cursor.removeColumn("colName5");

		assertEquals(0, cursor.getColumnIndex("colName1"));
		assertEquals(1, cursor.getColumnIndex("colName2"));
		assertEquals(-1, cursor.getColumnIndex("colName3"));
		assertEquals(2, cursor.getColumnIndex("colName4"));
		assertEquals(-1, cursor.getColumnIndex("colName5"));
		assertEquals(3, cursor.getColumnIndex("colName6"));
		assertEquals(-1, cursor.getColumnIndex("abc"));
		
		assertEquals(0, cursor.getColumnIndexOrThrow("colName1"));
		assertEquals(1, cursor.getColumnIndexOrThrow("colName2"));
		assertEquals(2, cursor.getColumnIndexOrThrow("colName4"));
		assertEquals(3, cursor.getColumnIndexOrThrow("colName6"));
		
		try {
			assertEquals(-1, cursor.getColumnIndexOrThrow("colName3"));
			catched = false;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			
			catched = true;
		}
		assertTrue(catched);

		try {
			assertEquals(-1, cursor.getColumnIndexOrThrow("colName5"));
			catched = false;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			
			catched = true;
		}
		assertTrue(catched);

		try {
			assertEquals(-1, cursor.getColumnIndexOrThrow("abc"));
			catched = false;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			
			catched = true;
		}
		assertTrue(catched);

	}
	
	public void testGetColumnName() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		cursor.addColumn("colName1");
		cursor.addColumn("colName2");
		cursor.addColumn("colName3");
		cursor.addColumn("colName4");
		cursor.addColumn("colName5");
		cursor.addColumn("colName6");
		
		assertEquals("colName1", cursor.getColumnName(0));
		assertEquals("colName2", cursor.getColumnName(1));
		assertEquals("colName3", cursor.getColumnName(2));
		assertEquals("colName4", cursor.getColumnName(3));
		assertEquals("colName5", cursor.getColumnName(4));
		assertEquals("colName6", cursor.getColumnName(5));
		assertNull(cursor.getColumnName(6));
		assertNull(cursor.getColumnName(-1));
		
		cursor.removeColumn("colName3");
		cursor.removeColumn("colName5");

		assertEquals("colName1", cursor.getColumnName(0));
		assertEquals("colName2", cursor.getColumnName(1));
		assertEquals("colName4", cursor.getColumnName(2));
		assertEquals("colName6", cursor.getColumnName(3));
		assertNull(cursor.getColumnName(6));
		assertNull(cursor.getColumnName(-1));
	}
	
	public void testPutAndGetValue() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		cursor.addColumn("colName1");
		cursor.addColumn("colName2");
		cursor.addColumn("colName3");

		cursor.putValue("colName1", 0.1314);
		cursor.putValue("colName2", 0.414f);
		cursor.putValue("colName3", 123);
		cursor.putValue("colName4", 12345678900l);
		cursor.putValue("colName5", 8888);
		cursor.putValue("colName6", "Text");
		
		assertEquals(6, cursor.getColumnCount());
		
		assertEquals(0.1314, cursor.getDouble(0));
		assertEquals(0.414f, cursor.getFloat(1));
		assertEquals(123, cursor.getInt(2));
		assertEquals(12345678900l, cursor.getLong(3));
		assertEquals(8888, cursor.getShort(4));
		assertEquals("Text", cursor.getString(5));
		
		cursor.removeColumn("colName1");
		cursor.removeColumn("colName2");
		cursor.removeColumn("colName3");
		
		assertEquals(3, cursor.getColumnCount());
		
		assertEquals(12345678900l, cursor.getLong(0));
		assertEquals(8888, cursor.getShort(1));
		assertEquals("Text", cursor.getString(2));
	}
	
	public void testClearValue() {
		CommandCursor cursor = new CommandCursor("cmd_test_1");
		assertNotNull(cursor);
		
		cursor.putValue("colName1", 0.1314);
		cursor.putValue("colName2", 0.414f);
		cursor.putValue("colName3", 123);
		cursor.putValue("colName4", 12345678900l);
		cursor.putValue("colName5", 8888);
		cursor.putValue("colName6", "Text");
		
		assertEquals(6, cursor.getColumnCount());
		
		assertEquals(0.1314, cursor.getDouble(0));
		assertEquals(0.414f, cursor.getFloat(1));
		assertEquals(123, cursor.getInt(2));
		assertEquals(12345678900l, cursor.getLong(3));
		assertEquals(8888, cursor.getShort(4));
		assertEquals("Text", cursor.getString(5));
		
		cursor.clearValue(0);
		cursor.clearValue(2);
		cursor.clearValue(4);
		
		assertEquals(6, cursor.getColumnCount());
		
		assertEquals(0.0, cursor.getDouble(0));
		assertEquals(0.414f, cursor.getFloat(1));
		assertEquals(0, cursor.getInt(2));
		assertEquals(12345678900l, cursor.getLong(3));
		assertEquals(0, cursor.getShort(4));
		assertEquals("Text", cursor.getString(5));
	}
	
}
