package com.dailystudio.factory;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.manager.ISingletonObject;
import com.dailystudio.manager.Manager;
import com.dailystudio.manager.SingletonManager;
import com.dailystudio.test.Asserts;

import android.test.AndroidTestCase;

public class SingletonFactoryTest extends AndroidTestCase  {
	
	private static class SampleObject extends Object implements ISingletonObject<String> {

		private String mName = null;
		
		public SampleObject(String name) {
			mName = name;
		}
		
		@Override
		public String getSingletonKey() {
			return mName;
		}
		
	}
	
	public static class SampleManager extends SingletonManager<String, SampleObject> {
		
		public static SampleManager getInstance() {
			return Manager.getInstance(SampleManager.class);
		}
		
	}
	
	private static class SampleFactory extends SingletoneFactory<SampleObject, String> {

		@Override
		public SampleObject findObject(String params) {
			SampleManager smgr = SampleManager.getInstance();
			if (smgr == null) {
				return null;
			}
			
			return smgr.getObject(params);
		}

		@Override
		protected SampleObject newObject(String params) {
			return new SampleObject(params);
		}

		@Override
		protected void cacheObject(SampleObject object) {
			SampleManager smgr = SampleManager.getInstance();
			if (smgr == null) {
				return;
			}
			
			smgr.addObject(object);
		}
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		Manager.clearManagers();
	}
	
	public void testCreateObject() {
		SampleFactory sfactory = new SampleFactory();
		
		SampleObject object1 = sfactory.createObject("Object1");
		SampleObject object2 = sfactory.createObject("Object2");
		SampleObject object3 = sfactory.createObject("Object3");
		SampleObject object4 = sfactory.createObject("Object4");
		SampleObject object11 = sfactory.createObject("Object1");
		
		assertNotNull(object1);
		assertNotNull(object2);
		assertNotNull(object3);
		assertNotNull(object4);
		assertNotNull(object11);

		assertEquals(object11, object1);
		
		SampleManager smgr = SampleManager.getInstance();
		assertNotNull(smgr);
		
		List<SampleObject> expected = new ArrayList<SampleObject>();
		expected.add(object1);
		expected.add(object2);
		expected.add(object3);
		expected.add(object4);

		Asserts.assertEquals(expected, smgr.listObjects());
	}

}
