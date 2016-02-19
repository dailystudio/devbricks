package com.dailystudio.dataobject.database;

import java.util.List;

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

public class DatabaseConnectivityTest extends ActivityTestCase {
	
	private final static String AUTHORITY = "com.dailystudio.dataobject.database";
	
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
	
	public void testInsertDatabaseObject() {
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject2.class);
		
		assertNotNull(connectivity);
		
		DatabaseObject object = DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);
		assertNotNull(object);
		
		object.setValue(SampleObject2.COLUMN_LAT, 0.1);
		object.setValue(SampleObject2.COLUMN_LON, 0.2);
		object.setValue(SampleObject2.COLUMN_ALT, 0.3);
		
		long rowId = connectivity.insert(object);
		assertEquals(true, (rowId > 0));
		
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
		AbsDatabaseConnectivity connectivity1 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject1.class);
		assertNotNull(connectivity1);
		
		AbsDatabaseConnectivity connectivity2 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject2.class);
		assertNotNull(connectivity2);
		
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
		
		connectivity1.insert(objects);
		connectivity2.insert(objects);
		
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
		AbsDatabaseConnectivity connectivity1 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject1.class);
		assertNotNull(connectivity1);
		
		AbsDatabaseConnectivity connectivity2 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject2.class);
		assertNotNull(connectivity2);
		
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
		
		connectivity1.insert(objects);
		connectivity2.insert(objects);
		
		Query query1 = new Query(SampleObject1.class);
		assertNotNull(query1);
		ExpressionToken selection1 = SampleObject1.COLUMN_TIME.gt(5000l);
		assertNotNull(selection1);
		
		query1.setSelection(selection1);
		
		connectivity1.delete(query1);

		Query qParams2 = new Query(SampleObject2.class);
		assertNotNull(qParams2);
		ExpressionToken selection2 = SampleObject2.COLUMN_LAT.gt(0.2).and(SampleObject2.COLUMN_LON.lt(0.8));
		assertNotNull(selection2);
		
		qParams2.setSelection(selection2);
		
		connectivity2.delete(qParams2);
		
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
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject1.class);
		assertNotNull(connectivity);
		
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
		
		connectivity.insert(objects);
		
		Query query = new Query(SampleObject1.class);
		assertNotNull(query);
		
		connectivity.delete(query);

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
		AbsDatabaseConnectivity connectivity1 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject1.class);
		assertNotNull(connectivity1);
		
		AbsDatabaseConnectivity connectivity2 = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, SampleObject2.class);
		assertNotNull(connectivity2);
		
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
		
		connectivity1.insert(objects);
		connectivity2.insert(objects);
		
		DatabaseObject updateObject = null;
		
		Query query1 = new Query(SampleObject1.class);
		assertNotNull(query1);
		ExpressionToken selection1 = SampleObject1.COLUMN_TIME.gt(5000l);
		assertNotNull(selection1);
		
		query1.setSelection(selection1);
		
		updateObject = 
			DatabaseObjectFactory.createDatabaseObject(SampleObject1.class);

		updateObject.setValue(SampleObject1.COLUMN_TIME, 0l);
		
		connectivity1.update(query1, updateObject);

		Query query2 = new Query(SampleObject2.class);
		assertNotNull(query2);
		ExpressionToken selection2 = SampleObject2.COLUMN_LAT.gt(0.2).and(SampleObject2.COLUMN_LON.lt(0.8));
		assertNotNull(selection2);
		
		query2.setSelection(selection2);
		
		updateObject = 
			DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);

		updateObject.setValue(SampleObject2.COLUMN_ALT, 12.34);
		
		connectivity2.update(query2, updateObject);
		
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
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.insert(objects);

		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = new Query(QueryObject.class);
		assertNotNull(query);
		ExpressionToken selToken = col.gt(5).and(col.lt(9));
		query.setSelection(selToken);
		
		List<DatabaseObject> results = connectivity.query(query);
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
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.insert(objects);

		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = new Query(QueryObject.class);
		assertNotNull(query);
		ExpressionToken selToken = col.gte(0).and(col.lt(2));
		query.setSelection(selToken);
		OrderingToken groupByToken = col.groupBy();
		query.setGroupBy(groupByToken);
		
		List<DatabaseObject> results = connectivity.query(query);
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
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.insert(objects);

		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = null;
		List<DatabaseObject> results = null;
		OrderingToken orderByToken = null;
		DatabaseObject resultObject = null;
		
		query = new Query(QueryObject.class);
		assertNotNull(query);
		orderByToken = col.orderByAscending();
		query.setOrderBy(orderByToken);
		
		results = connectivity.query(query);
		assertNotNull(results);
		assertEquals(30, results.size());
		
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
		
		query = new Query(QueryObject.class);
		assertNotNull(query);
		orderByToken = col.orderByDescending();
		query.setOrderBy(orderByToken);
		
		results = connectivity.query(query);
		assertNotNull(results);
		assertEquals(30, results.size());
		
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
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.insert(objects);

		Query query = new Query(QueryObject.class);
		assertNotNull(query);
		ExpressionToken limit = new ExpressionToken("5");
		query.setLimit(limit);
		
		List<DatabaseObject> results = connectivity.query(query);
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
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.insert(objects);

		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = new Query(QueryObject.class);
		assertNotNull(query);
		ExpressionToken selToken = col.gt(5).and(col.lt(9));
		query.setSelection(selToken);
		
		List<DatabaseObject> results = connectivity.query(query, ProjectionObject.class);
		assertNotNull(results);
		assertEquals(1, results.size());
		
		DatabaseObject resultObject = results.get(0);

		Integer iCount = resultObject.getIntegerValue("count( _id )");
		assertNotNull(iCount);
		assertEquals(3, iCount.intValue());
		
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
		
		AbsDatabaseConnectivity connectivity = 
			new DatabaseConnectivity(mTargetContext, AUTHORITY, QueryObject.class);
		assertNotNull(connectivity);
		
		connectivity.insert(objects);

		final Template templ = object.getTemplate();
		
		Column col = templ.getColumn("intValue");
		
		Query query = null;
		ExpressionToken selToken = null;
		DatabaseObject resultObject = null;
		Cursor c = null;
		
		query = new Query(QueryObject.class);
		assertNotNull(query);
		
		selToken = col.gt(5).and(col.lt(9));
		query.setSelection(selToken);
		
		c = connectivity.queryCursor(query);
		assertNotNull(c);
		assertEquals(3, c.getCount());
		
		assertTrue(c.moveToFirst());
		
		for (int i = 6; i <= 8; i++, c.moveToNext()) {
			resultObject = connectivity.fromCursor(c, QueryObject.class);
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
		
		c.close();
		
		selToken = col.gt(5).and(col.lt(9));
		query.setSelection(selToken);
		
		c = connectivity.queryCursor(query, ProjectionObject.class);
		assertNotNull(c);
		assertEquals(1, c.getCount());
		
		assertTrue(c.moveToFirst());
		
		resultObject = connectivity.fromCursor(c, ProjectionObject.class);

		Integer iCount = resultObject.getIntegerValue("count( _id )");
		assertNotNull(iCount);
		assertEquals(3, iCount.intValue());
		
		c.close();

		connectivity.delete(new Query(QueryObject.class));
	}

}
