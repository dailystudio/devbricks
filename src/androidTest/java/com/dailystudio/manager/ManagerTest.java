package com.dailystudio.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dailystudio.development.Logger;
import com.dailystudio.manager.IManagerWatcher;
import com.dailystudio.manager.IObjectWatcher;
import com.dailystudio.manager.Manager;
import com.dailystudio.test.Asserts;

import android.test.AndroidTestCase;

public class ManagerTest extends AndroidTestCase {
	
	static class SimpleManager1 extends Manager<String> {
		
	}
	
	static class SimpleManager2 extends Manager<Long> {
		
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
	
	public void testGetInstanceOfManager() {
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		assertEquals(simplemgr1,  Manager.getInstance(SimpleManager1.class));
		
		SimpleManager2 simplemgr2 = Manager.getInstance(SimpleManager2.class);
		assertNotNull(simplemgr2);
		assertEquals(simplemgr2,  Manager.getInstance(SimpleManager2.class));
	}

	public void testClearManagers() {
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		assertEquals(simplemgr1,  Manager.getInstance(SimpleManager1.class));
		
		SimpleManager2 simplemgr2 = Manager.getInstance(SimpleManager2.class);
		assertNotNull(simplemgr2);
		assertEquals(simplemgr2,  Manager.getInstance(SimpleManager2.class));
		
		Manager.clearManagers();
		List<Manager<?>> manangers = Manager.listManagers();
		assertNull(manangers);
	}

	public void testListManagers() {
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		
		SimpleManager2 simplemgr2 = Manager.getInstance(SimpleManager2.class);
		assertNotNull(simplemgr2);
		
		List<Manager<?>> manangers = Manager.listManagers();
		assertNotNull(manangers);

		List<Manager<?>> expected = new ArrayList<Manager<?>>();
		expected.add(simplemgr1);
		expected.add(simplemgr2);
		Collections.sort(expected);
		
		Asserts.assertEquals(expected, manangers);
	}

	public void testAddAndListObjects() {
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		
		simplemgr1.addObject(new String("1. test"));
		simplemgr1.addObject(new String("2. Add"));
		simplemgr1.addObject(new String("3. a"));
		simplemgr1.addObject(new String("4. Objects"));

		List<String> expected = new ArrayList<String>();
		expected.add(new String("1. test"));
		expected.add(new String("2. Add"));
		expected.add(new String("3. a"));
		expected.add(new String("4. Objects"));
		
		Asserts.assertEquals(expected, simplemgr1.listObjects());
	}

	public void testRemoveObjects() {
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		
		simplemgr1.addObject(new String("1. test"));
		simplemgr1.addObject(new String("2. Add"));
		simplemgr1.addObject(new String("3. a"));
		simplemgr1.addObject(new String("4. Objects"));

		simplemgr1.removeObject(new String("2. Add"));
		simplemgr1.removeObject(new String("4. Objects"));

		List<String> expected = new ArrayList<String>();
		expected.add(new String("1. test"));
		expected.add(new String("3. a"));
		
		Asserts.assertEquals(expected, simplemgr1.listObjects());
	}

	public void testClearObjects() {
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		
		simplemgr1.addObject(new String("1. test"));
		simplemgr1.addObject(new String("2. Add"));
		simplemgr1.addObject(new String("3. a"));
		simplemgr1.addObject(new String("4. Objects"));

		simplemgr1.clearObjects();

		assertEquals(0, simplemgr1.getCount());
		assertNull(simplemgr1.listObjects());
	}
	
	public void testAddManagerWatcher() {
		SimpleManagerWatcher watcher = new SimpleManagerWatcher();
		Manager.addManagerWatcher(watcher);
		
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		SimpleManager2 simplemgr2 = Manager.getInstance(SimpleManager2.class);
		assertNotNull(simplemgr2);

		List<Manager<?>> expected = new ArrayList<Manager<?>>();
		expected.add(simplemgr1);
		expected.add(simplemgr2);
		
		Asserts.assertEquals(expected, watcher.getManagers());
	}

