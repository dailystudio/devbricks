package com.dailystudio.dataobject;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.dataobject.samples.SampleObject1;
import com.dailystudio.dataobject.samples.TestDatabaseObject;
import com.dailystudio.dataobject.samples.VersionControlledObject;
import com.dailystudio.test.ActivityTestCase;
import com.dailystudio.test.Asserts;

public class DatabaseObjectTest extends ActivityTestCase {

	private class TempDatabaseObject extends DatabaseObject {

		public TempDatabaseObject(Context context) {
			super(context);
		}
		
	}
	
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

	public void testSetAndGetIntegerValue() {
		DatabaseObject object = null;

		object = new DatabaseObject(mContext);
		assertNotNull(object);
		assertEquals(true, object.isEmpty());
		
		object = new TestDatabaseObject(mContext);
		assertNotNull(object);

		object.setValue((Column)null, 1111);
		assertEquals(true, object.isEmpty());
		
		object.setValue(new IntegerColumn("col_error"), 3333);
		assertEquals(true, object.isEmpty());

		object.setValue(TestDatabaseObject.COLUMN_INTEGER_VAL, 0x123456);
		assertEquals(0x123456, object.getIntegerValue(TestDatabaseObject.COLUMN_INTEGER_VAL));

		object.setValue(TestDatabaseObject.COLUMN_INTEGER_VAL.getName(), 0xfffff);
		assertEquals(0xfffff, object.getIntegerValue(TestDatabaseObject.COLUMN_INTEGER_VAL.getName()));
		
		assertEquals(0, object.getIntegerValue(TestDatabaseObject.COLUMN_LONG_VAL));
		assertEquals(0, object.getIntegerValue(TestDatabaseObject.COLUMN_DOUBLE_VAL));
		assertEquals(0, object.getIntegerValue(TestDatabaseObject.COLUMN_TEXT_VAL));
		assertEquals(0, object.getIntegerValue(TestDatabaseObject.COLUMN_BLOB_VAL));
		
		assertEquals(0, object.getIntegerValue(new IntegerColumn("dummy")));
	}
	
	public void testSetAndGetLongValue() {
		DatabaseObject object = null;

		object = new DatabaseObject(mContext);
		assertNotNull(object);
		assertEquals(true, object.isEmpty());
		
		object = new TestDatabaseObject(mContext);
		assertNotNull(object);
		
		object.setValue((Column)null, 1l);
		assertEquals(true, object.isEmpty());
		
		object.setValue(new LongColumn("col_error"), 3l);
		assertEquals(true, object.isEmpty());

		object.setValue(TestDatabaseObject.COLUMN_LONG_VAL, 123456789l);
		assertEquals(123456789l, object.getLongValue(TestDatabaseObject.COLUMN_LONG_VAL));

		object.setValue(TestDatabaseObject.COLUMN_LONG_VAL.getName(), 123454321l);
		assertEquals(123454321l, object.getLongValue(TestDatabaseObject.COLUMN_LONG_VAL.getName()));
		
		assertEquals(0l, object.getLongValue(TestDatabaseObject.COLUMN_INTEGER_VAL));
		assertEquals(0l, object.getLongValue(TestDatabaseObject.COLUMN_DOUBLE_VAL));
		assertEquals(0l, object.getLongValue(TestDatabaseObject.COLUMN_TEXT_VAL));
		assertEquals(0l, object.getLongValue(TestDatabaseObject.COLUMN_BLOB_VAL));
		
		assertEquals(0l, object.getLongValue(new LongColumn("dummy")));
	}
	
	public void testSetAndGetDoubleValue() {
		DatabaseObject object = null;

		object = new DatabaseObject(mContext);
		assertNotNull(object);
		assertEquals(true, object.isEmpty());
		
		object = new TestDatabaseObject(mContext);
		assertNotNull(object);
		
		object.setValue((Column)null, 1.1);
		assertEquals(true, object.isEmpty());
		
		object.setValue(new DoubleColumn("col_error"), 3.3);
		assertEquals(true, object.isEmpty());

		object.setValue(TestDatabaseObject.COLUMN_DOUBLE_VAL, 1.414);
		assertEquals(1.414, object.getDoubleValue(TestDatabaseObject.COLUMN_DOUBLE_VAL));
		
		object.setValue(TestDatabaseObject.COLUMN_DOUBLE_VAL.getName(), 1.732);
		assertEquals(1.732, object.getDoubleValue(TestDatabaseObject.COLUMN_DOUBLE_VAL.getName()));

		assertEquals(.0, object.getDoubleValue(TestDatabaseObject.COLUMN_INTEGER_VAL));
		assertEquals(.0, object.getDoubleValue(TestDatabaseObject.COLUMN_LONG_VAL));
		assertEquals(.0, object.getDoubleValue(TestDatabaseObject.COLUMN_TEXT_VAL));
		assertEquals(.0, object.getDoubleValue(TestDatabaseObject.COLUMN_BLOB_VAL));
		
		assertEquals(.0, object.getDoubleValue(new DoubleColumn("dummy")));
	}
	
