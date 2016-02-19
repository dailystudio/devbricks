package com.dailystudio.dataobject;

import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.dataobject.samples.CopierTestObject1;

import android.test.AndroidTestCase;

public class DatabaseObjectCopierTest extends AndroidTestCase {
	
	private static class CopierTestObject1Copier extends DatabaseObjectCopier {
		
		@Override
		public void fillObject(DatabaseObject dstObject,
				DatabaseObject srcObject) {
			super.fillObject(dstObject, srcObject);
			
			if (dstObject.getVersion() == DatabaseObject.VERSION_START + 1
					&& srcObject.getVersion() == DatabaseObject.VERSION_START) {
				int intDiff = srcObject.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL2) -
					srcObject.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL1);
				dstObject.setValue(CopierTestObject1.COLUMN_INT_DIFF, intDiff);
				
				long longDiff = srcObject.getLongValue(CopierTestObject1.COLUMN_LONG_VAL2) -
					srcObject.getLongValue(CopierTestObject1.COLUMN_LONG_VAL1);
				dstObject.setValue(CopierTestObject1.COLUMN_LONG_DIFF, longDiff);
			
				double doubleDiff = srcObject.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL2) -
					srcObject.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL1);
				dstObject.setValue(CopierTestObject1.COLUMN_DOUBLE_DIFF, doubleDiff);
		
				int textDiff = srcObject.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL2).length() -
					srcObject.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL1).length();
				dstObject.setValue(CopierTestObject1.COLUMN_TEXT_DIFF, String.valueOf(textDiff));
			}
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
	
	public void testFillObjectOldToNew() {
		CopierTestObject1 object1 = new CopierTestObject1(mContext, DatabaseObject.VERSION_START);
		assertNotNull(object1);
		
		object1.setValue(CopierTestObject1.COLUMN_INT_VAL1, 123);
		object1.setValue(CopierTestObject1.COLUMN_INT_VAL2, 234);
		object1.setValue(CopierTestObject1.COLUMN_LONG_VAL1, 10000000000l);
		object1.setValue(CopierTestObject1.COLUMN_LONG_VAL2, 12345678900l);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_VAL1, 0.777);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_VAL2, 0.333);
		object1.setValue(CopierTestObject1.COLUMN_TEXT_VAL1, "This is first string");
		object1.setValue(CopierTestObject1.COLUMN_TEXT_VAL2, "This is second string");
		

		DatabaseObjectCopier copier = null; 
		
		CopierTestObject1 object2 = new CopierTestObject1(mContext);
		assertNotNull(object2);
		
		copier = new DatabaseObjectCopier();
		copier.fillObject(object2, object1);
		
		assertEquals(123, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL1));
		assertEquals(234, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL2));
		assertEquals(10000000000l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_VAL1));
		assertEquals(12345678900l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_VAL2));
		assertEquals(0.777, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL1));
		assertEquals(0.333, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL2));
		assertEquals("This is first string", object2.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL1));
		assertEquals("This is second string", object2.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL2));
		assertEquals(0, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_DIFF));
		assertEquals(0l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_DIFF));
		assertEquals(.0, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_DIFF));
		assertNull(object2.getTextValue(CopierTestObject1.COLUMN_TEXT_DIFF));
		
		CopierTestObject1 object3 = new CopierTestObject1(mContext);
		assertNotNull(object3);
		
		copier = new CopierTestObject1Copier();
		copier.fillObject(object3, object1);
		
		assertEquals(123, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL1));
		assertEquals(234, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL2));
		assertEquals(10000000000l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_VAL1));
		assertEquals(12345678900l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_VAL2));
		assertEquals(0.777, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL1));
		assertEquals(0.333, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL2));
		assertEquals("This is first string", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL1));
		assertEquals("This is second string", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL2));
		assertEquals(111, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_DIFF));
		assertEquals(2345678900l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_DIFF));
		assertEquals(-.444, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_DIFF));
		assertEquals("1", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_DIFF));
	}

	public void testFillObjectNewToOld() {
		CopierTestObject1 object1 = new CopierTestObject1(mContext);
		assertNotNull(object1);
		
		object1.setValue(CopierTestObject1.COLUMN_INT_VAL1, 123);
		object1.setValue(CopierTestObject1.COLUMN_INT_VAL2, 234);
		object1.setValue(CopierTestObject1.COLUMN_LONG_VAL1, 10000000000l);
		object1.setValue(CopierTestObject1.COLUMN_LONG_VAL2, 12345678900l);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_VAL1, 0.777);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_VAL2, 0.333);
		object1.setValue(CopierTestObject1.COLUMN_TEXT_VAL1, "This is first string");
		object1.setValue(CopierTestObject1.COLUMN_TEXT_VAL2, "This is second string");
		object1.setValue(CopierTestObject1.COLUMN_INT_DIFF, 111);
		object1.setValue(CopierTestObject1.COLUMN_LONG_DIFF, 2345678900l);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_DIFF, -.444);
		object1.setValue(CopierTestObject1.COLUMN_TEXT_DIFF, "1");

		DatabaseObjectCopier copier = null; 
		
		CopierTestObject1 object2 = new CopierTestObject1(mContext, DatabaseObject.VERSION_START);
		assertNotNull(object2);
		
		copier = new DatabaseObjectCopier();
		copier.fillObject(object2, object1);
		
		assertEquals(123, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL1));
		assertEquals(234, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL2));
		assertEquals(10000000000l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_VAL1));
		assertEquals(12345678900l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_VAL2));
		assertEquals(0.777, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL1));
		assertEquals(0.333, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL2));
		assertEquals("This is first string", object2.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL1));
		assertEquals("This is second string", object2.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL2));
		
		CopierTestObject1 object3 = new CopierTestObject1(mContext);
		assertNotNull(object3);
		
		copier = new CopierTestObject1Copier();
		copier.fillObject(object3, object1);
		
		assertEquals(123, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL1));
		assertEquals(234, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL2));
		assertEquals(10000000000l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_VAL1));
		assertEquals(12345678900l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_VAL2));
		assertEquals(0.777, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL1));
		assertEquals(0.333, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL2));
		assertEquals("This is first string", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL1));
		assertEquals("This is second string", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL2));
	}
	
	public void testColneObjectOldToNew() {
		CopierTestObject1 object1 = new CopierTestObject1(mContext, DatabaseObject.VERSION_START);
		assertNotNull(object1);
		
		object1.setValue(CopierTestObject1.COLUMN_INT_VAL1, 123);
		object1.setValue(CopierTestObject1.COLUMN_INT_VAL2, 234);
		object1.setValue(CopierTestObject1.COLUMN_LONG_VAL1, 10000000000l);
		object1.setValue(CopierTestObject1.COLUMN_LONG_VAL2, 12345678900l);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_VAL1, 0.777);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_VAL2, 0.333);
		object1.setValue(CopierTestObject1.COLUMN_TEXT_VAL1, "This is first string");
		object1.setValue(CopierTestObject1.COLUMN_TEXT_VAL2, "This is second string");
		
		DatabaseObjectCopier copier = null; 
		
		copier = new DatabaseObjectCopier();

		DatabaseObject object2 = copier.cloneObject(object1, CopierTestObject1.class);
		assertNotNull(object2);
	
		assertEquals(123, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL1));
		assertEquals(234, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL2));
		assertEquals(10000000000l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_VAL1));
		assertEquals(12345678900l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_VAL2));
		assertEquals(0.777, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL1));
		assertEquals(0.333, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL2));
		assertEquals("This is first string", object2.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL1));
		assertEquals("This is second string", object2.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL2));
		assertEquals(0, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_DIFF));
		assertEquals(0l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_DIFF));
		assertEquals(.0, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_DIFF));
		assertNull(object2.getTextValue(CopierTestObject1.COLUMN_TEXT_DIFF));
		
		copier = new CopierTestObject1Copier();

		DatabaseObject object3 = copier.cloneObject(object1, CopierTestObject1.class);
		assertNotNull(object3);
		
		assertEquals(123, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL1));
		assertEquals(234, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL2));
		assertEquals(10000000000l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_VAL1));
		assertEquals(12345678900l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_VAL2));
		assertEquals(0.777, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL1));
		assertEquals(0.333, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL2));
		assertEquals("This is first string", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL1));
		assertEquals("This is second string", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL2));
		assertEquals(111, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_DIFF));
		assertEquals(2345678900l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_DIFF));
		assertEquals(-.444, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_DIFF));
		assertEquals("1", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_DIFF));
	}

	public void testCloneObjectNewToOld() {
		CopierTestObject1 object1 = new CopierTestObject1(mContext);
		assertNotNull(object1);
		
		object1.setValue(CopierTestObject1.COLUMN_INT_VAL1, 123);
		object1.setValue(CopierTestObject1.COLUMN_INT_VAL2, 234);
		object1.setValue(CopierTestObject1.COLUMN_LONG_VAL1, 10000000000l);
		object1.setValue(CopierTestObject1.COLUMN_LONG_VAL2, 12345678900l);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_VAL1, 0.777);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_VAL2, 0.333);
		object1.setValue(CopierTestObject1.COLUMN_TEXT_VAL1, "This is first string");
		object1.setValue(CopierTestObject1.COLUMN_TEXT_VAL2, "This is second string");
		object1.setValue(CopierTestObject1.COLUMN_INT_DIFF, 111);
		object1.setValue(CopierTestObject1.COLUMN_LONG_DIFF, 2345678900l);
		object1.setValue(CopierTestObject1.COLUMN_DOUBLE_DIFF, -.444);
		object1.setValue(CopierTestObject1.COLUMN_TEXT_DIFF, "1");

		DatabaseObjectCopier copier = null; 
		
		copier = new DatabaseObjectCopier();

		DatabaseObject object2 = copier.cloneObject(object1, CopierTestObject1.class);
		assertNotNull(object2);
		
		
		assertEquals(123, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL1));
		assertEquals(234, object2.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL2));
		assertEquals(10000000000l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_VAL1));
		assertEquals(12345678900l, object2.getLongValue(CopierTestObject1.COLUMN_LONG_VAL2));
		assertEquals(0.777, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL1));
		assertEquals(0.333, object2.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL2));
		assertEquals("This is first string", object2.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL1));
		assertEquals("This is second string", object2.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL2));
		
		copier = new CopierTestObject1Copier();

		DatabaseObject object3 = copier.cloneObject(object1, CopierTestObject1.class);
		assertNotNull(object3);
		
		assertEquals(123, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL1));
		assertEquals(234, object3.getIntegerValue(CopierTestObject1.COLUMN_INT_VAL2));
		assertEquals(10000000000l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_VAL1));
		assertEquals(12345678900l, object3.getLongValue(CopierTestObject1.COLUMN_LONG_VAL2));
		assertEquals(0.777, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL1));
		assertEquals(0.333, object3.getDoubleValue(CopierTestObject1.COLUMN_DOUBLE_VAL2));
		assertEquals("This is first string", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL1));
		assertEquals("This is second string", object3.getTextValue(CopierTestObject1.COLUMN_TEXT_VAL2));
	}

}
