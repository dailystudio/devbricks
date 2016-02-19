package com.dailystudio.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dailystudio.test.Asserts;

import android.test.AndroidTestCase;

public class GroupManagerTest extends AndroidTestCase  {
	
	private static class GObject1 extends Object implements IGroupObject<String, String> {

		private String mGroup = null;
		private String mName = null;
		
		public GObject1(String name, String group) {
			mName = name;
			mGroup = group;
		}
		
		@Override
		public String getSingletonKey() {
			return mName;
		}
		
		@Override
		public String getGroup() {
			return mGroup;
		}
		
	}
	
	private static class GObject2 extends Object implements IGroupObject<Class<?>, String> {

		private String mName = null;

		public GObject2(String name) {
			mName = name;
		}
		
		@Override
		public Class<?> getGroup() {
			return getClass();
		}

		@Override
		public String getSingletonKey() {
			return mName;
		}
		
	}
	
	private static class GObject2A extends GObject2 {

		public GObject2A(String name) {
			super(name);
		}
		
	}
	
	private static class GObject2B extends GObject2 {

		public GObject2B(String name) {
			super(name);
		}
		
	}
	
	static class GroupManager1 extends GroupManager<String, String, GObject1> {

		List<String> mGroups = new ArrayList<String>();
		
		@Override
		protected void onGroupAdded(String group) {
			super.onGroupAdded(group);
			
			synchronized (mGroups) {
				mGroups.add(group);
			}
		}
		
		@Override
		protected void onGroupRemoved(String group) {
			super.onGroupRemoved(group);
			
			synchronized (mGroups) {
				mGroups.remove(group);
			}
		}

		@Override
		protected void onGroupsCleared() {
			super.onGroupsCleared();
			
			synchronized (mGroups) {
				mGroups.clear();
			}
		}
	}
	
	static class GroupManager2 extends GroupManager<Class<?>, String, GObject2> {
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		GroupManager1 gmgr1 = Manager.getInstance(GroupManager1.class);
		assertNotNull(gmgr1);
		
		GroupManager1 gmgr2 = Manager.getInstance(GroupManager1.class);
		assertNotNull(gmgr2);
		
		gmgr1.clearObjects();
		gmgr2.clearObjects();
	}
	
