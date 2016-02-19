package com.dailystudio.manager;

import com.dailystudio.manager.IObjectWatcher;
import com.dailystudio.manager.ISingletonObject;
import com.dailystudio.manager.Manager;
import com.dailystudio.manager.SingletonManager;

import android.test.AndroidTestCase;

public class SingletonManagerTest extends AndroidTestCase  {
	
	private static class SObject1 extends Object implements ISingletonObject<String> {

		private String mName = null;
		
		public SObject1(String name) {
			mName = name;
		}
		
		@Override
		public String getSingletonKey() {
			return mName;
		}
		
	}
	
	private static class SObject2 extends Object implements ISingletonObject<Class<?>> {

		@Override
		public Class<?> getSingletonKey() {
			return getClass();
		}
		
	}
	
	private static class SObject2A extends SObject2 {
		
	}
	
	private static class SObject2B extends SObject2 {
		
	}
	
	static class SimpleSingletonManager1 extends SingletonManager<String, SObject1> {
		
	}
	
	static class SimpleSingletonManager2 extends SingletonManager<Class<?>, SObject2> {
		
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
	
	public void testAddObject() {
		SObject1TargetAWatcher watcher1A = new SObject1TargetAWatcher();
		assertNotNull(watcher1A);
		
		SObject1TargetBWatcher watcher1B = new SObject1TargetBWatcher();
		assertNotNull(watcher1B);
		
		SObject2TargetAWatcher watcher2A = new SObject2TargetAWatcher();
		assertNotNull(watcher2A);
		
		SObject2TargetBWatcher watcher2B = new SObject2TargetBWatcher();
		assertNotNull(watcher2B);
		
		SimpleSingletonManager1 ssmgr1 = Manager.getInstance(SimpleSingletonManager1.class);
		assertNotNull(ssmgr1);
		
		ssmgr1.addObjectWatcher(watcher1A);
		ssmgr1.addObjectWatcher(watcher1B);
		
		SimpleSingletonManager2 ssmgr2 = Manager.getInstance(SimpleSingletonManager2.class);
		assertNotNull(ssmgr2);
		
		ssmgr2.addObjectWatcher(watcher2A);
		ssmgr2.addObjectWatcher(watcher2B);

		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				ssmgr1.addObject(new SObject1("TargetA"));
			} else {
				ssmgr1.addObject(new SObject1("TargetB"));
			}
		}

		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				ssmgr2.addObject(new SObject2A());
			} else {
				ssmgr2.addObject(new SObject2B());
			}
		}

		assertEquals(1, watcher1A.getCount());
		assertEquals(1, watcher1B.getCount());
		assertEquals(1, watcher2A.getCount());
		assertEquals(1, watcher2B.getCount());
	}

	public void testRemoveObject() {
		SimpleSingletonManager1 ssmgr1 = Manager.getInstance(SimpleSingletonManager1.class);
		assertNotNull(ssmgr1);
		
		SObject1 object1A = new SObject1("TargetA");
		SObject1 object1B = new SObject1("TargetB");
		SObject1 object1C = new SObject1("TargetC");

		ssmgr1.addObject(object1A);
		ssmgr1.addObject(object1B);
		ssmgr1.addObject(object1C);
		
		ssmgr1.removeObject(object1A);
		assertEquals(2, ssmgr1.getCount());
		
		ssmgr1.removeObject(new SObject1("TargetB"));
		assertEquals(2, ssmgr1.getCount());
		
		ssmgr1.removeObjectByKey("TargetB");
		assertEquals(1, ssmgr1.getCount());
	}

	public void testGetObject() {
		SimpleSingletonManager1 ssmgr1 = Manager.getInstance(SimpleSingletonManager1.class);
		assertNotNull(ssmgr1);
		
		SObject1 objectA1 = new SObject1("TargetA");
		SObject1 objectA2 = new SObject1("TargetA");
		SObject1 objectA3 = new SObject1("TargetA");
		SObject1 objectB = new SObject1("TargetB");
		SObject1 objectC = new SObject1("TargetC");

		ssmgr1.addObject(objectA1);
		ssmgr1.addObject(objectB);
		ssmgr1.addObject(objectA2);
		ssmgr1.addObject(objectC);
		ssmgr1.addObject(objectA3);
		
		assertEquals(3, ssmgr1.getCount());
		assertEquals(objectA3, ssmgr1.getObject("TargetA"));
		assertEquals(objectB, ssmgr1.getObject("TargetB"));
		assertEquals(objectC, ssmgr1.getObject("TargetC"));
		
		assertNull(ssmgr1.getObject("TargetD"));
	}

	public void testClearObject() {
		SimpleSingletonManager1 ssmgr1 = Manager.getInstance(SimpleSingletonManager1.class);
		assertNotNull(ssmgr1);
		
		SObject1 object1A = new SObject1("TargetA");
		SObject1 object1B = new SObject1("TargetB");
		SObject1 object1C = new SObject1("TargetC");

		ssmgr1.addObject(object1A);
		ssmgr1.addObject(object1B);
		ssmgr1.addObject(object1C);
		assertEquals(3, ssmgr1.getCount());
		
		ssmgr1.clearObjects();
		
		assertNull(ssmgr1.getObject("TargetA"));
		assertNull(ssmgr1.getObject("TargetB"));
		assertNull(ssmgr1.getObject("TargetC"));
	}

	private class SObject1TargetAWatcher implements IObjectWatcher<SObject1> {

		private int mCount = 0;
		
		@Override
		public boolean matchWatcher(SObject1 object) {
			if (object == null) {
				return false;
			}
			
			String key = object.getSingletonKey();
			if (key == null) {
				return false;
			}
			
			return key.equals("TargetA");
		}

		@Override
		public void onObjectAdded(SObject1 object) {
			mCount++;
		}

		@Override
		public void onObjectRemoved(SObject1 object) {
			mCount--;
		}

		@Override
		public void onObjectsCleared(SObject1[] objects) {
			mCount = 0;
		}
		
		public int getCount() {
			return mCount;
		}
		
	}

	private class SObject1TargetBWatcher implements IObjectWatcher<SObject1> {

		private int mCount = 0;
		
		@Override
		public boolean matchWatcher(SObject1 object) {
			if (object == null) {
				return false;
			}
			
			String key = object.getSingletonKey();
			if (key == null) {
				return false;
			}
			
			return key.equals("TargetB");
		}

		@Override
		public void onObjectAdded(SObject1 object) {
			mCount++;
		}

		@Override
		public void onObjectRemoved(SObject1 object) {
			mCount--;
		}

		@Override
		public void onObjectsCleared(SObject1[] objects) {
			mCount = 0;
		}
		
		public int getCount() {
			return mCount;
		}
		
	}

	private class SObject2TargetAWatcher implements IObjectWatcher<SObject2> {

		private int mCount = 0;
		
		@Override
		public boolean matchWatcher(SObject2 object) {
			if (object == null) {
				return false;
			}
			
			Class<?> key = object.getSingletonKey();
			if (key == null) {
				return false;
			}
			
			return (key == SObject2A.class);
		}

		@Override
		public void onObjectAdded(SObject2 object) {
			mCount++;
		}

		@Override
		public void onObjectRemoved(SObject2 object) {
			mCount--;
		}

		@Override
		public void onObjectsCleared(SObject2[] objects) {
			mCount = 0;
		}
		
		public int getCount() {
			return mCount;
		}
		
	}

	private class SObject2TargetBWatcher implements IObjectWatcher<SObject2> {

		private int mCount = 0;
		
		@Override
		public boolean matchWatcher(SObject2 object) {
			if (object == null) {
				return false;
			}
			
			Class<?> key = object.getSingletonKey();
			if (key == null) {
				return false;
			}
			
			return key == SObject2A.class;
		}

		@Override
		public void onObjectAdded(SObject2 object) {
			mCount++;
		}

		@Override
		public void onObjectRemoved(SObject2 object) {
			mCount--;
		}

		@Override
		public void onObjectsCleared(SObject2[] objects) {
			mCount = 0;
		}
		
		public int getCount() {
			return mCount;
		}
		
	}

}
