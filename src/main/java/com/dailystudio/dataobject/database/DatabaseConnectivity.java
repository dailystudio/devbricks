package com.dailystudio.dataobject.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.net.Uri;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.dataobject.query.QueryToken;
import com.dailystudio.development.Logger;

public class DatabaseConnectivity extends AbsDatabaseConnectivity {

	private static String[] MOCK_COLUMN_NAMES = {
		"_id",
	};
	
	private static class MockEmptyCursor extends AbstractWindowedCursor {

		@Override
		public String[] getColumnNames() {
			return MOCK_COLUMN_NAMES;
		}

		@Override
		public int getCount() {
			return 0;
		}
		
	}
	
	private ContentResolver mContentResovler = null;

	private String mAuthority = null;
	
	public DatabaseConnectivity(Context context, 
			Class<? extends DatabaseObject> objectClass) {
		this(context, null, objectClass);
	}
	
	public DatabaseConnectivity(Context context, 
			String authority,
			Class<? extends DatabaseObject> objectClass) {
		this(context, authority, objectClass, DatabaseObject.VERSION_LATEST);
	}
	
	public DatabaseConnectivity(Context context, 
			Class<? extends DatabaseObject> objectClass,
			int version) {
		this(context, null, objectClass, version);
	}
	
	public DatabaseConnectivity(Context context, 
			String authority,
			Class<? extends DatabaseObject> objectClass,
			int version) {
		super(context, objectClass, version);
		
		if (mContext != null) {
			mContentResovler = context.getContentResolver();

			if (authority == null) {
				authority = mContext.getPackageName();
			}

		}
		
		mAuthority = authority;
	}
	
	private boolean checkProviderPreparation() {
		return (mContentResovler != null
				&& mAuthority != null
				&& mObjectClass != null);
	}
	
	@Override
	protected long onInsert(DatabaseObject object) {
		if (object == null || object.isEmpty()) {
			return 0;
		}
		
		if (checkProviderPreparation() == false) {
			return 0;
		}
		
		final ContentValues values = object.getValues();
		if (values == null) {
			return 0;
		}
		
		Uri uri = ProviderUriBuilder.buildQueryUri(mAuthority, mObjectClass, 
				getDatabaseVersion());
		if (uri == null) {
			return 0;
		}
		
		final Template template = object.getTemplate();
		if (template == null) {
			return 0;
		}
		
		final String table = DatabaseObject.classToTable(mObjectClass);
		if (table == null) {
			return 0;
		}
		
		uri = ProviderUriBuilder.attachCreateTableParamter(
				uri, object.toSQLTableCreationString());
		if (uri == null) {
			return 0;
		}
		
		Uri resUri = mContentResovler.insert(uri, values);
		if (resUri == null) {
			return 0;
		}
		
		ProviderResultUriParser parser = new ProviderResultUriParser(resUri);
		
		return parser.getRowId();
	}

	@Override
	protected int onInsert(DatabaseObject[] objects) {
		if (objects == null) {
			return 0;
		}
		
		final int count = objects.length;
		if (count <= 0) {
			return 0;
		}
		
		if (checkProviderPreparation() == false) {
			return 0;
		}
		
		ContentValues[] values = new ContentValues[count];
		
		Uri uri = ProviderUriBuilder.buildQueryUri(mAuthority, mObjectClass, 
				getDatabaseVersion());
		if (uri == null) {
			return 0;
		}
		
		final Template template = objects[0].getTemplate();
		if (template == null) {
			return 0;
		}
		
		final String table = DatabaseObject.classToTable(mObjectClass);
		if (table == null) {
			return 0;
		}
		
		final DatabaseObject sample = objects[0];
		
		uri = ProviderUriBuilder.attachCreateTableParamter(
				uri, sample.toSQLTableCreationString());
		if (uri == null) {
			return 0;
		}
		
		for (int i = 0; i < count; i++) {
			values[i] = objects[i].getValues();
		}
		
		return mContentResovler.bulkInsert(uri, values);
	}

	@Override
	protected int onDelete(Query query) {
		if (query == null) {
			return 0;
		}
		
		if (checkProviderPreparation() == false) {
			return 0;
		}
		
		Uri uri = ProviderUriBuilder.buildQueryUri(mAuthority, mObjectClass, 
				getDatabaseVersion());
		if (uri == null) {
			return 0;
		}
		
		String selection = null;
		
		QueryToken selToken = query.getSelection();
		if (selToken != null) {
			selection = selToken.toString();
		}
		
		return mContentResovler.delete(uri, selection, null);
	}

	@Override
	protected int onUpdate(Query query, DatabaseObject object) {
		if (query == null || object == null || object.isEmpty()) {
			return 0;
		}
		
		if (checkProviderPreparation() == false) {
			return 0;
		}
		
		final ContentValues values = object.getValues();
		if (values == null) {
			return 0;
		}
		
		Uri uri = ProviderUriBuilder.buildQueryUri(mAuthority, mObjectClass, 
				getDatabaseVersion());
		if (uri == null) {
			return 0;
		}
		
		final Template template = object.getTemplate();
		if (template == null) {
			return 0;
		}
		
		final String table = DatabaseObject.classToTable(mObjectClass);
		if (table == null) {
			return 0;
		}
		
		uri = ProviderUriBuilder.attachCreateTableParamter(
				uri, object.toSQLTableCreationString());
		if (uri == null) {
			return 0;
		}
		
		String selection = null;
		
		QueryToken selToken = query.getSelection();
		if (selToken != null) {
			selection = selToken.toString();
		}
		
		return mContentResovler.update(uri, values, selection, null);
	}
	