	public void testSetAndGetStringValue() {
		DatabaseObject object = null;

		object = new DatabaseObject(mContext);
		assertNotNull(object);
		assertEquals(true, object.isEmpty());
		
		object = new TestDatabaseObject(mContext);
		assertNotNull(object);
		
		object.setValue((Column)null, "text1");
		assertEquals(true, object.isEmpty());
		
		object.setValue(new TextColumn("col_error"), "text3");
		assertEquals(true, object.isEmpty());

		object.setValue(TestDatabaseObject.COLUMN_TEXT_VAL, "test");
		assertEquals("test", object.getTextValue(TestDatabaseObject.COLUMN_TEXT_VAL));

		object.setValue(TestDatabaseObject.COLUMN_TEXT_VAL.getName(), "abcd");
		assertEquals("abcd", object.getTextValue(TestDatabaseObject.COLUMN_TEXT_VAL.getName()));
		
		assertNull(object.getTextValue(TestDatabaseObject.COLUMN_INTEGER_VAL));
		assertNull(object.getTextValue(TestDatabaseObject.COLUMN_LONG_VAL));
		assertNull(object.getTextValue(TestDatabaseObject.COLUMN_DOUBLE_VAL));
		assertNull(object.getTextValue(TestDatabaseObject.COLUMN_BLOB_VAL));
		
		assertNull(object.getTextValue(new TextColumn("dummy")));
	}
	
	public void testSetAndGetBlobValue() {
		DatabaseObject object = null;
		
		final byte[] BINARY_A = {
			0x00, 0x01, 0x02, 0x03,
			0x04, 0x05, 0x06, 0x07,
			0x08, 0x09, 0x0A, 0x0B,
			0x0C, 0x0D, 0x0E, 0x0F,
		};
		
		final byte[] BINARY_B = {
			(byte)0xFF, (byte)0xFE, (byte)0xFD, (byte)0xFC,
			(byte)0xFB, (byte)0xFA, (byte)0xF9, (byte)0xF8,
			(byte)0xF7, (byte)0xF6, (byte)0xF5, (byte)0xF4,
			(byte)0xF3, (byte)0xF2, (byte)0xF1, (byte)0xF0,
		};
		
		final byte[] BINARY_C = {};

		object = new DatabaseObject(mContext);
		assertNotNull(object);
		assertEquals(true, object.isEmpty());
		
		object = new TestDatabaseObject(mContext);
		assertNotNull(object);
		
		object.setValue((Column)null, BINARY_A);
		assertEquals(true, object.isEmpty());
		
		object.setValue(new TextColumn("col_error"), BINARY_A);
		assertEquals(true, object.isEmpty());

		object.setValue(TestDatabaseObject.COLUMN_BLOB_VAL, BINARY_A);
		Asserts.assertEquals(BINARY_A, 
				object.getBlobValue(TestDatabaseObject.COLUMN_BLOB_VAL));

		object.setValue(TestDatabaseObject.COLUMN_BLOB_VAL, BINARY_C);
		Asserts.assertEquals(BINARY_C, 
				object.getBlobValue(TestDatabaseObject.COLUMN_BLOB_VAL));

		object.setValue(TestDatabaseObject.COLUMN_BLOB_VAL, BINARY_B);
		Asserts.assertEquals(BINARY_B, 
				object.getBlobValue(TestDatabaseObject.COLUMN_BLOB_VAL));
		
		assertNull(object.getBlobValue(TestDatabaseObject.COLUMN_INTEGER_VAL));
		assertNull(object.getBlobValue(TestDatabaseObject.COLUMN_LONG_VAL));
		assertNull(object.getBlobValue(TestDatabaseObject.COLUMN_DOUBLE_VAL));
		assertNull(object.getBlobValue(TestDatabaseObject.COLUMN_TEXT_VAL));
		
		assertNull(object.getBlobValue(new BlobColumn("dummy")));
	}
	
