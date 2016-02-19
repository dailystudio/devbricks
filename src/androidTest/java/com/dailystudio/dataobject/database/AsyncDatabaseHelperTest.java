package com.dailystudio.dataobject.database;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DatabaseObjectFactory;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.dataobject.samples.ProjectionObject;
import com.dailystudio.dataobject.samples.QueryObject;
import com.dailystudio.dataobject.samples.SampleObject1;
import com.dailystudio.dataobject.samples.SampleObject2;
import com.dailystudio.development.Logger;
import com.dailystudio.test.ActivityTestCase;

public class AsyncDatabaseHelperTest extends ActivityTestCase {
	
	private final static String AUTHORITY = "com.dailystudio.dataobject.database";
	
	private class TestAsyncDatabaseHelp extends AsyncDatabaseHelper {
		
		private int mToken;
		private Object mCookie;
		
		public TestAsyncDatabaseHelp(Context context, 
				Class<? extends DatabaseObject> objectClass) {
			super(context, null, objectClass);
		}
		
		public TestAsyncDatabaseHelp(Context context, 
				String authority,
				Class<? extends DatabaseObject> objectClass) {
			super(context, authority, objectClass, DatabaseObject.VERSION_LATEST);
		}
		
		public TestAsyncDatabaseHelp(Context context, 
				String authority,
				Class<? extends DatabaseObject> objectClass,
				boolean handleResultInThread) {
			super(context, authority, objectClass, 
					DatabaseObject.VERSION_LATEST, handleResultInThread);
		}
		
		public TestAsyncDatabaseHelp(Context context, 
				Class<? extends DatabaseObject> objectClass,
				int version) {
			super(context, null, objectClass, version);
		}
		
		public TestAsyncDatabaseHelp(Context context, 
				String authority,
				Class<? extends DatabaseObject> objectClass,
				int version) {
			super(context, authority, objectClass, version, false);
		}
		
		public TestAsyncDatabaseHelp(Context context, 
				String authority,
				Class<? extends DatabaseObject> objectClass,
				int version,
				boolean handleResultInThread) {
			super(context, authority, objectClass, version, handleResultInThread);
		}
		
		@Override
		public void startInsert(int token, Object cookie, DatabaseObject object) {
			super.startInsert(token, cookie, object);
			
			assignMeta(token, cookie);
		}
		
		@Override
		public void startBulkInsert(int token, Object cookie,
				DatabaseObject[] objects) {
			super.startBulkInsert(token, cookie, objects);
			
			assignMeta(token, cookie);
		}
		
		@Override
		public void startDelete(int token, Object cookie, Query query) {
			super.startDelete(token, cookie, query);
			
			assignMeta(token, cookie);
		}
		
		@Override
		public void startUpdate(int token, Object cookie, Query query,
				DatabaseObject object) {
			super.startUpdate(token, cookie, query, object);
			
			assignMeta(token, cookie);
		}
		
		@Override
		public void startQuery(int token, Object cookie, Query query,
				Class<? extends DatabaseObject> projectClass) {
			super.startQuery(token, cookie, query, projectClass);
			
			assignMeta(token, cookie);
		}
		
		@Override
		public void startQueryCursor(int token, Object cookie, Query query,
				Class<? extends DatabaseObject> projectClass) {
			super.startQueryCursor(token, cookie, query, projectClass);
			
			assignMeta(token, cookie);
		}
		
		@Override
		protected void onInsertComplete(int token, Object cookie, long rowId) {
			super.onInsertComplete(token, cookie, rowId);
			
			assertMeta(token, cookie);
			assertEquals(true, (rowId > 0));
			
			notifyWatcher();
		}
		
		@Override
		protected void onBulkInsertComplete(int token, Object cookie,
				int rowsAdded) {
			super.onBulkInsertComplete(token, cookie, rowsAdded);
			
			assertMeta(token, cookie);
			assertEquals(true, (rowsAdded > 0));
			
			notifyWatcher();
		}
		
		@Override
		protected void onDeleteComplete(int token, Object cookie,
				int rowsDeleted) {
			super.onDeleteComplete(token, cookie, rowsDeleted);
			
			assertMeta(token, cookie);
			assertEquals(true, (rowsDeleted > 0));
			
			notifyWatcher();
		}
		
