package com.dailystudio.factory;

import android.content.Context;

import com.dailystudio.factory.Factory;
import com.dailystudio.test.ActivityTestCase;

public class ClassFactoryTest extends ActivityTestCase {
	
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
	
	public void testCreateObjectForClass() {
		ClassFactory<TestObject> factory = new ClassFactory<TestObject>();
		assertNotNull(factory);
		
		TestObject object = null;

		object = factory.createObjectForClass(TestObject.class);
		assertNotNull(object);
		assertTrue(object instanceof TestObject);

		object = factory.createObjectForClass(TestContextObject.class);
		assertNotNull(object);
		assertTrue(object instanceof TestContextObject);
		assertEquals(mContext.getApplicationContext(), ((TestContextObject)object).getContext());
	}
	
}