	public void testIsEmpty() {
		DatabaseObject object = null;

		object = new DatabaseObject(mContext);
		assertNotNull(object);
		assertEquals(true, object.isEmpty());
		
		object = new TestDatabaseObject(mContext);
		assertNotNull(object);
		
		object.setValue(TestDatabaseObject.COLUMN_TEXT_VAL, "test");
		assertEquals(false, object.isEmpty());
	}
	
	public void testListNonEmptyColumns() {
		DatabaseObject object = new TestDatabaseObject(mContext);
		assertNotNull(object);
		
		object.setValue(TestDatabaseObject.COLUMN_INTEGER_VAL, 888);
		object.setValue(TestDatabaseObject.COLUMN_LONG_VAL, 123456789l);

		List<Column> expected = new ArrayList<Column>();
		assertNotNull(expected);
		expected.add(TestDatabaseObject.COLUMN_INTEGER_VAL);
		expected.add(TestDatabaseObject.COLUMN_LONG_VAL);
		
		Asserts.assertEquals(expected, object.listNonEmptyColumns());
	}
	
	public void testToSQLSelectionString() {
		DatabaseObject object = new TestDatabaseObject(mContext);
		assertNotNull(object);
		
		object.setValue(TestDatabaseObject.COLUMN_INTEGER_VAL, 888);
		object.setValue(TestDatabaseObject.COLUMN_LONG_VAL, 123456789l);
		object.setValue(TestDatabaseObject.COLUMN_DOUBLE_VAL, 3.1415926);
		object.setValue(TestDatabaseObject.COLUMN_TEXT_VAL, "This is a text value");
		
		assertEquals("intVal = 888 AND longVal = 123456789 AND doubleVal = 3.1415926 AND textVal = \'This is a text value\'", 
				object.toSQLSelectionString());
	}
	
	public void testConvertToIntent() {
		DatabaseObject object = new TestDatabaseObject(mContext);
		assertNotNull(object);
		
		object.setValue(TestDatabaseObject.COLUMN_INTEGER_VAL, 888);
		object.setValue(TestDatabaseObject.COLUMN_LONG_VAL, 123456789l);
		object.setValue(TestDatabaseObject.COLUMN_DOUBLE_VAL, 3.1415926);
		object.setValue(TestDatabaseObject.COLUMN_TEXT_VAL, "This is a text value");
		
		Intent actual = object.convertToIntent();
		assertNotNull(actual);
		
		assertEquals(888, actual.getIntExtra("intVal", 0));
		assertEquals(123456789l, actual.getLongExtra("longVal", 0l));
		assertEquals(3.1415926, actual.getDoubleExtra("doubleVal", .0));
		assertEquals("This is a text value", actual.getStringExtra("textVal"));
	}
	
	
	public void testToSQLProjectionString() {
		Template templ1 = new Template();
		assertNotNull(templ1);
		
		Column col1 = new IntegerColumn("col1");
		Column col2 = new LongColumn("col2");
		Column col3 = new DoubleColumn("col3");
		Column col4 = new TextColumn("col4");
		assertNotNull(col1);
		assertNotNull(col2);
		assertNotNull(col3);
		assertNotNull(col4);
		
		templ1.addColumn(col1);
		templ1.addColumn(col2);
		templ1.addColumn(col3);
		templ1.addColumn(col4);

		String[] expected = {
				"col1",
				"col2",
				"col3",
				"col4",
		};
		
		DatabaseObject object = new DatabaseObject(mContext);
		assertNotNull(object);
		
		object.setTemplate(templ1);
		
		Asserts.assertEquals(expected, object.toSQLProjection());

		Template templ2 = new Template();
		assertNotNull(templ2);
		
		object.setTemplate(templ2);
		
		assertNull(object.toSQLProjection());
	}