	public void testAddObject() {
		GroupManager1 gmgr1 = Manager.getInstance(GroupManager1.class);
		assertNotNull(gmgr1);
		
		List<GObject1> expected1A = new ArrayList<GObject1>();
		List<GObject1> expected1B = new ArrayList<GObject1>();
		
		GObject1 object1 = null;
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				object1 = new GObject1(String.valueOf(i), "GroupA");
				gmgr1.addObject(object1);
				expected1A.add(object1);
			} else {
				object1 = new GObject1(String.valueOf(i), "GroupB");
				gmgr1.addObject(object1);
				expected1B.add(object1);
			}
		}
		
		assertEquals(10, gmgr1.getCount());
		assertEquals(2, gmgr1.getGroupCount());
		
		List<GObject1> actual1A = gmgr1.getObjectsInGroup("GroupA");
		List<GObject1> actual1B = gmgr1.getObjectsInGroup("GroupB");
		Asserts.assertEquals(expected1A, actual1A);
		Asserts.assertEquals(expected1B, actual1B);

		GroupManager2 gmgr2 = Manager.getInstance(GroupManager2.class);
		assertNotNull(gmgr2);
		
		List<GObject2> expected2A = new ArrayList<GObject2>();
		List<GObject2> expected2B = new ArrayList<GObject2>();
		
		GObject2 object2 = null;
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				object2 = new GObject2A(String.valueOf(i));
				gmgr2.addObject(object2);
				expected2A.add(object2);
			} else {
				object2 = new GObject2B(String.valueOf(i));
				gmgr2.addObject(object2);
				expected2B.add(object2);
			}
		}
		
		assertEquals(10, gmgr2.getCount());
		assertEquals(2, gmgr2.getGroupCount());
		
		List<GObject2> actual2A = gmgr2.getObjectsInGroup(GObject2A.class);
		List<GObject2> actual2B = gmgr2.getObjectsInGroup(GObject2B.class);
		Asserts.assertEquals(expected2A, actual2A);
		Asserts.assertEquals(expected2B, actual2B);
	}

	public void testRemoveObject() {
		GroupManager1 gmgr1 = Manager.getInstance(GroupManager1.class);
		assertNotNull(gmgr1);
		
		List<GObject1> expected1A = new ArrayList<GObject1>();
		List<GObject1> removedList1A = new ArrayList<GObject1>();
		List<GObject1> removedList1B = new ArrayList<GObject1>();
		
		GObject1 object1 = null;
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				object1 = new GObject1(String.valueOf(i), "GroupA");
				gmgr1.addObject(object1);
				
				if (i % 2 == 0) {
					removedList1A.add(object1);
				} else {
					expected1A.add(object1);
				}
			} else {
				object1 = new GObject1(String.valueOf(i), "GroupB");
				gmgr1.addObject(object1);
				removedList1B.add(object1);
			}
		}
		
		for (GObject1 object: removedList1A) {
			gmgr1.removeObject(object);
		}
		
		for (GObject1 object: removedList1B) {
			gmgr1.removeObject(object);
		}
		
		assertEquals(1, gmgr1.getGroupCount());
		
		List<GObject1> actual1A = gmgr1.getObjectsInGroup("GroupA");
		List<GObject1> actual1B = gmgr1.getObjectsInGroup("GroupB");
		Asserts.assertEquals(expected1A, actual1A);
		assertNull(actual1B);
	}

	public void testRemoveGroup() {
		GroupManager1 gmgr1 = Manager.getInstance(GroupManager1.class);
		assertNotNull(gmgr1);
		
		List<GObject1> expected1A = new ArrayList<GObject1>();
		List<GObject1> expected1B = new ArrayList<GObject1>();
		
		GObject1 object1 = null;
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				object1 = new GObject1(String.valueOf(i), "GroupA");
				gmgr1.addObject(object1);
				expected1A.add(object1);
			} else {
				object1 = new GObject1(String.valueOf(i), "GroupB");
				gmgr1.addObject(object1);
				expected1B.add(object1);
			}
		}

		gmgr1.removeGroup("GroupA");
		assertEquals(1, gmgr1.getGroupCount());
		
		List<GObject1> actual1A = gmgr1.getObjectsInGroup("GroupA");
		List<GObject1> actual1B = gmgr1.getObjectsInGroup("GroupB");
		assertNull(actual1A);
		Asserts.assertEquals(expected1B, actual1B);
		
		gmgr1.removeGroup(null);
	}
	
	public void testGetFirstObjectInGroup() {
		GroupManager1 gmgr1 = Manager.getInstance(GroupManager1.class);
		assertNotNull(gmgr1);
		
		List<GObject1> toRemoveList = new ArrayList<GObject1>();
		
		GObject1 object1 = null;
		for (int i = 0; i < 10; i++) {
			object1 = new GObject1(String.valueOf(i), "GroupA");
			gmgr1.addObject(object1);
			toRemoveList.add(object1);
		}

		assertEquals(1, gmgr1.getGroupCount());
		assertEquals(10, gmgr1.getCountInGroup("GroupA"));
		
		for (int i = 0; i < 10; i++) {
			assertEquals(toRemoveList.get(i), 
					gmgr1.getFirstObjectInGroup("GroupA"));
			gmgr1.removeObject(toRemoveList.get(i));
		}
		
		assertNull(gmgr1.getFirstObjectInGroup("GroupA"));
	}
	
	public void testGetLastObjectInGroup() {
		GroupManager1 gmgr1 = Manager.getInstance(GroupManager1.class);
		assertNotNull(gmgr1);
		
		List<GObject1> toRemoveList = new ArrayList<GObject1>();
		
		GObject1 object1 = null;
		for (int i = 0; i < 10; i++) {
			object1 = new GObject1(String.valueOf(i), "GroupA");
			gmgr1.addObject(object1);
			toRemoveList.add(0, object1);
		}

		assertEquals(1, gmgr1.getGroupCount());
		assertEquals(10, gmgr1.getCountInGroup("GroupA"));
		
		for (int i = 0; i < 10; i++) {
			assertEquals(toRemoveList.get(i), 
					gmgr1.getLastObjectInGroup("GroupA"));
			gmgr1.removeObject(toRemoveList.get(i));
		}
		
		assertNull(gmgr1.getLastObjectInGroup("GroupA"));
	}
	
	public void testListGroups() {
		GroupManager1 gmgr1 = Manager.getInstance(GroupManager1.class);
		assertNotNull(gmgr1);
		
		GObject1 object1 = null;
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				object1 = new GObject1(String.valueOf(i), "GroupA");
				gmgr1.addObject(object1);
			} else {
				object1 = new GObject1(String.valueOf(i), "GroupB");
				gmgr1.addObject(object1);
			}
		}
		
		assertEquals(2, gmgr1.getGroupCount());
		
		List<String> expectedGroups = new ArrayList<String>();
		expectedGroups.add("GroupA");
		expectedGroups.add("GroupB");
	
		List<String> actualGroups = gmgr1.listGroups();
		
		Collections.sort(expectedGroups);
		Collections.sort(actualGroups);
	
		Asserts.assertEquals(expectedGroups, actualGroups);
	}

	public void testOnGroupStuff() {
		GroupManager1 gmgr1 = Manager.getInstance(GroupManager1.class);
		assertNotNull(gmgr1);
		
		List<GObject1> expected1A = new ArrayList<GObject1>();
		List<GObject1> expected1B = new ArrayList<GObject1>();
		List<String> expectedGroups = new ArrayList<String>();
		
		GObject1 object1 = null;
		for (int i = 0; i < 10; i++) {
			if (i % 3 == 0) {
				object1 = new GObject1(String.valueOf(i), "GroupA");
				gmgr1.addObject(object1);
				expected1A.add(object1);
			} else {
				object1 = new GObject1(String.valueOf(i), "GroupB");
				gmgr1.addObject(object1);
				expected1B.add(object1);
			}
		}
		
		expectedGroups.add("GroupA");
		expectedGroups.add("GroupB");
		
		assertEquals(10, gmgr1.getCount());
		assertEquals(2, gmgr1.getGroupCount());
		
		List<GObject1> actual1A = null;
		List<GObject1> actual1B = null;
		
		actual1A = gmgr1.getObjectsInGroup("GroupA");
		actual1B = gmgr1.getObjectsInGroup("GroupB");
		Asserts.assertEquals(expected1A, actual1A);
		Asserts.assertEquals(expected1B, actual1B);
		Asserts.assertEquals(expectedGroups, gmgr1.mGroups);
		
		for (GObject1 o: expected1B) {
			gmgr1.removeObject(o);
		}
		
		expectedGroups.remove("GroupB");
		
		actual1A = gmgr1.getObjectsInGroup("GroupA");
		actual1B = gmgr1.getObjectsInGroup("GroupB");
		Asserts.assertEquals(expected1A, actual1A);
		assertNull(actual1B);
		
		Asserts.assertEquals(expectedGroups, gmgr1.mGroups);
		
		gmgr1.removeGroup("GroupA");
		expectedGroups.remove("GroupA");
		
		actual1A = gmgr1.getObjectsInGroup("GroupA");
		actual1B = gmgr1.getObjectsInGroup("GroupB");
		assertNull(actual1A);
		assertNull(actual1B);
		
		Asserts.assertEquals(expectedGroups, gmgr1.mGroups);
	}

}
