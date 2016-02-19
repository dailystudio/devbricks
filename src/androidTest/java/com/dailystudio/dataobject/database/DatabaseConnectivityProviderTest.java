package com.dailystudio.dataobject.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DatabaseObjectFactory;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.samples.ProjectionObject;
import com.dailystudio.dataobject.samples.QueryObject;
import com.dailystudio.dataobject.samples.SampleObject2;
import com.dailystudio.dataobject.samples.VersionControlledObject;
import com.dailystudio.test.ActivityTestCase;

public class DatabaseConnectivityProviderTest extends ActivityTestCase {
	
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
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		DatabaseObject object = new SampleObject2(mTargetContext);
		object.setValue("latitude", 0.123);
		object.setValue("longitude", 0.456);
		object.setValue("altitude", 0.789);
		
		final ContentValues values = object.getValues();
		assertNotNull(values);
		
		Uri queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, SampleObject2.class);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.insert(queryUri, values);
		
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
		assertEquals(0.123, c.getDouble(c.getColumnIndex(SampleObject2.COLUMN_LAT.getName())));
		assertEquals(0.456, c.getDouble(c.getColumnIndex(SampleObject2.COLUMN_LON.getName())));
		assertEquals(0.789, c.getDouble(c.getColumnIndex(SampleObject2.COLUMN_ALT.getName())));

		c.close();
		
		sqlDB.delete(DatabaseObject.classToTable(SampleObject2.class), 
				null, null);
		
		sqlDB.close();
	}

	public void testInsertDatabaseObjects() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < 10; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);
			assertNotNull(object);
				
			object.setValue(SampleObject2.COLUMN_LAT, 0.1 * i);
			object.setValue(SampleObject2.COLUMN_LON, 0.2 * i);
			object.setValue(SampleObject2.COLUMN_ALT, 0.3 * i);
			
			values[i] = object.getValues();
		}
		
		Uri queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, SampleObject2.class);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		DatabaseOpenHandler handler = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject2.class), 0x1);
		assertNotNull(handler);
		
		SQLiteDatabase sqlDB = null;
		Cursor c = null;
		
		sqlDB = handler.getReadableDatabase();
		assertNotNull(sqlDB);
		c = sqlDB.query(DatabaseObject.classToTable(SampleObject2.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(10, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < count; i++) {
			assertEquals(0.1 * i, c.getDouble(c.getColumnIndex("latitude")));
			assertEquals(0.2 * i, c.getDouble(c.getColumnIndex("longitude")));
			assertEquals(0.3 * i, c.getDouble(c.getColumnIndex("altitude")));

			c.moveToNext();
		}
		
		c.close();
		
		sqlDB.delete(DatabaseObject.classToTable(SampleObject2.class), 
				null, null);
		
		sqlDB.close();
	}
	
	public void testDeleteDatabaseObjects() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < count; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);
			assertNotNull(object);
				
			object.setValue(SampleObject2.COLUMN_LAT, 0.1 * i);
			object.setValue(SampleObject2.COLUMN_LON, 0.2 * i);
			object.setValue(SampleObject2.COLUMN_ALT, 0.3 * i);
			
			values[i] = object.getValues();
		}
		
		Uri queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, SampleObject2.class);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		ExpressionToken selection = SampleObject2.COLUMN_LAT.gt(0.2).and(SampleObject2.COLUMN_LON.lt(0.8));

		cr.delete(queryUri, selection.toString(), null);
		
		DatabaseOpenHandler handler = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject2.class), 0x1);
		assertNotNull(handler);
		
		SQLiteDatabase sqlDB = null;
		Cursor c = null;
		
		sqlDB = handler.getReadableDatabase();
		assertNotNull(sqlDB);
		c = sqlDB.query(DatabaseObject.classToTable(SampleObject2.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(9, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < count; i++) {
			if ((0.1 * i > 0.2) && (0.2 * i < 0.8)) {
				continue;
			} else {
				assertEquals(0.1 * i, c.getDouble(c.getColumnIndex("latitude")));
				assertEquals(0.2 * i, c.getDouble(c.getColumnIndex("longitude")));
				assertEquals(0.3 * i, c.getDouble(c.getColumnIndex("altitude")));
			}
			c.moveToNext();
		}
		
		c.close();
		
		sqlDB.delete(DatabaseObject.classToTable(SampleObject2.class), 
				null, null);
		
		sqlDB.close();
	}

	public void testDeleteAllDatabaseObjects() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < count; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);
			assertNotNull(object);
				
			object.setValue(SampleObject2.COLUMN_LAT, 0.1 * i);
			object.setValue(SampleObject2.COLUMN_LON, 0.2 * i);
			object.setValue(SampleObject2.COLUMN_ALT, 0.3 * i);
			
			values[i] = object.getValues();
		}
		
		Uri queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, SampleObject2.class);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		cr.delete(queryUri, null, null);
		
		DatabaseOpenHandler handler = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject2.class), 0x1);
		assertNotNull(handler);
		
		SQLiteDatabase sqlDB = null;
		
		sqlDB = handler.getReadableDatabase();
		assertNotNull(sqlDB);
		
		boolean catched = false;
		try {
			sqlDB.query(DatabaseObject.classToTable(SampleObject2.class), 
					null, null, null, null, null, null);
			
			catched = false;
		} catch (SQLException e) {
			e.printStackTrace();
			
			catched = true;
		}
		
		sqlDB.close();

		assertTrue(catched);
	}
	
	public void testUpdateDatabaseObjects() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < count; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);
			assertNotNull(object);
				
			object.setValue(SampleObject2.COLUMN_LAT, 0.1 * i);
			object.setValue(SampleObject2.COLUMN_LON, 0.2 * i);
			object.setValue(SampleObject2.COLUMN_ALT, 0.3 * i);
			
			values[i] = object.getValues();
		}
		
		Uri queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, SampleObject2.class);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		DatabaseObject updateObject = 
			DatabaseObjectFactory.createDatabaseObject(SampleObject2.class);

		updateObject.setValue(SampleObject2.COLUMN_ALT, 12.34);

		ExpressionToken selection = SampleObject2.COLUMN_LAT.gt(0.2).and(SampleObject2.COLUMN_LON.lt(0.8));

		cr.update(queryUri, updateObject.getValues(), 
				selection.toString(), null);
		
		DatabaseOpenHandler handler = 
			new DatabaseOpenHandler(mTargetContext, 
					DatabaseObject.classToDatabase(SampleObject2.class), 0x1);
		assertNotNull(handler);
		
		SQLiteDatabase sqlDB = null;
		Cursor c = null;
		
		sqlDB = handler.getReadableDatabase();
		assertNotNull(sqlDB);
		c = sqlDB.query(DatabaseObject.classToTable(SampleObject2.class), 
				null, null, null, null, null, null);
		assertNotNull(c);
		assertEquals(10, c.getCount());
		assertEquals(true, c.moveToFirst());
		for (int i = 0; i < count; i++) {
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
		
		c.close();
		
		sqlDB.delete(DatabaseObject.classToTable(SampleObject2.class), 
				null, null);
		
		sqlDB.close();
	}
	
	public void testSimpleQuery() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < count; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
			assertNotNull(object);
			
			object.setValue("intValue", i);
			object.setValue("doubleValue", ((double) i * 2));
			object.setValue("textValue", String.format("%04d", i * 3));
			
			values[i] = object.getValues();
		}
		
		final Template tmpl = object.getTemplate();
		long serial = 0;
		Uri queryUri = null;
		
		serial = System.currentTimeMillis();
		
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		Column col = tmpl.getColumn("intValue");
		
		ExpressionToken selection = col.gt(5).and(col.lt(9));
		assertNotNull(selection);

		Cursor c = null;

		serial = System.currentTimeMillis();
		
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		c = cr.query(queryUri, object.toSQLProjection(), 
				selection.toString(), null, null);
		
		assertNotNull(c);
		assertEquals(3, c.getCount());
		assertEquals(true, c.moveToFirst());
		
		int columnIndex = -1;
		for (int i = 6; i <= 8; i++) {
			columnIndex = c.getColumnIndex("intValue");
			assertEquals(i, c.getInt(columnIndex));
			
			columnIndex = c.getColumnIndex("doubleValue");
			assertEquals(((double) i * 2), c.getDouble(columnIndex));
			
			columnIndex = c.getColumnIndex("textValue");
			assertEquals(String.format("%04d", i * 3), c.getString(columnIndex));
			
			c.moveToNext();
		}
		
		c.close();
		
		closeOpenedDatabase(serial);
		
		cr.delete(queryUri, null, null);
	}

	public void testQueryWithGroupBy() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 3 * 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 10; j++) {
				object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
				assertNotNull(object);
				
				object.setValue("intValue", i);
				object.setValue("doubleValue", ((double) i * 2));
				object.setValue("textValue", String.format("%04d", i * 3));
				
				values[i * 10 + j] = object.getValues();
			}
		}
		
		final Template tmpl = object.getTemplate();
		long serial = 0;
		Uri queryUri = null;
		
		serial = System.currentTimeMillis();
		
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		Column col = tmpl.getColumn("intValue");
		
		ExpressionToken selection = col.gte(0).and(col.lt(2));
		assertNotNull(selection);
		OrderingToken groupByToken = col.groupBy();
		assertNotNull(groupByToken);
		
		Cursor c = null;

		serial = System.currentTimeMillis();
		
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		c = cr.query(queryUri, object.toSQLProjection(), 
				selection.toString(), null, 
				SortOrderEncoder.encode(groupByToken.toString(), null, null, null));
		
		assertNotNull(c);
		assertEquals(2, c.getCount());
		assertEquals(true, c.moveToFirst());
		
		int columnIndex = -1;
		for (int i = 0; i < 2; i++) {
			columnIndex = c.getColumnIndex("intValue");
			assertEquals(i, c.getInt(columnIndex));
			
			columnIndex = c.getColumnIndex("doubleValue");
			assertEquals(((double) i * 2), c.getDouble(columnIndex));
			
			columnIndex = c.getColumnIndex("textValue");
			assertEquals(String.format("%04d", i * 3), c.getString(columnIndex));
			
			c.moveToNext();
		}
		
		c.close();
		
		closeOpenedDatabase(serial);
		
		cr.delete(queryUri, null, null);
	}

	public void testQueryWithOrderBy() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 3 * 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 10; j++) {
				object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
				assertNotNull(object);
				
				object.setValue("intValue", j);
				object.setValue("doubleValue", ((double) j * 2));
				object.setValue("textValue", String.format("%04d", j * 3));
				
				values[i * 10 + j] = object.getValues();
			}
		}
		
		final Template tmpl = object.getTemplate();
		long serial = 0;
		Uri queryUri = null;
		
		serial = System.currentTimeMillis();
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		Column col = tmpl.getColumn("intValue");
		
		OrderingToken orderByToken = null;
		Cursor c = null;
		int columnIndex = -1;
		
		orderByToken = col.orderByAscending();
		assertNotNull(orderByToken);
		
		serial = System.currentTimeMillis();
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		c = cr.query(queryUri, object.toSQLProjection(), 
				null, null, 
				SortOrderEncoder.encode(null, null, orderByToken.toString(), null));
		
		assertNotNull(c);
		assertEquals(30, c.getCount());
		assertEquals(true, c.moveToFirst());
		
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 3; i++) {
				columnIndex = c.getColumnIndex("intValue");
				assertEquals(j, c.getInt(columnIndex));
				
				columnIndex = c.getColumnIndex("doubleValue");
				assertEquals(((double) j * 2), c.getDouble(columnIndex));
				
				columnIndex = c.getColumnIndex("textValue");
				assertEquals(String.format("%04d", j * 3), c.getString(columnIndex));
				
				c.moveToNext();
			}
		}
		
		c.close();
		
		closeOpenedDatabase(serial);
		
		orderByToken = col.orderByDescending();
		assertNotNull(orderByToken);
		
		serial = System.currentTimeMillis();
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		c = cr.query(queryUri, object.toSQLProjection(), 
				null, null, 
				SortOrderEncoder.encode(null, null, orderByToken.toString(), null));
		
		assertNotNull(c);
		assertEquals(30, c.getCount());
		assertEquals(true, c.moveToFirst());
		
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < 3; i++) {
				columnIndex = c.getColumnIndex("intValue");
				assertEquals((9 - j), c.getInt(columnIndex));
				
				columnIndex = c.getColumnIndex("doubleValue");
				assertEquals(((double) (9 - j) * 2), c.getDouble(columnIndex));
				
				columnIndex = c.getColumnIndex("textValue");
				assertEquals(String.format("%04d", (9 - j) * 3), c.getString(columnIndex));
				
				c.moveToNext();
			}
		}
		
		c.close();
		
		closeOpenedDatabase(serial);
		
		cr.delete(queryUri, null, null);
	}

	public void testLimitQuery() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < count; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
			assertNotNull(object);
			
			object.setValue("intValue", i);
			object.setValue("doubleValue", ((double) i * 2));
			object.setValue("textValue", String.format("%04d", i * 3));
			
			values[i] = object.getValues();
		}
		
		long serial = 0;
		Uri queryUri = null;
		
		serial = System.currentTimeMillis();
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		ExpressionToken limit = new ExpressionToken("5");
		assertNotNull(limit);
		
		Cursor c = null;

		serial = System.currentTimeMillis();
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);

		c = cr.query(queryUri, object.toSQLProjection(), 
				null, null, 
				SortOrderEncoder.encode(null, null, null, limit.toString()));
		
		assertNotNull(c);
		assertEquals(5, c.getCount());
		assertEquals(true, c.moveToFirst());
		
		int columnIndex = -1;
		for (int i = 0; i < 5; i++) {
			columnIndex = c.getColumnIndex("intValue");
			assertEquals(i, c.getInt(columnIndex));
			
			columnIndex = c.getColumnIndex("doubleValue");
			assertEquals(((double) i * 2), c.getDouble(columnIndex));
			
			columnIndex = c.getColumnIndex("textValue");
			assertEquals(String.format("%04d", i * 3), c.getString(columnIndex));
			
			c.moveToNext();
		}
		
		c.close();
		
		closeOpenedDatabase(serial);
		
		cr.delete(queryUri, null, null);
	}

	public void testQueryWithProjectionClass() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < count; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(QueryObject.class);
			assertNotNull(object);
			
			object.setValue("intValue", i);
			object.setValue("doubleValue", ((double) i * 2));
			object.setValue("textValue", String.format("%04d", i * 3));
			
			values[i] = object.getValues();
		}
		
		final Template tmpl = object.getTemplate();
		long serial = 0;
		Uri queryUri = null;
		
		serial = System.currentTimeMillis();
		
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		Column col = tmpl.getColumn("intValue");
		
		ExpressionToken selection = col.gt(5).and(col.lt(9));
		assertNotNull(selection);
		
		DatabaseObject projectionObject = new ProjectionObject(mContext);
		assertNotNull(projectionObject);
		
		Cursor c = null;

		serial = System.currentTimeMillis();
		
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, QueryObject.class, serial);
		assertNotNull(queryUri);
		
		c = cr.query(queryUri, projectionObject.toSQLProjection(), 
				selection.toString(), null, null);
		
		assertNotNull(c);
		assertEquals(1, c.getCount());
		assertEquals(true, c.moveToFirst());
		
		int columnIndex = -1;
		columnIndex = c.getColumnIndex(ProjectionObject.COLUMN_ID_COUNT.getName());
		assertEquals(3, c.getInt(columnIndex));

		c.close();
		
		closeOpenedDatabase(serial);
		
		cr.delete(queryUri, null, null);
	}
	
	private void closeOpenedDatabase(long serial) {
		if (serial < 0) {
			return;
		}
		
		Intent i = new Intent(OpenedDatabaseCloseReceiver.ACTION_CLOSE_DATABASE);
		assertNotNull(i);
		
		i.putExtra(OpenedDatabaseCloseReceiver.EXTRA_SERIAL, serial);
		
		mContext.sendBroadcast(i);
	}
	
	public void testQueryCommand() {
		ContentResolver cr = mTargetContext.getContentResolver();
		assertNotNull(cr);
		
		final int count = 10;
		ContentValues[] values = new ContentValues[count];
		assertNotNull(values);
		
		DatabaseObject object = null;
		for (int i = 0; i < count; i++) {
			object = DatabaseObjectFactory.createDatabaseObject(
					VersionControlledObject.class, DatabaseObject.VERSION_START);
			assertNotNull(object);
			
			object.setValue(VersionControlledObject.COLUMN_INT_VER1, i);
			object.setValue(VersionControlledObject.COLUMN_LONG_VER1, ((long) i * 10000000000l));
			
			values[i] = object.getValues();
		}
		
		Uri queryUri = null;
		
		queryUri = ProviderUriBuilder.buildQueryUri(
				AUTHORITY, VersionControlledObject.class);
		assertNotNull(queryUri);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(queryUri, 
				object.toSQLTableCreationString());
		assertNotNull(queryUri);
		
		cr.bulkInsert(queryUri, values);
		
		Uri commandUri = ProviderUriBuilder.buildCommandUri(
				AUTHORITY, VersionControlledObject.class, 
				DatabaseObject.VERSION_START + 1,
				GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(commandUri);
		
		Cursor c = null;
		
		c = cr.query(commandUri, null, null, null, null);
		
		assertNotNull(c);
		assertEquals(1, c.getCount());
		assertEquals(true, c.moveToFirst());
		
		final int newVer = c.getInt(c.getColumnIndex(GetUpdateInfoCmdCursor.COLUMN_NEW_VERSION));
		final int oldVer = c.getInt(c.getColumnIndex(GetUpdateInfoCmdCursor.COLUMN_OLD_VERSION));
		
		c.close();
		
		assertEquals(2, newVer);
		assertEquals(1, oldVer);
		
//		cr.delete(queryUri, null, null);
	}
	
}