	private Cursor doQueryCursor(long serial, Query query,
			Class<? extends DatabaseObject> projectionClass) {
		if (query == null) {
			return null;
		}
		
		if (checkProviderPreparation() == false) {
			return null;
		}
		
		Uri uri = ProviderUriBuilder.buildQueryUri(mAuthority, mObjectClass, 
				getDatabaseVersion(), serial);
		if (uri == null) {
			return null;
		}
		
		String selection = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		String limit = null;
		
		QueryToken selToken = query.getSelection();
		if (selToken != null) {
			selection = selToken.toString();
		}
		
		QueryToken groupByToken = query.getGroupBy();
		if (groupByToken != null) {
			groupBy = groupByToken.toString();
		}
		
		QueryToken havingToken = query.getHaving();
		if (havingToken != null) {
			having = havingToken.toString();
		}
		
		QueryToken orderByToken = query.getOrderBy();
		if (orderByToken != null) {
			orderBy = orderByToken.toString();
		}
		
		QueryToken limitToken = query.getLimit();
		if (limitToken != null) {
			limit = limitToken.toString();
		}
		
		String sortOrder = SortOrderEncoder.encode(
				groupBy, having, orderBy, limit);
		
		String projection[] = createProjection(projectionClass);
		
		Cursor c = null;
		try {
			c = mContentResovler.query(uri, projection, selection, null, sortOrder);
		} catch (Exception e) {
			Logger.warnning("content resolver failure: %s", e.toString());
		}
		
		if (c == null || c.getCount() <= 0) {
			if (c != null) {
				c.close();
				
				closeOpenedDatabase(serial);
			}
			
			return null;
		}
		
		return c;
	}

	@Override
	protected Cursor onQueryCursor(Query query,
			Class<? extends DatabaseObject> projectionClass) {
		final long serial = System.currentTimeMillis();

		Cursor c = doQueryCursor(serial, query, projectionClass);
		
		/*
		 * XXX: we must return an fake empty cursor,
		 * 		or CursorLoader will failed to register conn
		 */
		if (c == null) {
			c = makeMockEmptyCursor(serial);
		}
		
		return c;
	}
	
	private Cursor makeMockEmptyCursor(long serial) {
		Uri uri = ProviderUriBuilder.buildQueryUri(mAuthority, mObjectClass, 
				getDatabaseVersion(), serial);
		if (uri == null) {
			return null;
		}

		Cursor c = new MockEmptyCursor();
		
		c.setNotificationUri(mContentResovler, uri);
		
		return c;
	}

	@Override
	protected List<DatabaseObject> onQuery(Query query,
			Class<? extends DatabaseObject> projectionClass) {
		final long serial = System.currentTimeMillis();
		
//		Logger.debug("OPEN DB: serial = %d", serial);
		Cursor c = doQueryCursor(serial, query, projectionClass);
		if (c == null) {
			return null;
		}
		
		List<DatabaseObject> objects = new ArrayList<DatabaseObject>();
		DatabaseObject object = null;
		
		try {
			if (c.moveToFirst()) {
				do {
					object = fromCursor(c, projectionClass);
					if (object != null) {
						object.fillValuesFromCursor(c);
						
						objects.add(object);
					}
				} while (c.moveToNext());
			}
		} finally {
			
		}
		
		c.close();

		closeOpenedDatabase(serial);
		
		return objects;
	}

	private void closeOpenedDatabase(long serial) {
		if (serial < 0) {
			return;
		}
		
		Intent i = new Intent(OpenedDatabaseCloseReceiver.ACTION_CLOSE_DATABASE);
		
		i.putExtra(OpenedDatabaseCloseReceiver.EXTRA_SERIAL, serial);
		
		mContext.sendBroadcast(i);
//		Logger.debug("CLOSE DB: serial = %d", serial);
	}

	@Override
	public DatabaseUpdateInfo checkUpdates() {
		if (checkProviderPreparation() == false) {
			return null;
		}
		
		Uri uri = ProviderUriBuilder.buildCommandUri(
				mAuthority, mObjectClass, 
				getDatabaseVersion(),
				GetUpdateInfoCmdCursor.COMMAND_NAME);
		if (uri == null) {
			return null;
		}
		
		Cursor c = null;
		try {
			c = mContentResovler.query(uri, null, null, null, null);
		} catch (Exception e) {
			Logger.warnning("content resolver failure: %s", e.toString());
		}
		
		if (c == null || c.getCount() <= 0) {
			if (c != null) {
				c.close();
			}
			
			return null;
		}

		if (c.moveToFirst() == false) {
			c.close();
			
			return null;
		}
		
		int newVer = DatabaseObject.VERSION_START;
		int oldVer = DatabaseObject.VERSION_START;
		
		try {
			newVer = c.getInt(c.getColumnIndex(GetUpdateInfoCmdCursor.COLUMN_NEW_VERSION));
			oldVer = c.getInt(c.getColumnIndex(GetUpdateInfoCmdCursor.COLUMN_OLD_VERSION));
		} catch (Exception e) {
			Logger.warnning("database failure: %s", e.toString());
		}
		
		c.close();

		return new DatabaseUpdateInfo(newVer, oldVer);
	}

	public Uri getDatabaseObserverUri() {
		final String database = DatabaseObject.classToDatabase(mObjectClass);
		final String table = DatabaseObject.classToTable(mObjectClass);

		Uri resUri = ProviderUriBuilder.buildResultUri(
				mAuthority, database, getDatabaseVersion(), table);
		
		return resUri;
	}

}
