package com.dailystudio.dataobject.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.dataobject.query.QueryToken;
import com.dailystudio.development.Logger;

class DatabaseConnectivityDirectSQLiteImpl extends
		AbsDatabaseConnectivity {

	private DatabaseOpenHandler mOpenHandler = null;

	public DatabaseConnectivityDirectSQLiteImpl(Context context, 
			Class<? extends DatabaseObject> objectClass) {
		this(context, objectClass, DatabaseObject.VERSION_LATEST);
	}

	public DatabaseConnectivityDirectSQLiteImpl(Context context, 
			Class<? extends DatabaseObject> objectClass,
			int version) {
		super(context, objectClass, version);
		
		if (mObjectClass != null) {
			String database = DatabaseObject.classToDatabase(mObjectClass);
			if (database != null) {
				int dbver = getDatabaseVersion();
				
				mOpenHandler = new DatabaseOpenHandler(context, database, dbver);
			}
		}
	}

	@Override
	protected long onInsert(DatabaseObject object) {
		if (object == null || object.isEmpty()) {
			return 0;
		}
		
		if (mOpenHandler == null) {
			return 0;
		}
		
		final SQLiteDatabase db = mOpenHandler.getWritableDatabase();
		if (db == null) {
			return 0;
		}
		
		final ContentValues values = object.getValues();
		if (values == null) {
			db.close();
			
			return 0;
		}
		
		final Template template = object.getTemplate();
		if (template == null) {
			db.close();
			
			return 0;
		}
		
		final String table = DatabaseObject.classToTable(object.getClass());
		if (table == null) {
			db.close();
			
			return 0;
		}
		
		if (checkOrCreateTable(db, table, object) == false) {
			db.close();
			
			return 0;
		}
		
		long rowId = -1;
		try {
			db.beginTransaction();

			rowId = db.insert(table, object.handleNullProjection(), values);
			
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Logger.warnning("database failure: %s", e.toString());
		} catch (IllegalStateException e){
			Logger.warnning("database failure: %s", e.toString());
		} finally {
			try {
				db.endTransaction();
			} catch (Exception e) {
				Logger.warnning("database failure: %s", e.toString());
			}
		}
		
		db.close();
		
		if (rowId <= 0) {
			return 0;
		}
		
		return rowId;
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
		
		if (mOpenHandler == null) {
			return 0;
		}
		
		final SQLiteDatabase db = mOpenHandler.getWritableDatabase();
		if (db == null) {
			return 0;
		}
		
		int successful = 0;

		try {
			db.beginTransaction();

			ContentValues values = null;
			DatabaseObject object = null; 
			Template template = null;
			String table = null;
			long rowId = -1;
			
			for (int i = 0; i < count; i++) {
				object = objects[i];
				if (object == null || object.isEmpty()) {
					continue;
				}
				
				values = object.getValues();
				if (values == null) {
					continue;
				}
				
				template = object.getTemplate();
				if (template == null) {
					continue;
				}
				
				table = DatabaseObject.classToTable(object.getClass());
				if (table == null) {
					continue;
				}

				if (checkOrCreateTable(db, table, object) == false) {
					continue;
				}

				rowId = db.insert(table, object.handleNullProjection(), values);
				
				if (rowId <= 0) {
					continue;
				}

				successful++;
			}
			
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Logger.warnning("database failure: %s", e.toString());
		} catch (IllegalStateException e){
			Logger.warnning("database failure: %s", e.toString());
		} finally {
			try {
				db.endTransaction();
			} catch (Exception e) {
				Logger.warnning("database failure: %s", e.toString());
			}
		}
		
		db.close();
		
		return successful;
	}
	
	@Override
	protected int onDelete(Query query) {
		if (query == null) {
			return 0;
		}
		
		if (mOpenHandler == null) {
			return 0;
		}
		
		final SQLiteDatabase db = mOpenHandler.getWritableDatabase();
		if (db == null) {
			return 0;
		}
		
		
		final String table = DatabaseObject.classToTable(query.getObjectClass());
		if (table == null) {
			db.close();
			
			return 0;
		}
		
		String selection = null;
		
		QueryToken selToken = query.getSelection();
		if (selToken != null) {
			selection = selToken.toString();
		}
		
		int affected = -1;
		try {
			db.beginTransaction();

			affected = db.delete(table, selection, null);
			
			if (selection == null) {
				db.execSQL(String.format("DROP TABLE %s", table));
			}
			
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Logger.warnning("database failure: %s", e.toString());
		} catch (IllegalStateException e){
			Logger.warnning("database failure: %s", e.toString());
		} finally {
			try {
				db.endTransaction();
			} catch (Exception e) {
				Logger.warnning("database failure: %s", e.toString());
			}
		}
		
		db.close();
		
		if (affected <= 0) {
			return 0;
		}
		
		return affected;
	}

	@Override
	protected int onUpdate(Query query, DatabaseObject object) {
		if (query == null || object == null || object.isEmpty()) {
			return 0;
		}
		
		if (mOpenHandler == null) {
			return 0;
		}
		
		final SQLiteDatabase db = mOpenHandler.getWritableDatabase();
		if (db == null) {
			return 0;
		}
		
		
		final ContentValues values = object.getValues();
		if (values == null) {
			db.close();
			
			return 0;
		}
		
		final Template template = object.getTemplate();
		if (template == null) {
			db.close();
			
			return 0;
		}
		
		final String table = DatabaseObject.classToTable(object.getClass());
		if (table == null) {
			db.close();
			
			return 0;
		}
		
		if (checkOrCreateTable(db, table, object) == false) {
			db.close();
			
			return 0;
		}
		
		String selection = null;
		
		QueryToken selToken = query.getSelection();
		if (selToken != null) {
			selection = selToken.toString();
		}
		
		int affected = -1;
		try {
			db.beginTransaction();

			affected = db.update(table, object.getValues(), selection, null);
			
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Logger.warnning("database failure: %s", e.toString());
		} finally {
			try {
				db.endTransaction();
			} catch (Exception e) {
				Logger.warnning("database failure: %s", e.toString());
			}
		}
		
		db.close();
		
		if (affected <= 0) {
			return 0;
		}
		
		return affected;
	}

	private Cursor doQuery(SQLiteDatabase db, Query query,
			Class<? extends DatabaseObject> projectionClass) {
		if (db == null || query == null) {
			return null;
		}
		
		final Class<? extends DatabaseObject> objectClass = query.getObjectClass();
		if (objectClass == null) {
			db.close();
			
			return null;
		}
		
		String table = DatabaseObject.classToTable(objectClass);
		if (table == null) {
			db.close();
			
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
		
		String projection[] = createProjection(projectionClass);
		Logger.debug("selection: (%s)", selection);
		Logger.debug("groupBy: (%s)", groupBy);
		Logger.debug("having: (%s)", having);
		Logger.debug("orderBy: (%s)", orderBy);
		Logger.debug("limit: (%s)", limit);
		
		Logger.debug("projection: (%s)", projectionToString(projection));
		
		Cursor c = null;
		
		try {
			db.beginTransaction();

			c = db.query(table, projection, selection, null, groupBy, having, orderBy, limit);

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Logger.warnning("database failure: %s", e.toString());
		} catch (IllegalStateException e){
			Logger.warnning("database failure: %s", e.toString());
		} finally {
			try {
				db.endTransaction();
			} catch (Exception e) {
				Logger.warnning("database failure: %s", e.toString());
			}
		}
		
		if (c == null || c.getCount() <= 0) {
			if (c != null) {
				c.close();
			}
			
			db.close();
			
			return null;
		}
		
		return c;
	}
	
	@Override
	protected Cursor onQueryCursor(Query query,
			Class<? extends DatabaseObject> projectionClass) {
		if (mOpenHandler == null) {
			return null;
		}
		
		final SQLiteDatabase db = mOpenHandler.getReadableDatabase();
		if (db == null) {
			return null;
		}
		
		return doQuery(db, query, projectionClass);
	}
	
	@Override
	protected List<DatabaseObject> onQuery(Query query,
			Class<? extends DatabaseObject> projectionClass) {
		if (mOpenHandler == null) {
			return null;
		}
		
		final SQLiteDatabase db = mOpenHandler.getReadableDatabase();
		if (db == null) {
			return null;
		}
		
		Cursor c = doQuery(db, query, projectionClass);
		if (c == null) {
			return null;
		}
		
		final Class<? extends DatabaseObject> objectClass = query.getObjectClass();
		if (objectClass == null) {
			return null;
		}
		
		List<DatabaseObject> objects = new ArrayList<DatabaseObject>();
		DatabaseObject object = null;
		
		try {
			if (c.moveToFirst()) {
				do {
					object = fromCursor(c, projectionClass);
					
					if (object != null) {
						objects.add(object);
					}
				} while (c.moveToNext());
			}
		} catch (SQLException e) {
			Logger.warnning("database failure: %s", e.toString());
		} catch (IllegalStateException e){
			Logger.warnning("database failure: %s", e.toString());
		} 
		
		c.close();
		
		db.close();
		
		return objects;
	}

	protected boolean checkOrCreateTable(SQLiteDatabase db, String tableName, DatabaseObject object) {
		if (db == null || db.isReadOnly() 
				|| tableName == null || object == null) {
			return false;
		}

		String creationSQL = object.toSQLTableCreationString();
		if (creationSQL == null) {
			return false;
		}
		
		boolean success = false;
		try {
	        db.execSQL(creationSQL);
	        success = true;
		} catch (Exception e) {
			Logger.warnning("database failure: %s", e.toString());
			success = false;
		}
		
		return success;
	}

	@Override
	public DatabaseUpdateInfo checkUpdates() {
		if (mOpenHandler == null) {
			return null;
		}
		
		SQLiteDatabase db = mOpenHandler.getWritableDatabase();
		if (db == null) {
			return null;
		}
		
		db.close();
		
		final int newVersion = mOpenHandler.getNewVersion();
		final int oldVersion = mOpenHandler.getOldVersion();
		
		DatabaseUpdateInfo info = new DatabaseUpdateInfo(newVersion, oldVersion);
		
		return info;
	}

}