		@Override
		protected void onUpdateComplete(int token, Object cookie,
				int rowsUpdated) {
			super.onUpdateComplete(token, cookie, rowsUpdated);
			
			assertMeta(token, cookie);
			assertEquals(true, (rowsUpdated > 0));
			
			notifyWatcher();
		}
		
		@Override
		protected void onQueryComplete(int token, Object cookie,
				List<DatabaseObject> objects) {
			super.onQueryComplete(token, cookie, objects);
			
			assertMeta(token, cookie);
			assertQueryData(cookie, objects);
			
			notifyWatcher();
		}
		
		@Override
		protected void onQueryCursorComplete(int token, Object cookie,
				Cursor cursor) {
			super.onQueryCursorComplete(token, cookie, cursor);
			
			assertMeta(token, cookie);
			assertQueryCursor(cookie, cursor);
			
			notifyWatcher();
		}
		
		private void assignMeta(int token, Object cookie) {
			mToken = token;
			mCookie = cookie;
		}
		
		private void assertMeta(int token, Object cookie) {
			assertEquals(mToken, token);
			assertEquals(mCookie, cookie);
		}
		
		private void notifyWatcher() {
			synchronized (this) {
				notifyAll();
			}
		}
		
		private void assertQueryCursor(Object cookie, Cursor cursor) {
			if (cookie.equals("tesetQueryCursor1")) {
				assertQueryCursor1(cursor);
			} else if (cookie.equals("tesetQueryCursor2")) {
				assertQueryCursor2(cursor);
			} 		
		}

		private void assertQueryCursor2(Cursor cursor) {
			AbsDatabaseConnectivity connectivity = 
				new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
			assertNotNull(connectivity);

			assertNotNull(cursor);
			assertEquals(3, cursor.getCount());
			
			assertTrue(cursor.moveToFirst());
			
			DatabaseObject resultObject = null;
			for (int i = 6; i <= 8; i++, cursor.moveToNext()) {
				resultObject = connectivity.fromCursor(cursor, QueryObject.class);
				assertNotNull(resultObject);
				
				Logger.debug("resultObject(%s)", resultObject.toSQLSelectionString());
				Integer iVal = resultObject.getIntegerValue("intValue");
				assertNotNull(iVal);
				assertEquals(i, iVal.intValue());

				Double dVal = resultObject.getDoubleValue("doubleValue");
				assertNotNull(dVal);
				assertEquals(((double) i * 2), dVal.doubleValue());

				String sVal = resultObject.getTextValue("textValue");
				assertNotNull(sVal);
				assertEquals(String.format("%04d", i * 3), sVal);
			}
			
			cursor.close();
		}

		private void assertQueryCursor1(Cursor cursor) {
			AbsDatabaseConnectivity connectivity = 
				new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
			assertNotNull(connectivity);

			assertNotNull(cursor);
			assertEquals(1, cursor.getCount());
			
			assertTrue(cursor.moveToFirst());
			
			DatabaseObject resultObject = 
				connectivity.fromCursor(cursor, ProjectionObject.class);

			Integer iCount = resultObject.getIntegerValue("count( _id )");
			assertNotNull(iCount);
			assertEquals(3, iCount.intValue());
			
			cursor.close();
		}

		private void assertQueryData(Object cookie, List<DatabaseObject> objects) {
			if (cookie.equals("tesetSimpleQuery")) {
				assertQueryDataForTestSimpleQuery(objects);
			} else if (cookie.equals("testQueryWithGroupBy")) {
				assertQueryDataForTestQueryWithGroupBy(objects);
			} else if (cookie.equals("testQueryWithOrderBy1")) {
				assertQueryDataForTestQueryWithOrderBy1(objects);
			} else if (cookie.equals("testQueryWithOrderBy2")) {
				assertQueryDataForTestQueryWithOrderBy2(objects);
			} else if (cookie.equals("testLimitQuery")) {
				assertQueryDataForTestLimitQuery(objects);
			} else if (cookie.equals("testQueryWithProjectionClass")) {
				assertQueryDataForTestQueryWithProjectionClass(objects);
			}
		}

