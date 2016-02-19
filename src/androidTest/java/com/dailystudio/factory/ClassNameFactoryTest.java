package com.dailystudio.factory;

import android.content.Context;

import com.dailystudio.factory.Factory;
import com.dailystudio.test.ActivityTestCase;

public class ClassNameFactoryTest extends ActivityTestCase {
	
	static class TestObject extends Object {
	}
	
	static class TestContextObject extends TestObject {
		
		protected Context mContext;
		
		public TestContextObject(Context context) {
			mContext = context;
		}
		
		public Context getContext() {
			return mContext;
		}
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Factory.bindContext(mContext);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		Factory.unbindContext(mContext);
	}
	
	public void testParseClassName() {
		ClassNameFactory<TestObject> factory = new ClassNameFactory<TestObject>();
		assertNotNull(factory);
		
		String klassName = null;
		
		assertNull(factory.parseClassName(null));
		
		klassName = factory.parseClassName(".factory.ClassFactoryTest.TestObject");
		assertEquals("com.dailystudio.test.factory.ClassFactoryTest.TestObject", klassName);

		klassName = factory.parseClassName("com.dailystudio.test.factory.ClassFactoryTest.TestObject");
		assertEquals("com.dailystudio.test.factory.ClassFactoryTest.TestObject", klassName);
	}
	
	public void testNewObject() {
		ClassNameFactory<TestObject> factory = new ClassNameFactory<TestObject>();
		assertNotNull(factory);
		
		TestObject object = null;

		object = factory.newObject(
				"com.dailystudio.factory.ClassNameFactoryTest$TestObject");
		assertNotNull(object);
		assertTrue(object instanceof TestObject);

		object = factory.newObject(
				"com.dailystudio.factory.ClassNameFactoryTest$TestContextObject");
		assertNotNull(object);
		assertTrue(object instanceof TestContextObject);
		assertEquals(mContext.getApplicationContext(), ((TestContextObject)object).getContext());
	}
	
}
