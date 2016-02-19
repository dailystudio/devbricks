package com.dailystudio.dataobject;

import java.util.ArrayList;
import java.util.HashMap;

import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.dataobject.samples.TestDatabaseObject;
import com.dailystudio.dataobject.samples.VersionControlledObject;
import com.dailystudio.test.ActivityTestCase;

import android.content.Intent;
import android.test.mock.MockCursor;

public class DatabaseObjectFactoryTest extends ActivityTestCase {

	private static class MockData {
		int mIntVal;
		long mLongVal;
		double mDoubleVal;
		String mStringVal;
	}
	
	public static class SimpleMockCursor extends MockCursor {
		
		public static final String COL_INT_VAL = "intVal";
		public static final String COL_LONG_VAL = "longVal";
		public static final String COL_DOUBLE_VAL = "doubleVal";
		public static final String COL_TEXT_VAL = "textVal";
		
		private static final int IND_INT_VAL = 0;
		private static final int IND_LONG_VAL = 1;
		private static final int IND_DOUBLE_VAL = 2;
		private static final int IND_TEXT_VAL = 3;
		
		private static final String[] mColumnNames = {
			COL_INT_VAL,
			COL_LONG_VAL,
			COL_DOUBLE_VAL,
			COL_TEXT_VAL,
		};
		
		private static final int COUNT = 10;
		private static HashMap<String, Integer> mNameToIndexMap = new HashMap<String, Integer>();
		private static ArrayList<MockData> mData = new ArrayList<MockData>();
		
		static {
			mNameToIndexMap.put(COL_INT_VAL, IND_INT_VAL);
			mNameToIndexMap.put(COL_LONG_VAL, IND_LONG_VAL);
			mNameToIndexMap.put(COL_DOUBLE_VAL, IND_DOUBLE_VAL);
			mNameToIndexMap.put(COL_TEXT_VAL, IND_TEXT_VAL);
			
			MockData data = null;
			for (int i = 0; i < COUNT; i++) {
				data = new MockData();
				
				data.mIntVal = i;
				data.mLongVal = i * 1000l;
				data.mDoubleVal = i * 0.0001;
				data.mStringVal = String.format("Text%03d", i);
				
				mData.add(data);
			}
		}
		
		private int mIndex = 0;
		
		@Override
		public boolean moveToFirst() {
			mIndex = 0;
			
			return true;
		}
		
		@Override
		public boolean moveToLast() {
			mIndex = (COUNT - 1);
			return true;
		}
		
		@Override
		public boolean moveToNext() {
			if (mIndex == (COUNT - 1)) {
				return false;
			}
			
			mIndex++;
			
			return true;
		}
		
		@Override
		public boolean moveToPrevious() {
			if (mIndex == 0) {
				return false;
			}
			
			mIndex--;
			
			return true;
		}
		
		@Override
		public int getCount() {
			return COUNT;
		};
		
		@Override
		public int getColumnCount() {
			return 4;
		}
		
		@Override
		public boolean isNull(int columnIndex) {
			return (getColumnName(columnIndex) == null);
		}
		
		@Override
		public int getColumnIndex(String columnName) {
			Integer index = mNameToIndexMap.get(columnName);
			if (index == null) {
				return -1;
			}
			
			return index.intValue();
		}
		
		@Override
		public int getColumnIndexOrThrow(String columnName) {
			Integer index = mNameToIndexMap.get(columnName);
			if (index == null) {
				throw new IllegalArgumentException(
						String.format("no such column %s", columnName));
			}
			
			return index.intValue();
		}
		
		@Override
		public int getPosition() {
			return mIndex;
		}
		
		@Override
		public String getColumnName(int columnIndex) {
			if (columnIndex < 0 || columnIndex >= mColumnNames.length) {
				return null;
			}
			
			return mColumnNames[columnIndex];
		}
		
		@Override
		public int getInt(int columnIndex) {
			if (columnIndex != getColumnIndex(COL_INT_VAL)) {
				return -1;
			}
			
			if (mIndex < 0 || mIndex >= mData.size()) {
				return -1;
			}
			
			MockData data = mData.get(mIndex);
			if (data == null) {
				return -1;
			}
			
			return data.mIntVal;
		}