		private void assertQueryDataForTestSimpleQuery(List<DatabaseObject> results) {
			assertNotNull(results);
			assertEquals(3, results.size());
			
			DatabaseObject resultObject = null;
			for (int i = 6; i <= 8; i++) {
				resultObject = results.get(i - 6);
				assertNotNull(resultObject);
				
				Logger.debug("resultObject(%s)", resultObject.toSQLSelectionString());
				Integer iVal = resultObject.getIntegerValue("intValue");
				assertNotNull(iVal);
				assertEquals(i, iVal.intValue());

				Double dVal = resultObject.getDoubleValue("doubleValue");
				assertNotNull(dVal);
				assertEquals(((double) i * 2), dVal.doubleValue());

				String sVal = resultObject.getTextValue("textValue");
				assertNotNull(sVal);
				assertEquals(String.format("%04d", i * 3), sVal);
			}
		}
		
		private void assertQueryDataForTestQueryWithGroupBy(List<DatabaseObject> results) {
			assertNotNull(results);
			assertEquals(2, results.size());
			
			DatabaseObject resultObject = null;
			for (int i = 0; i < 2; i++) {
				resultObject = results.get(i);
				assertNotNull(resultObject);
					
				Logger.debug("resultObject(%s)", resultObject.toSQLSelectionString());
				Integer iVal = resultObject.getIntegerValue("intValue");
				assertNotNull(iVal);
				assertEquals(i, iVal.intValue());
		
				Double dVal = resultObject.getDoubleValue("doubleValue");
				assertNotNull(dVal);
				assertEquals(((double) i * 2), dVal.doubleValue());
		
				String sVal = resultObject.getTextValue("textValue");
				assertNotNull(sVal);
				assertEquals(String.format("%04d", i * 3), sVal);
			}
		}
		
		private void assertQueryDataForTestQueryWithOrderBy1(List<DatabaseObject> results) {
			assertNotNull(results);
			assertEquals(30, results.size());
			
			DatabaseObject resultObject = null;
			for (int j = 0; j < 10; j++) {
				for (int i = 0; i < 3; i++) {
					resultObject = results.get(j * 3 + i);
					assertNotNull(resultObject);
						
					Logger.debug("resultObject(%s)", resultObject.toSQLSelectionString());
					Integer iVal = resultObject.getIntegerValue("intValue");
					assertNotNull(iVal);
					assertEquals(j, iVal.intValue());
			
					Double dVal = resultObject.getDoubleValue("doubleValue");
					assertNotNull(dVal);
					assertEquals(((double) j * 2), dVal.doubleValue());
			
					String sVal = resultObject.getTextValue("textValue");
					assertNotNull(sVal);
					assertEquals(String.format("%04d", j * 3), sVal);
				}
			}
		}
		
		private void assertQueryDataForTestQueryWithOrderBy2(List<DatabaseObject> results) {
			assertNotNull(results);
			assertEquals(30, results.size());
			
			DatabaseObject resultObject = null;
			for (int j = 9; j >= 0; j--) {
				for (int i = 0; i < 3; i++) {
					resultObject = results.get((9 - j) * 3 + i);
					assertNotNull(resultObject);
						
					Logger.debug("resultObject(%s)", resultObject.toSQLSelectionString());
					Integer iVal = resultObject.getIntegerValue("intValue");
					assertNotNull(iVal);
					assertEquals(j, iVal.intValue());
			
					Double dVal = resultObject.getDoubleValue("doubleValue");
					assertNotNull(dVal);
					assertEquals(((double) j * 2), dVal.doubleValue());
			
					String sVal = resultObject.getTextValue("textValue");
					assertNotNull(sVal);
					assertEquals(String.format("%04d", j * 3), sVal);
				}
			}
		}
		
		private void assertQueryDataForTestLimitQuery(List<DatabaseObject> results) {
			assertNotNull(results);
			assertEquals(5, results.size());
			
			DatabaseObject resultObject = null;
			for (int i = 0; i < 5; i++) {
				resultObject = results.get(i);
				assertNotNull(resultObject);
				
				Logger.debug("resultObject(%s)", resultObject.toSQLSelectionString());
				Integer iVal = resultObject.getIntegerValue("intValue");
				assertNotNull(iVal);
				assertEquals(i, iVal.intValue());

				Double dVal = resultObject.getDoubleValue("doubleValue");
				assertNotNull(dVal);
				assertEquals(((double) i * 2), dVal.doubleValue());

				String sVal = resultObject.getTextValue("textValue");
				assertNotNull(sVal);
				assertEquals(String.format("%04d", i * 3), sVal);
			}
		}
		
