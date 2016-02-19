package com.dailystudio.dataobject.database;

import com.dailystudio.development.Logger;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseConnectivityProvider extends ContentProvider {

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
//		Logger.debug("uri = %s", uri);
		if (uri == null || values == null) {
			return null;
		}
		
		Context context = getContext();
		if (context == null) {
			return null;
		}
		
		String authority = uri.getAuthority();
		if (authority == null) {
			return null;
		}
		
		final ProviderQueryUriParser parser = new ProviderQueryUriParser(uri);
		
		String database = parser.getDatabase();
		int version = parser.getVersion();
		String table = parser.getTable();
		String createTableSQL = parser.getCreateTableSQL();
		
		if (database == null 
				|| table == null
				|| version <= 0
				|| createTableSQL == null) {
			return null;
		}
		
		DatabaseOpenHandler handler = new DatabaseOpenHandler(
				context, database, version);
		
		final SQLiteDatabase db = handler.getWritableDatabase();
		if (db == null) {
			return null;
		}
		
		if (checkOrCreateTable(db, table, createTableSQL) == false) {
			db.close();
			
			return null;
		}
		
		long rowId = -1;
		try {
			db.beginTransaction();
			
			rowId = db.insert(table, null, values);
			
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
			return null;
		}
		
		Uri resUri = ProviderUriBuilder.buildResultUri(
				authority, database, version, table, rowId);
		if (resUri != null) {
			final ContentResolver cr = context.getContentResolver();
			if (cr != null) {
				cr.notifyChange(resUri, null);
			}
		}
//		Logger.debug("resUri = %s", resUri);
		
		return resUri;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		if (uri == null || values == null) {
			return 0;
		}
		
		final int count = values.length;
		if (count <= 0) {
			return 0;
		}
		
		Context context = getContext();
		if (context == null) {
			return 0;
		}
		
		String authority = uri.getAuthority();
		if (authority == null) {
			return 0;
		}
		
		final ProviderQueryUriParser parser = new ProviderQueryUriParser(uri);
		
		String database = parser.getDatabase();
		int version = parser.getVersion();
		String table = parser.getTable();
		String createTableSQL = parser.getCreateTableSQL();
		
		if (database == null 
				|| table == null
				|| version <= 0
				|| createTableSQL == null) {
			return 0;
		}
		
		DatabaseOpenHandler handler = new DatabaseOpenHandler(
				context, database, version);
		
		final SQLiteDatabase db = handler.getWritableDatabase();
		if (db == null) {
			return 0;
		}
		
		if (checkOrCreateTable(db, table, createTableSQL) == false) {
			db.close();
			
			return 0;
		}
		
		int successful = 0;

		try {
			db.beginTransaction();

			long rowId = -1;

			for (int i = 0; i < count; i++) {
				rowId = db.insert(table, null, values[i]);
				
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
		
		Uri resUri = ProviderUriBuilder.buildResultUri(
				authority, database, version, table);
		if (resUri != null) {
			final ContentResolver cr = context.getContentResolver();
			if (cr != null) {
				cr.notifyChange(resUri, null);
			}
		}
		
		return successful;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
//		Logger.debug("uri = %s", uri);
		if (uri == null) {
			return 0;
		}
		
		Context context = getContext();
		if (context == null) {
			return 0;
		}
		
		String authority = uri.getAuthority();
		if (authority == null) {
			return 0;
		}
		
		final ProviderQueryUriParser parser = new ProviderQueryUriParser(uri);
		
		String database = parser.getDatabase();
		int version = parser.getVersion();
		String table = parser.getTable();
		
		if (database == null 
				|| table == null
				|| version <= 0) {
			return 0;
		}
		
		DatabaseOpenHandler handler = new DatabaseOpenHandler(
				context, database, version);
		
		final SQLiteDatabase db = handler.getWritableDatabase();
		if (db == null) {
			return 0;
		}

		int affected = -1;
		
		try {
			db.beginTransaction();

			affected = db.delete(table, selection, selectionArgs);

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
		
		Uri resUri = ProviderUriBuilder.buildResultUri(
				authority, database, version, table);
		if (resUri != null) {
			final ContentResolver cr = context.getContentResolver();
			if (cr != null) {
				cr.notifyChange(resUri, null);
			}
		}
//		Logger.debug("resUri = %s", resUri);
		
		return affected;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
//		Logger.debug("uri = %s", uri);
		if (uri == null || values == null) {
			return 0;
		}
		
		Context context = getContext();
		if (context == null) {
			return 0;
		}
		
		String authority = uri.getAuthority();
		if (authority == null) {
			return 0;
		}
		
		final ProviderQueryUriParser parser = new ProviderQueryUriParser(uri);
		
		String database = parser.getDatabase();
		int version = parser.getVersion();
		String table = parser.getTable();
		String createTableSQL = parser.getCreateTableSQL();
		
		if (database == null 
				|| table == null
				|| version <= 0
				|| createTableSQL == null) {
			return 0;
		}
		
		DatabaseOpenHandler handler = new DatabaseOpenHandler(
				context, database, version);
		
		final SQLiteDatabase db = handler.getWritableDatabase();
		if (db == null) {
			return 0;
		}
		
		if (checkOrCreateTable(db, table, createTableSQL) == false) {
			db.close();
			
			return 0;
		}
		
		int affected = -1;
		
		try {
			db.beginTransaction();

			affected = db.update(table, values, selection, selectionArgs);
			
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
		
		Uri resUri = ProviderUriBuilder.buildResultUri(
				authority, database, version, table);
		if (resUri != null) {
			final ContentResolver cr = context.getContentResolver();
			if (cr != null) {
				cr.notifyChange(resUri, null);
			}
		}
//		Logger.debug("resUri = %s", resUri);
		
		return affected;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (uri == null) {
			return null;
		}
		
		final ProviderUriParser parser = new ProviderUriParser(uri);
		
		final String base = parser.getBase();
		if (ProviderCommandUriParser.BASE_COMMAND.equals(base)) {
			return queryCommands(uri, projection, selection, selectionArgs, sortOrder);
		} else if (ProviderQueryUriParser.BASE_QUERY.equals(base)) {
			return queryObjects(uri, projection, selection, selectionArgs, sortOrder);
		}
		
		return null;
	}
	
	protected Cursor queryCommands(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Context context = getContext();
		if (context == null) {
			return null;
		}
		
		String authority = uri.getAuthority();
		if (authority == null) {
			return null;
		}
		
//		Logger.debug("uri: (%s)", uri);
//		Logger.debug("authority: (%s)", authority);
		
		final ProviderCommandUriParser parser = new ProviderCommandUriParser(uri);
		
		String database = parser.getDatabase();
		String table = parser.getTable();
		int version = parser.getVersion();
		String command = parser.getCommand();
//		Logger.debug("database: (%s)", database);
//		Logger.debug("table: (%s)", table);
//		Logger.debug("version: (%d)", version);
//		Logger.debug("command: (%s)", command);
		
		if (database == null 
				|| table == null
				|| version <= 0
				|| command == null) {
			return null;
		}
		
		if (GetUpdateInfoCmdCursor.COMMAND_NAME.equals(command)) {
			return doCommandGetUpdateInfo(context, database, table, version);
		}

		return null;
	}
	
	private Cursor doCommandGetUpdateInfo(Context context,
			String database, String table, int version) {
		if (database == null 
				|| table == null
				|| version <= 0) {
			return null;
		}
		
		DatabaseOpenHandler handler = new DatabaseOpenHandler(
				context, database, version);
		
		SQLiteDatabase db = handler.getWritableDatabase();
		if (db == null) {
			return null;
		}
		
		db.close();
		
		final int newVersion = handler.getNewVersion();
		final int oldVersion = handler.getOldVersion();

		CommandCursor cursor = new GetUpdateInfoCmdCursor();
		
		cursor.putValue(GetUpdateInfoCmdCursor.COLUMN_NEW_VERSION, newVersion);
		cursor.putValue(GetUpdateInfoCmdCursor.COLUMN_OLD_VERSION, oldVersion);
		
		return cursor;
	}
	
	protected Cursor queryObjects(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		if (uri == null) {
			return null;
		}
		
		Context context = getContext();
		if (context == null) {
			return null;
		}
		
		String authority = uri.getAuthority();
		if (authority == null) {
			return null;
		}
		
//		Logger.debug("uri: (%s)", uri);
//		Logger.debug("authority: (%s)", authority);
		
		final ProviderQueryUriParser parser = new ProviderQueryUriParser(uri);
		
		String database = parser.getDatabase();
		int version = parser.getVersion();
		String table = parser.getTable();
		long serial = parser.getSerial();
//		Logger.debug("database: (%s)", database);
//		Logger.debug("table: (%s)", table);
		
		if (database == null 
				|| table == null
				|| version <= 0) {
			return null;
		}
		
		DatabaseOpenHandler handler = new DatabaseOpenHandler(
				context, database, version);
		
		final SQLiteDatabase db = handler.getReadableDatabase();
		if (db == null) {
			return null;
		}
		
		String groupBy = null;
		String having = null;
		String orderBy = null;
		String limit = null;

		String[] decode = SortOrderEncoder.decode(sortOrder);
		if (decode != null) {
			groupBy = decode[0];
			having = decode[1];
			orderBy = decode[2];
			limit = decode[3];
		}
		
//		Logger.debug("selection: (%s)", selection);
//		Logger.debug("groupBy: (%s)", groupBy);
//		Logger.debug("having: (%s)", having);
//		Logger.debug("orderBy: (%s)", orderBy);
//		Logger.debug("limit: (%s)", limit);
		
		Cursor c = null;
		
		try {
			db.beginTransaction();

			c = db.query(table, projection, selection, selectionArgs, 
					groupBy, having, orderBy, limit);
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
		
		manageOpenedDatabase(serial, db);
		
		ContentResolver cr = context.getContentResolver();
		
		if (cr != null) {
			c.setNotificationUri(cr, uri);
		}
		
		return c;
	}
	
	private void manageOpenedDatabase(long serial, SQLiteDatabase database) {
		if (serial < 0 || database == null) {
			return;
		}
		
		OpenedDatabaseManager odbmgr = OpenedDatabaseManager.getInstance();
		if (odbmgr == null) {
			return;
		}
		
		OpenedDatabase odb = new OpenedDatabase(serial, database);
		
		odbmgr.addObject(odb);
	}
	
	protected boolean checkOrCreateTable(SQLiteDatabase db, String table, String createTableSQL) {
		if (db == null || db.isReadOnly() 
				|| table == null || createTableSQL == null) {
			return false;
		}

		boolean success = false;
		try {
	        db.execSQL(createTableSQL);
	        success = true;
		} catch (Exception e) {
			Logger.warnning("database failure: %s", e.toString());
			success = false;
		}
		
		return success;
	}

}