	public void testRemoveManagerWatcher() {
		SimpleManagerWatcher watcher = new SimpleManagerWatcher();
		Manager.addManagerWatcher(watcher);
		
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		
		Manager.removeManagerWatcher(watcher);
		
		SimpleManager2 simplemgr2 = Manager.getInstance(SimpleManager2.class);
		assertNotNull(simplemgr2);

		List<Manager<?>> expected = new ArrayList<Manager<?>>();
		expected.add(simplemgr1);
		
		Asserts.assertEquals(expected, watcher.getManagers());
	}
	
	public void testAddObjectWatcher() {
		TargetAWacther watcherA = new TargetAWacther();
		assertNotNull(watcherA);
		
		TargetBWacther watcherB = new TargetBWacther();
		assertNotNull(watcherB);
		
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		
		simplemgr1.addObjectWatcher(watcherA);
		simplemgr1.addObjectWatcher(watcherB);
		
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				simplemgr1.addObject("TargetA");
			} else {
				simplemgr1.addObject("TargetB");
			}
		}

		assertEquals(4, watcherA.getCount());
		assertEquals(6, watcherB.getCount());
	}

	public void testRemoveObjectWatcher() {
		TargetAWacther watcherA = new TargetAWacther();
		assertNotNull(watcherA);
		
		TargetBWacther watcherB = new TargetBWacther();
		assertNotNull(watcherB);
		
		SimpleManager1 simplemgr1 = Manager.getInstance(SimpleManager1.class);
		assertNotNull(simplemgr1);
		
		simplemgr1.addObjectWatcher(watcherA);
		simplemgr1.addObjectWatcher(watcherB);
		
		for (int i = 0; i < 10; i++) {
			if (i == 5) {
				simplemgr1.removeObjectWatcher(watcherA);
				simplemgr1.removeObjectWatcher(watcherB);
			}
			
			if (i % 3 == 0) {
				simplemgr1.addObject("TargetA");
			} else {
				simplemgr1.addObject("TargetB");
			}
		}

		assertEquals(2, watcherA.getCount());
		assertEquals(3, watcherB.getCount());
	}
	
	private class SimpleManagerWatcher implements IManagerWatcher {

		private List<Manager<?>> mAddedManager = new ArrayList<Manager<?>>();
		
		@Override
		public void onManagerAdded(Manager<?> manager) {
			Logger.debug("manger(%s)", manager);
			mAddedManager.add(manager);
		}
		
		public List<Manager<?>> getManagers() {
			return mAddedManager;
		}
		
	}
	
	private class TargetAWacther implements IObjectWatcher<String> {

		private int mCount = 0;
		
		@Override
		public boolean matchWatcher(String object) {
			if (object == null) {
				return false;
			}
			
			return object.equals("TargetA");
		}

		@Override
		public void onObjectAdded(String object) {
			Logger.debug("object(%s), count(%d)", object, mCount);
			mCount++;
		}

		@Override
		public void onObjectRemoved(String object) {
			Logger.debug("object(%s), count(%d)", object, mCount);
			mCount--;
		}

		@Override
		public void onObjectsCleared(String[] objects) {
			Logger.debug("objects(%s), count(%d)", objects, mCount);
			mCount = 0;
		}
		
		public int getCount() {
			return mCount;
		}
		
	}

	private class TargetBWacther implements IObjectWatcher<String> {

		private int mCount = 0;
		
		@Override
		public boolean matchWatcher(String object) {
			if (object == null) {
				return false;
			}
			
			return object.equals("TargetB");
		}

		@Override
		public void onObjectAdded(String object) {
			Logger.debug("object(%s), count(%d)", object, mCount);
			mCount++;
		}

		@Override
		public void onObjectRemoved(String object) {
			Logger.debug("object(%s), count(%d)", object, mCount);
			mCount--;
		}

		@Override
		public void onObjectsCleared(String[] objects) {
			Logger.debug("objects(%s), count(%d)", objects, mCount);
			mCount = 0;
		}
		
		public int getCount() {
			return mCount;
		}
		
	}
}
