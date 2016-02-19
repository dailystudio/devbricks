package com.dailystudio.dataobject.database;

import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DatabaseObjectFactory;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.dataobject.samples.DynamicColumnsObject;
import com.dailystudio.development.Logger;
import com.dailystudio.test.ActivityTestCase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUpdaterDirectSQLImplTest extends ActivityTestCase {
	
	class DynamicColumnsUpdater extends DatabaseUpdaterDirectSQLImpl {

		public DynamicColumnsUpdater(Context context, 
				Class<? extends DatabaseObject> objectClass) {
			super(context, objectClass);
		}

		protected void updateObject(DatabaseObject newObject,
				DatabaseObject oldObject) {
			if (newObject == null || oldObject == null) {
				return;
			}

			super.updateObject(newObject, oldObject);

			final int newVersion = newObject.getVersion();
			final int oldVersion = oldObject.getVersion();
			
			Logger.debug("newVersion = %d", newVersion);
			Logger.debug("oldVersion = %d", oldVersion);
			Logger.debug("newObject = %s", newObject);
			Logger.debug("oldObject = %s", oldObject);
			
			if (oldVersion == DatabaseObject.VERSION_START) {
				if (oldObject.getValue(DynamicColumnsObject.COLUMN_INT_VER1) == null) {
					newObject.setValue(DynamicColumnsObject.COLUMN_INT_VER1_UPDATED, 999);
				}
			}
			
			if (newVersion == DatabaseObject.VERSION_START + 1) {
				newObject.setValue(DynamicColumnsObject.COLUMN_INT_VER2, 1234);
				newObject.setValue(DynamicColumnsObject.COLUMN_DOUBLE_VER2, 0.414);
				newObject.setValue(DynamicColumnsObject.COLUMN_TEXT_VER2, "Text Version 2");
			}
			
			
			if (newVersion == DatabaseObject.VERSION_START + 2) {
				newObject.setValue(DynamicColumnsObject.COLUMN_TEXT_VER3, "Only In Version 3");
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
	
	public void testDoUpdate() {
		final int count = 10;
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 10; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(
					DynamicColumnsObject.class, DatabaseObject.VERSION_START);
			assertNotNull(object);
			
			if (i % 2 != 0) {
				object.setValue(DynamicColumnsObject.COLUMN_INT_VER1, i * 10);
			}
			object.setValue(DynamicColumnsObject.COLUMN_LONG_VER1, i * 10000000000l);
			Logger.debug("object = %s", object);

			objects[i] = object;
		}

		AbsDatabaseConnectivity connectivity = null;
		
		connectivity = new DatabaseConnectivityDirectSQLiteImpl(
				mTargetContext, DynamicColumnsObject.class, DatabaseObject.VERSION_START);
		assertNotNull(connectivity);
		
		connectivity.insert(objects);
		
		SQLiteDatabase sqlDB = null;
		Cursor c = null;
		DatabaseOpenHandler handler = null;
		boolean catched = false;
		
		handler = new DatabaseOpenHandler(mTargetContext, 
				DatabaseObject.classToDatabase(DynamicColumnsObject.class), DatabaseObject.VERSION_START);
		assertNotNull(handler);

		sqlDB = handler.getReadableDatabase();
		assertNotNull(sqlDB);
		
		c = sqlDB.query(DatabaseObject.classToTable(DynamicColumnsObject.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(10, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < 10; i++) {
			if (i % 2 != 0) {
				assertEquals(i * 10, c.getInt(
						c.getColumnIndex(DynamicColumnsObject.COLUMN_INT_VER1.getName())));
			}
			assertEquals(i * 10000000000l, c.getLong(
					c.getColumnIndex(DynamicColumnsObject.COLUMN_LONG_VER1.getName())));
			
			c.moveToNext();
		}
		c.close();
		sqlDB.close();
		
		DatabaseUpdateInfo info = null;
		AbsDatabaseUpdater updater = null;
		
		connectivity = new DatabaseConnectivityDirectSQLiteImpl(
				mTargetContext, DynamicColumnsObject.class, DatabaseObject.VERSION_START + 1);
		
		info = connectivity.checkUpdates();
		assertNotNull(info);
		assertTrue(info.needUpdate());
		assertEquals(DatabaseObject.VERSION_START + 1, info.getNewVersion());
		assertEquals(DatabaseObject.VERSION_START, info.getOldVersion());
	
		updater = 
			new DynamicColumnsUpdater(mTargetContext, DynamicColumnsObject.class);
		assertNotNull(updater);
		
		updater.doUpdate(info.getNewVersion(), info.getOldVersion());
		
		handler = new DatabaseOpenHandler(mTargetContext, 
				DatabaseObject.classToDatabase(DynamicColumnsObject.class), DatabaseObject.VERSION_START + 1);
		assertNotNull(handler);

		sqlDB = handler.getReadableDatabase();
		assertNotNull(sqlDB);
		
		c = sqlDB.query(DatabaseObject.classToTable(DynamicColumnsObject.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(10, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < 10; i++) {
			if (i % 2 != 0) {
				assertEquals(i * 10, c.getInt(
						c.getColumnIndex(DynamicColumnsObject.COLUMN_INT_VER1_UPDATED.getName())));
			} else {
				assertEquals(999, c.getInt(
						c.getColumnIndex(DynamicColumnsObject.COLUMN_INT_VER1_UPDATED.getName())));
			}
			
			catched = false;
			try {
				assertEquals(i * 10000000000l, c.getLong(
						c.getColumnIndex(DynamicColumnsObject.COLUMN_LONG_VER1.getName())));
				catched = false;
			} catch (IllegalStateException e) {
				catched = true;
			}
			assertTrue(catched);
			
			assertEquals(1234, c.getInt(
					c.getColumnIndex(DynamicColumnsObject.COLUMN_INT_VER2.getName())));
			assertEquals(0.414, c.getDouble(
					c.getColumnIndex(DynamicColumnsObject.COLUMN_DOUBLE_VER2.getName())));
			assertEquals("Text Version 2", c.getString(
					c.getColumnIndex(DynamicColumnsObject.COLUMN_TEXT_VER2.getName())));
			
			c.moveToNext();
		}
		c.close();
		sqlDB.close();
		
		connectivity = new DatabaseConnectivityDirectSQLiteImpl(
				mTargetContext, DynamicColumnsObject.class);
		
		info = connectivity.checkUpdates();
		assertNotNull(info);
		assertTrue(info.needUpdate());
		assertEquals(DatabaseObject.VERSION_START + 2, info.getNewVersion());
		assertEquals(DatabaseObject.VERSION_START + 1, info.getOldVersion());
	
		updater = 
			new DynamicColumnsUpdater(mTargetContext, DynamicColumnsObject.class);
		assertNotNull(updater);
		
		updater.doUpdate(info.getNewVersion(), info.getOldVersion());
		
		handler = new DatabaseOpenHandler(mTargetContext, 
				DatabaseObject.classToDatabase(DynamicColumnsObject.class), DatabaseObject.VERSION_START + 2);
		assertNotNull(handler);

		sqlDB = handler.getReadableDatabase();
		assertNotNull(sqlDB);
		
		c = sqlDB.query(DatabaseObject.classToTable(DynamicColumnsObject.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(10, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < 10; i++) {
			if (i % 2 != 0) {
				assertEquals(i * 10, c.getInt(
						c.getColumnIndex(DynamicColumnsObject.COLUMN_INT_VER1_UPDATED.getName())));
			} else {
				assertEquals(999, c.getInt(
						c.getColumnIndex(DynamicColumnsObject.COLUMN_INT_VER1_UPDATED.getName())));
			}
			
			catched = false;
			try {
				assertEquals(i * 10000000000l, c.getLong(
						c.getColumnIndex(DynamicColumnsObject.COLUMN_LONG_VER1.getName())));
				catched = false;
			} catch (IllegalStateException e) {
				catched = true;
			}
			assertTrue(catched);
			
			assertEquals(1234, c.getInt(
					c.getColumnIndex(DynamicColumnsObject.COLUMN_INT_VER2.getName())));
			assertEquals(0.414, c.getDouble(
					c.getColumnIndex(DynamicColumnsObject.COLUMN_DOUBLE_VER2.getName())));
			assertEquals("Text Version 2", c.getString(
					c.getColumnIndex(DynamicColumnsObject.COLUMN_TEXT_VER2.getName())));
			
			assertEquals("Only In Version 3", c.getString(
					c.getColumnIndex(DynamicColumnsObject.COLUMN_TEXT_VER3.getName())));
			c.moveToNext();
		}
		c.close();
		sqlDB.close();

		connectivity.delete(new Query(DynamicColumnsObject.class));
	}

}