		@Override
		public long getLong(int columnIndex) {
			if (columnIndex != getColumnIndex(COL_LONG_VAL)) {
				return -1l;
			}
			
			if (mIndex < 0 || mIndex >= mData.size()) {
				return -1l;
			}
			
			MockData data = mData.get(mIndex);
			if (data == null) {
				return -1l;
			}
			
			return data.mLongVal;
		}

		@Override
		public double getDouble(int columnIndex) {
			if (columnIndex != getColumnIndex(COL_DOUBLE_VAL)) {
				return 0.f;
			}
			
			if (mIndex < 0 || mIndex >= mData.size()) {
				return 0.f;
			}
			
			MockData data = mData.get(mIndex);
			if (data == null) {
				return 0.f;
			}
			
			return data.mDoubleVal;
		}
		
		@Override
		public String getString(int columnIndex) {
			if (columnIndex != getColumnIndex(COL_TEXT_VAL)) {
				return null;
			}
			
			if (mIndex < 0 || mIndex >= mData.size()) {
				return null;
			}
			
			MockData data = mData.get(mIndex);
			if (data == null) {
				return null;
			}
			
			return data.mStringVal;
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
	
	public void testCreateDefaultDatabaseObject() {
		DatabaseObjectFactory factory = DatabaseObjectFactory.getInstance();
		assertNotNull(factory);
		
		DatabaseObject object1 = null;
		DatabaseObject object2 = null;
		
		object1 = factory.createObject(new DatabaseObjectCreationParams(null));
		assertNotNull(object1);
		assertEquals(true, object1 instanceof DatabaseObject);

		object2 = factory.createObject(new DatabaseObjectCreationParams(TestDatabaseObject.class));
		assertNotNull(object2);
		assertEquals(false, (object1 == object2));
		assertEquals(true, object2 instanceof TestDatabaseObject);
	}
	
	public void testCreateDatabaseObject() {
		Template templ = new Template();
		assertNotNull(templ);
		
		templ.addColumn(TestDatabaseObject.COLUMN_INTEGER_VAL);
		templ.addColumn(TestDatabaseObject.COLUMN_LONG_VAL);
		templ.addColumn(TestDatabaseObject.COLUMN_DOUBLE_VAL);
		templ.addColumn(TestDatabaseObject.COLUMN_TEXT_VAL);
		templ.addColumn(TestDatabaseObject.COLUMN_BLOB_VAL);

		DatabaseObject object = null;

		object = DatabaseObjectFactory.createDatabaseObject(TestDatabaseObject.class);
		assertNotNull(object);
		assertEquals(true, object instanceof TestDatabaseObject);
		assertEquals(templ, object.getTemplate());
		
		MockCursor cursor = new SimpleMockCursor();
		assertNotNull(cursor);
		
		assertEquals(true, cursor.moveToFirst());
		
		int i = 0;
		do {
			object = DatabaseObjectFactory.createDatabaseObject(TestDatabaseObject.class);
			assertNotNull(object);
			
			object.fillValuesFromCursor(cursor);
			
			assertEquals(true, object instanceof TestDatabaseObject);
			assertEquals(templ, object.getTemplate());
			assertEquals(i, object.getIntegerValue(TestDatabaseObject.COLUMN_INTEGER_VAL));
			assertEquals(i * 1000l, object.getLongValue(TestDatabaseObject.COLUMN_LONG_VAL));
			assertEquals(i * 0.0001, object.getDoubleValue(TestDatabaseObject.COLUMN_DOUBLE_VAL));
			assertEquals(String.format("Text%03d", i), object.getTextValue(TestDatabaseObject.COLUMN_TEXT_VAL));
			
			i++;
		} while (cursor.moveToNext());
	}
	
	
	public void testVersionRelatedFunctions() {
		DatabaseObject object = null;
		Intent actual = null;
		String expected = null;
		
		object = DatabaseObjectFactory.createDatabaseObject(VersionControlledObject.class, DatabaseObject.VERSION_START);
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
		
		object = DatabaseObjectFactory.createDatabaseObject(VersionControlledObject.class);
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