	public void testToSQLTableCreationString() {
		DatabaseObject object = new DatabaseObject(mContext);
		assertNotNull(object);
		
		Template templ1 = new Template();
		assertNotNull(templ1);
		
		Column col1 = new IntegerColumn("col1");
		Column col2 = new LongColumn("col2");
		Column col3 = new DoubleColumn("col3");
		Column col4 = new TextColumn("col4");
		assertNotNull(col1);
		assertNotNull(col2);
		assertNotNull(col3);
		assertNotNull(col4);
		
		templ1.addColumn(col1);
		templ1.addColumn(col2);
		templ1.addColumn(col3);
		templ1.addColumn(col4);

		object.setTemplate(templ1);
		
		String expected = 
			"CREATE TABLE IF NOT EXISTS DatabaseObject ( col1 INTEGER, col2 LONG, col3 DOUBLE, col4 TEXT );";
		assertEquals(expected, object.toSQLTableCreationString());

		Template templ2 = new Template();
		assertNotNull(templ2);
		
		object.setTemplate(templ2);
		
		assertNull(object.toSQLTableCreationString());
	}
	
	public void testClassToTable() {
		String expected = null;
		
		expected = "DatabaseObject";
		assertEquals(expected, DatabaseObject.classToTable((DatabaseObject.class)));

		expected = "SampleObject1";
		assertEquals(expected, DatabaseObject.classToTable((SampleObject1.class)));
		
		expected = "DatabaseObjectTest_TempDatabaseObject";
		assertEquals(expected, DatabaseObject.classToTable((TempDatabaseObject.class)));
	}
	
	public void testClassToDatabase() {
		String expected = null;
		
		expected = "com.dailystudio.dataobject.DatabaseObject.db";
		assertEquals(expected, DatabaseObject.classToDatabase((DatabaseObject.class)));

		expected = "com.dailystudio.dataobject.samples.SampleObject1.db";
		assertEquals(expected, DatabaseObject.classToDatabase((SampleObject1.class)));
		
		expected = "com.dailystudio.dataobject.DatabaseObjectTest_TempDatabaseObject.db";
		assertEquals(expected, DatabaseObject.classToDatabase((TempDatabaseObject.class)));
	}
	