		private void assertQueryDataForTestQueryWithProjectionClass(List<DatabaseObject> results) {
			assertNotNull(results);
			assertEquals(1, results.size());
			
			DatabaseObject resultObject = results.get(0);

			Integer iCount = resultObject.getIntegerValue("count( _id )");
			assertNotNull(iCount);
			assertEquals(3, iCount.intValue());
		}

	}

	private Random mRandom = new Random();
	
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
	
	private void waitHelper(AsyncDatabaseHelper helper) {
		if (helper == null) {
			return;
		}
		
		synchronized (helper) {
			try {
				helper.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void testInsertDatabaseObject() {
		AsyncDatabaseHelper helper = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, SampleObject2.class, true);
		assertNotNull(helper);
		
		DatabaseObject object = DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);
		assertNotNull(object);
		
		object.setValue(SampleObject2.COLUMN_LAT, 0.1);
		object.setValue(SampleObject2.COLUMN_LON, 0.2);
		object.setValue(SampleObject2.COLUMN_ALT, 0.3);
		
		helper.startInsert(mRandom.nextInt(), "InsertCookie", object);

		waitHelper(helper);
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject2.class);
		
		assertNotNull(connectivity);
		
		DatabaseOpenHandler handler = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject2.class), 0x1);
		assertNotNull(handler);
		
		SQLiteDatabase sqlDB = handler.getReadableDatabase();
		assertNotNull(sqlDB);
		Cursor c = sqlDB.query(DatabaseObject.classToTable(SampleObject2.class),
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(1, c.getCount());
		
		assertEquals(true, c.moveToFirst());
		assertEquals(0.1, c.getDouble(c.getColumnIndex(SampleObject2.COLUMN_LAT.getName())));
		assertEquals(0.2, c.getDouble(c.getColumnIndex(SampleObject2.COLUMN_LON.getName())));
		assertEquals(0.3, c.getDouble(c.getColumnIndex(SampleObject2.COLUMN_ALT.getName())));

		c.close();
		
//		sqlDB.delete(AbsDatabaseConnectivity.classToTable(SampleObject2.class), 
//				null, null);
		
		sqlDB.close();

		connectivity.delete(new Query(SampleObject2.class));
	}

	public void testInsertDatabaseObjects() {
		AsyncDatabaseHelper helper1 = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, SampleObject1.class, true);
		assertNotNull(helper1);
		
		AsyncDatabaseHelper helper2 = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, SampleObject2.class, true);
		assertNotNull(helper2);
		
		final int count = 10;
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				object = DatabaseObjectFactory.createDatabaseObject(SampleObject1.class);
				assertNotNull(object);
				
				object.setValue(SampleObject1.COLUMN_TIME, (1000l * i));
			} else {
				object = DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);
				assertNotNull(object);
				
				object.setValue(SampleObject2.COLUMN_LAT, 0.1 * i);
				object.setValue(SampleObject2.COLUMN_LON, 0.2 * i);
				object.setValue(SampleObject2.COLUMN_ALT, 0.3 * i);
			}
			
			objects[i] = object;
		}
		
		helper1.startBulkInsert(mRandom.nextInt(), "BulkInsertCookie1", objects);
		waitHelper(helper1);
		
		helper2.startBulkInsert(mRandom.nextInt(), "BulkInsertCookie2", objects);
		waitHelper(helper2);
		
		AbsDatabaseConnectivity connectivity1 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject1.class);
		assertNotNull(connectivity1);
		
		AbsDatabaseConnectivity connectivity2 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject2.class);
		assertNotNull(connectivity2);
		
		DatabaseOpenHandler handler1 = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject1.class), 0x1);
		assertNotNull(handler1);
		
		DatabaseOpenHandler handler2 = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject2.class), 0x1);
		assertNotNull(handler2);
		
		SQLiteDatabase sqlDB = null;
		Cursor c = null;
		
		sqlDB = handler1.getReadableDatabase();
		assertNotNull(sqlDB);
		c = sqlDB.query(DatabaseObject.classToTable(SampleObject1.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(5, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				assertEquals(i * 1000l, c.getLong(c.getColumnIndex("time")));
				c.moveToNext();
			}
		}
		c.close();
		
//		sqlDB.delete(AbsDatabaseConnectivity.classToTable(SampleObject1.class), 
//				null, null);
		
		sqlDB.close();
		
		sqlDB = handler2.getReadableDatabase();
		assertNotNull(sqlDB);
		c = sqlDB.query(DatabaseObject.classToTable(SampleObject2.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(5, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < 5; i++) {
			if (i % 2 != 0) {
				assertEquals(0.1 * i, c.getDouble(c.getColumnIndex("latitude")));
				assertEquals(0.2 * i, c.getDouble(c.getColumnIndex("longitude")));
				assertEquals(0.3 * i, c.getDouble(c.getColumnIndex("altitude")));
				c.moveToNext();
			}
		}
		c.close();
		
//		sqlDB.delete(AbsDatabaseConnectivity.classToTable(SampleObject2.class), 
//				null, null);
		
		sqlDB.close();

		connectivity1.delete(new Query(SampleObject1.class));
		connectivity2.delete(new Query(SampleObject2.class));
	}

	public void testDeleteDatabaseObjects() {
		AsyncDatabaseHelper helper1 = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, SampleObject1.class, true);
		assertNotNull(helper1);
		
		AsyncDatabaseHelper helper2 = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, SampleObject2.class, true);
		assertNotNull(helper2);
		
		final int count = 10;
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				object = DatabaseObjectFactory.createDatabaseObject(SampleObject1.class);
				assertNotNull(object);
				
				object.setValue(SampleObject1.COLUMN_TIME, (1000l * i));
			} else {
				object = DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);
				assertNotNull(object);
				
				object.setValue(SampleObject2.COLUMN_LAT, 0.1 * i);
				object.setValue(SampleObject2.COLUMN_LON, 0.2 * i);
				object.setValue(SampleObject2.COLUMN_ALT, 0.3 * i);
			}
			
			objects[i] = object;
		}
		
		helper1.startBulkInsert(mRandom.nextInt(), "BulkInsert1", objects);
		waitHelper(helper1);
		helper2.startBulkInsert(mRandom.nextInt(), "BulkInsert2", objects);
		waitHelper(helper2);
		
		Query query1 = new Query(SampleObject1.class);
		assertNotNull(query1);
		ExpressionToken selection1 = SampleObject1.COLUMN_TIME.gt(5000l);
		assertNotNull(selection1);
		
		query1.setSelection(selection1);
		
		helper1.startDelete(mRandom.nextInt(), "BulkInsert2", query1);
		waitHelper(helper1);

		Query qParams2 = new Query(SampleObject2.class);
		assertNotNull(qParams2);
		ExpressionToken selection2 = SampleObject2.COLUMN_LAT.gt(0.2).and(SampleObject2.COLUMN_LON.lt(0.8));
		assertNotNull(selection2);
		
		qParams2.setSelection(selection2);
		
		helper2.startDelete(mRandom.nextInt(), "BulkInsert2", qParams2);
		waitHelper(helper2);
		
		AbsDatabaseConnectivity connectivity1 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject1.class);
		assertNotNull(connectivity1);
		
		AbsDatabaseConnectivity connectivity2 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject2.class);
		assertNotNull(connectivity2);
		
		DatabaseOpenHandler handler1 = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject1.class), 0x1);
		assertNotNull(handler1);
		
		DatabaseOpenHandler handler2 = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject2.class), 0x1);
		assertNotNull(handler2);
		
		SQLiteDatabase sqlDB = null;
		Cursor c = null;
		
		sqlDB = handler1.getReadableDatabase();
		assertNotNull(sqlDB);
		
		c = sqlDB.query(DatabaseObject.classToTable(SampleObject1.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(3, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				if (i * 1000l > 5000l) {
					continue;
				} else {
					assertEquals(i * 1000l, c.getLong(c.getColumnIndex("time")));
				}
				c.moveToNext();
			}
		}
		c.close();
		
//		sqlDB.delete(AbsDatabaseConnectivity.classToTable(SampleObject1.class), 
//				null, null);
		
		sqlDB.close();

		sqlDB = handler2.getReadableDatabase();
		assertNotNull(sqlDB);
		c = sqlDB.query(DatabaseObject.classToTable(SampleObject2.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(4, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < 5; i++) {
			if (i % 2 != 0) {
				if ((0.1 * i > 0.2) && (0.2 * i < 0.8)) {
					continue;
				} else {
					assertEquals(0.1 * i, c.getDouble(c.getColumnIndex("latitude")));
					assertEquals(0.2 * i, c.getDouble(c.getColumnIndex("longitude")));
					assertEquals(0.3 * i, c.getDouble(c.getColumnIndex("altitude")));
				}
				c.moveToNext();
			}
		}
		c.close();
		
//		sqlDB.delete(AbsDatabaseConnectivity.classToTable(SampleObject2.class), 
//				null, null);
		
		sqlDB.close();

		connectivity1.delete(new Query(SampleObject1.class));
		connectivity2.delete(new Query(SampleObject2.class));
	}

	public void testDeleteAllDatabaseObjects() {
		AsyncDatabaseHelper helper = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, SampleObject1.class, true);
		assertNotNull(helper);
		
		final int count = 10;
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 10; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(SampleObject1.class);
			assertNotNull(object);
				
			object.setValue(SampleObject1.COLUMN_TIME, (1000l * i));
			objects[i] = object;
		}
		
		helper.startBulkInsert(mRandom.nextInt(), "BulkInsert", objects);
		waitHelper(helper);
		
		Query query = new Query(SampleObject1.class);
		assertNotNull(query);
		
		helper.startDelete(mRandom.nextInt(), "BulkInsert", query);
		waitHelper(helper);

		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject1.class);
		assertNotNull(connectivity);
		
		DatabaseOpenHandler handler = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject1.class), 0x1);
		assertNotNull(handler);

		SQLiteDatabase sqlDB = handler.getReadableDatabase();
		assertNotNull(sqlDB);
	
		boolean catched = false;
		try {
			sqlDB.query(DatabaseObject.classToTable(SampleObject1.class), 
					null, null, null, null, null, null);
			
			catched = false;
		} catch (SQLException e) {
			e.printStackTrace();
			
			catched = true;
		}
		
		sqlDB.close();

		assertTrue(catched);

		connectivity.delete(new Query(SampleObject1.class));
	}

	public void testUpdateDatabaseObjects() {
		AsyncDatabaseHelper helper1 = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, SampleObject1.class, true);
		assertNotNull(helper1);
		
		AsyncDatabaseHelper helper2 = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, SampleObject2.class, true);
		assertNotNull(helper2);
		
		final int count = 10;
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				object = DatabaseObjectFactory.createDatabaseObject(SampleObject1.class);
				assertNotNull(object);
				
				object.setValue(SampleObject1.COLUMN_TIME, (1000l * i));
			} else {
				object = DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);
				assertNotNull(object);
				
				object.setValue(SampleObject2.COLUMN_LAT, 0.1 * i);
				object.setValue(SampleObject2.COLUMN_LON, 0.2 * i);
				object.setValue(SampleObject2.COLUMN_ALT, 0.3 * i);
			}
			
			objects[i] = object;
		}
		
		helper1.startBulkInsert(mRandom.nextInt(), "BulkInsert1", objects);
		waitHelper(helper1);
		helper2.startBulkInsert(mRandom.nextInt(), "BulkInsert2", objects);
		waitHelper(helper2);
		
		DatabaseObject updateObject = null;
		
		Query query1 = new Query(SampleObject1.class);
		assertNotNull(query1);
		ExpressionToken selection1 = SampleObject1.COLUMN_TIME.gt(5000l);
		assertNotNull(selection1);
		
		query1.setSelection(selection1);
		
		updateObject = 
			DatabaseObjectFactory.createDatabaseObject(SampleObject1.class);

		updateObject.setValue(SampleObject1.COLUMN_TIME, 0l);
		
		helper1.startUpdate(mRandom.nextInt(), "update1", query1, updateObject);
		waitHelper(helper1);
		
		Query query2 = new Query(SampleObject2.class);
		assertNotNull(query2);
		ExpressionToken selection2 = SampleObject2.COLUMN_LAT.gt(0.2).and(SampleObject2.COLUMN_LON.lt(0.8));
		assertNotNull(selection2);
		
		query2.setSelection(selection2);
		
		updateObject = 
			DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);

		updateObject.setValue(SampleObject2.COLUMN_ALT, 12.34);
		
		helper2.startUpdate(mRandom.nextInt(), "update2", query2, updateObject);
		waitHelper(helper2);
		
		AbsDatabaseConnectivity connectivity1 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject1.class);
		assertNotNull(connectivity1);
		
		AbsDatabaseConnectivity connectivity2 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject2.class);
		assertNotNull(connectivity2);
		
		DatabaseOpenHandler handler1 = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject1.class), 0x1);
		assertNotNull(handler1);
		
		DatabaseOpenHandler handler2 = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject2.class), 0x1);
		assertNotNull(handler2);
		
		SQLiteDatabase sqlDB = null;
		Cursor c = null;
		
		sqlDB = handler1.getReadableDatabase();
		assertNotNull(sqlDB);
		
		c = sqlDB.query(DatabaseObject.classToTable(SampleObject1.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(5, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < 10; i++) {
			if (i % 2 == 0) {
				if (i * 1000l > 5000l) {
					assertEquals(0, c.getLong(c.getColumnIndex("time")));
				} else {
					assertEquals(i * 1000l, c.getLong(c.getColumnIndex("time")));
				}
				c.moveToNext();
			}
		}
		c.close();
		sqlDB.close();

		sqlDB = handler2.getReadableDatabase();
		assertNotNull(sqlDB);
		
		c = sqlDB.query(DatabaseObject.classToTable(SampleObject2.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(5, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < 5; i++) {
			if (i % 2 != 0) {
				if ((0.1 * i > 0.2) && (0.2 * i < 0.8)) {
					assertEquals(0.1 * i, c.getDouble(c.getColumnIndex("latitude")));
					assertEquals(0.2 * i, c.getDouble(c.getColumnIndex("longitude")));
					assertEquals(12.34, c.getDouble(c.getColumnIndex("altitude")));
				} else {
					assertEquals(0.1 * i, c.getDouble(c.getColumnIndex("latitude")));
					assertEquals(0.2 * i, c.getDouble(c.getColumnIndex("longitude")));
					assertEquals(0.3 * i, c.getDouble(c.getColumnIndex("altitude")));
				}
				c.moveToNext();
			}
		}
		c.close();
		sqlDB.close();

		connectivity1.delete(new Query(SampleObject1.class));
		connectivity2.delete(new Query(SampleObject2.class));
	}
	
	public void testSimpleQuery() {
		final int count = 10;
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 10; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
			assertNotNull(object);
			
			object.setValue("intValue", i);
			object.setValue("doubleValue", ((double) i * 2));
			object.setValue("textValue", String.format("%04d", i * 3));
			
			objects[i] = object;
		}
		
		AsyncDatabaseHelper helper = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, QueryObject.class, true);
		assertNotNull(helper);
		
		helper.startBulkInsert(mRandom.nextInt(), "BulkInsert1", objects);
		waitHelper(helper);
		
		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = new Query(QueryObject.class);
		assertNotNull(query);
		ExpressionToken selToken = col.gt(5).and(col.lt(9));
		query.setSelection(selToken);
		
		helper.startQuery(mRandom.nextInt(), "testSimpleQuery", query);
		waitHelper(helper);
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.delete(new Query(QueryObject.class));
	}


	public void testQueryWithGroupBy() {
		final int count = (3 * 10);
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 10; j++) {
				object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
				assertNotNull(object);
				
				object.setValue("intValue", i);
				object.setValue("doubleValue", ((double) i * 2));
				object.setValue("textValue", String.format("%04d", i * 3));
				
				objects[i * 10 + j] = object;
			}
		}
		
		AsyncDatabaseHelper helper = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, QueryObject.class, true);
		assertNotNull(helper);
		
		helper.startBulkInsert(mRandom.nextInt(), "BulkInsert1", objects);
		waitHelper(helper);

		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = new Query(QueryObject.class);
		assertNotNull(query);
		ExpressionToken selToken = col.gte(0).and(col.lt(2));
		query.setSelection(selToken);
		OrderingToken groupByToken = col.groupBy();
		query.setGroupBy(groupByToken);
		
		helper.startQuery(mRandom.nextInt(), "testQueryWithGroupBy", query);
		waitHelper(helper);
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.delete(new Query(QueryObject.class));
	}
	
	public void testQueryWithOrderBy() {
		final int count = (3 * 10);
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 10; j++) {
				object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
				assertNotNull(object);
				
				object.setValue("intValue", j);
				object.setValue("doubleValue", ((double) j * 2));
				object.setValue("textValue", String.format("%04d", j * 3));
				
				objects[i * 10 + j] = object;
			}
		}
		
		AsyncDatabaseHelper helper = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, QueryObject.class, true);
		assertNotNull(helper);
		
		helper.startBulkInsert(mRandom.nextInt(), "BulkInsert1", objects);
		waitHelper(helper);

		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = null;
		OrderingToken orderByToken = null;
		
		query = new Query(QueryObject.class);
		assertNotNull(query);
		orderByToken = col.orderByAscending();
		query.setOrderBy(orderByToken);
		
		helper.startQuery(mRandom.nextInt(), "testQueryWithOrderBy1", query);
		waitHelper(helper);
		
		query = new Query(QueryObject.class);
		assertNotNull(query);
		orderByToken = col.orderByDescending();
		query.setOrderBy(orderByToken);
		
		helper.startQuery(mRandom.nextInt(), "testQueryWithOrderBy2", query);
		waitHelper(helper);

		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.delete(new Query(QueryObject.class));
	}

	public void testLimitQuery() {
		final int count = 10;
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 10; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
			assertNotNull(object);
			
			object.setValue("intValue", i);
			object.setValue("doubleValue", ((double) i * 2));
			object.setValue("textValue", String.format("%04d", i * 3));
			
			objects[i] = object;
		}
		
		AsyncDatabaseHelper helper = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, QueryObject.class, true);
		assertNotNull(helper);
		
		helper.startBulkInsert(mRandom.nextInt(), "BulkInsert1", objects);
		waitHelper(helper);

		Query query = new Query(QueryObject.class);
		assertNotNull(query);
		ExpressionToken limit = new ExpressionToken("5");
		query.setLimit(limit);
		
		helper.startQuery(mRandom.nextInt(), "testLimitQuery", query);
		waitHelper(helper);
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.delete(new Query(QueryObject.class));
	}

	public void testQueryWithProjectionClass() {
		final int count = 10;
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 10; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
			assertNotNull(object);
			
			object.setValue("intValue", i);
			object.setValue("doubleValue", ((double) i * 2));
			object.setValue("textValue", String.format("%04d", i * 3));
			
			objects[i] = object;
		}
		
		AsyncDatabaseHelper helper = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, QueryObject.class, true);
		assertNotNull(helper);
		
		helper.startBulkInsert(mRandom.nextInt(), "BulkInsert1", objects);
		waitHelper(helper);

		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = new Query(QueryObject.class);
		assertNotNull(query);
		ExpressionToken selToken = col.gt(5).and(col.lt(9));
		query.setSelection(selToken);
		
		helper.startQuery(mRandom.nextInt(), "testQueryWithProjectionClass", 
				query, ProjectionObject.class);
		waitHelper(helper);
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.delete(new Query(QueryObject.class));
	}

	public void testQueryCursor() {
		final int count = 10;
		DatabaseObject object = null;
		DatabaseObject[] objects = new DatabaseObject[count];
		assertNotNull(objects);
		
		for (int i = 0; i < 10; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
			assertNotNull(object);
			
			object.setValue("intValue", i);
			object.setValue("doubleValue", ((double) i * 2));
			object.setValue("textValue", String.format("%04d", i * 3));
			
			objects[i] = object;
		}
		
		AsyncDatabaseHelper helper = new TestAsyncDatabaseHelp(
				mTargetContext, AUTHORITY, QueryObject.class, true);
		assertNotNull(helper);
		
		helper.startBulkInsert(mRandom.nextInt(), "BulkInsert1", objects);
		waitHelper(helper);

		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = null;
		ExpressionToken selToken = null;
		
		query = new Query(QueryObject.class);
		assertNotNull(query);
		
		selToken = col.gt(5).and(col.lt(9));
		query.setSelection(selToken);
	
		helper.startQueryCursor(mRandom.nextInt(), "testQueryCursor1", query);
		waitHelper(helper);

		selToken = col.gt(5).and(col.lt(9));
		query.setSelection(selToken);
		
		helper.startQueryCursor(mRandom.nextInt(), "testQueryCursor2", query);
		waitHelper(helper);
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);

		connectivity.delete(new Query(QueryObject.class));
	}

}