	public void testVersionRelatedFunctions() {
		DatabaseObject object = null;
		Intent actual = null;
		String expected = null;
		
		object = new VersionControlledObject(mContext, DatabaseObject.VERSION_START);
		assertNotNull(object);
		
		assertEquals(DatabaseObject.VERSION_START, object.getVersion());
		
		object.setValue(VersionControlledObject.COLUMN_INT_VER1, 1982);
		object.setValue(VersionControlledObject.COLUMN_INT_VER2, 1983);
		object.setValue(VersionControlledObject.COLUMN_LONG_VER1, 12345678987654321l);
		object.setValue(VersionControlledObject.COLUMN_DOUBLE_VER2, 0.414);
		object.setValue(VersionControlledObject.COLUMN_TEXT_VER2, "Text string");
		assertEquals(1982, object.getIntegerValue(VersionControlledObject.COLUMN_INT_VER1));
		assertEquals(0, object.getIntegerValue(VersionControlledObject.COLUMN_INT_VER2));
		assertEquals(12345678987654321l, object.getLongValue(VersionControlledObject.COLUMN_LONG_VER1));
		assertEquals(.0, object.getDoubleValue(VersionControlledObject.COLUMN_DOUBLE_VER2));
		assertNull(object.getTextValue(VersionControlledObject.COLUMN_TEXT_VER2));
		
		object.setValue(VersionControlledObject.COLUMN_INT_VER1.getName(), 1982);
		object.setValue(VersionControlledObject.COLUMN_INT_VER2.getName(), 1983);
		object.setValue(VersionControlledObject.COLUMN_LONG_VER1.getName(), 12345678987654321l);
		object.setValue(VersionControlledObject.COLUMN_DOUBLE_VER2.getName(), 0.414);
		object.setValue(VersionControlledObject.COLUMN_TEXT_VER2.getName(), "Text string");
		assertEquals(1982, object.getIntegerValue(VersionControlledObject.COLUMN_INT_VER1.getName()));
		assertEquals(0, object.getIntegerValue(VersionControlledObject.COLUMN_INT_VER2.getName()));
		assertEquals(12345678987654321l, object.getLongValue(VersionControlledObject.COLUMN_LONG_VER1.getName()));
		assertEquals(.0, object.getDoubleValue(VersionControlledObject.COLUMN_DOUBLE_VER2.getName()));
		assertNull(object.getTextValue(VersionControlledObject.COLUMN_TEXT_VER2.getName()));
		
		assertEquals("int_ver1 = 1982 AND long_ver1 = 12345678987654321", 
				object.toSQLSelectionString());
		
		expected = 
			"CREATE TABLE IF NOT EXISTS VersionControlledObject ( int_ver1 INTEGER, long_ver1 LONG );";
		assertEquals(expected, object.toSQLTableCreationString());

		actual = object.convertToIntent();
		assertNotNull(actual);
		
		assertEquals(1982, actual.getIntExtra("int_ver1", 0));
		assertEquals(0, actual.getIntExtra("int_ver2", 0));
		assertEquals(12345678987654321l, actual.getLongExtra("long_ver1", 0l));
		assertEquals(.0, actual.getDoubleExtra("double_ver2", .0));
		assertNull(actual.getStringExtra("text_ver2"));
		
		object = new VersionControlledObject(mContext);
		assertNotNull(object);
		
		assertEquals(DatabaseObject.VERSION_START + 1, object.getVersion());
		
		object.setValue(VersionControlledObject.COLUMN_INT_VER1, 1982);
		object.setValue(VersionControlledObject.COLUMN_INT_VER2, 1983);
		object.setValue(VersionControlledObject.COLUMN_LONG_VER1, 12345678987654321l);
		object.setValue(VersionControlledObject.COLUMN_DOUBLE_VER2, 0.414);
		object.setValue(VersionControlledObject.COLUMN_TEXT_VER2, "Text string");
		assertEquals(1982, object.getIntegerValue(VersionControlledObject.COLUMN_INT_VER1));
		assertEquals(1983, object.getIntegerValue(VersionControlledObject.COLUMN_INT_VER2));
		assertEquals(12345678987654321l, object.getLongValue(VersionControlledObject.COLUMN_LONG_VER1));
		assertEquals(0.414, object.getDoubleValue(VersionControlledObject.COLUMN_DOUBLE_VER2));
		assertEquals("Text string", object.getTextValue(VersionControlledObject.COLUMN_TEXT_VER2));
		
		object.setValue(VersionControlledObject.COLUMN_INT_VER1.getName(), 1982);
		object.setValue(VersionControlledObject.COLUMN_INT_VER2.getName(), 1983);
		object.setValue(VersionControlledObject.COLUMN_LONG_VER1.getName(), 12345678987654321l);
		object.setValue(VersionControlledObject.COLUMN_DOUBLE_VER2.getName(), 0.414);
		object.setValue(VersionControlledObject.COLUMN_TEXT_VER2.getName(), "Text string");
		assertEquals(1982, object.getIntegerValue(VersionControlledObject.COLUMN_INT_VER1.getName()));
		assertEquals(1983, object.getIntegerValue(VersionControlledObject.COLUMN_INT_VER2.getName()));
		assertEquals(12345678987654321l, object.getLongValue(VersionControlledObject.COLUMN_LONG_VER1.getName()));
		assertEquals(0.414, object.getDoubleValue(VersionControlledObject.COLUMN_DOUBLE_VER2.getName()));
		assertEquals("Text string", object.getTextValue(VersionControlledObject.COLUMN_TEXT_VER2.getName()));
		
		assertEquals("int_ver1 = 1982 AND int_ver2 = 1983 AND long_ver1 = 12345678987654321 AND double_ver2 = 0.414 AND text_ver2 = \'Text string\'", 
				object.toSQLSelectionString());
		
		expected = 
			"CREATE TABLE IF NOT EXISTS VersionControlledObject ( int_ver1 INTEGER, int_ver2 INTEGER, long_ver1 LONG, double_ver2 DOUBLE, text_ver2 TEXT );";
		assertEquals(expected, object.toSQLTableCreationString());

		actual = object.convertToIntent();
		assertNotNull(actual);
		
		assertEquals(1982, actual.getIntExtra("int_ver1", 0));
		assertEquals(1983, actual.getIntExtra("int_ver2", 0));
		assertEquals(12345678987654321l, actual.getLongExtra("long_ver1", 0l));
		assertEquals(0.414, actual.getDoubleExtra("double_ver2", .0));
		assertEquals("Text string", actual.getStringExtra("text_ver2"));
	}

}
