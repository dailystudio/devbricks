package com.dailystudio.dataobject.database;

import com.dailystudio.development.Logger;
import com.dailystudio.manager.ISingletonObject;
import com.dailystudio.manager.Manager;
import com.dailystudio.manager.SingletonManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

class DatabaseOpenHandler extends SQLiteOpenHelper {

	private static class DatabaseHandlerInstance implements ISingletonObject<String> {

		String dbName;
		int dbVer;
		DatabaseOpenHandler dbHandler;

		private DatabaseHandlerInstance(String db, int ver, DatabaseOpenHandler handler) {
			dbName = db;
			dbVer = ver;
			dbHandler= handler;
		}

		@Override
		public String toString() {
			return String.format("%s(0x%08x): db = %s, ver = %d, handler = %s",
					getClass().getSimpleName(),
					hashCode(),
					dbName, dbVer, dbHandler);
		}

		@Override
		public String getSingletonKey() {
			return dbName;
		}

	}

	public static class DatabaseHandlerInstanceManager
			extends SingletonManager<String,DatabaseHandlerInstance> {

		public static synchronized DatabaseHandlerInstanceManager getInstance() {
			return Manager.getInstance(DatabaseHandlerInstanceManager.class);
		}

		@Override
		public void addObject(DatabaseHandlerInstance object) {
			super.addObject(object);

			Logger.debug("add new help: %s", object);
		}
	}

	private static final int RETRY_TIMES = 3;
	private static final int RETRY_INTERVAL = 500;
	
	private int mCurrentVersion;
	private int mOldVersion = -1;

	public static synchronized DatabaseOpenHandler getInstance(Context context, String name, int version) {
		if (context == null || TextUtils.isEmpty(name)) {
			return null;
		}

		final Context appContext = context.getApplicationContext();

		DatabaseHandlerInstanceManager dbhiMgr =
				DatabaseHandlerInstanceManager.getInstance();

		DatabaseOpenHandler handler = null;

		DatabaseHandlerInstance instance = dbhiMgr.getObject(name);
		if (instance == null) {
			handler = new DatabaseOpenHandler(appContext,
							name, version);

			instance = new DatabaseHandlerInstance(name, version, handler);

			dbhiMgr.addObject(instance);
		} else {
			handler = instance.dbHandler;

			if (instance.dbVer != version) {
				handler.close();
				handler = new DatabaseOpenHandler(appContext,
						name, version);

				instance.dbVer = version;
				instance.dbHandler = handler;
			}
		}

		return handler;
	}
	
	private DatabaseOpenHandler(Context context, String name, int version) {
		super(context, name, null, version);
		
		mCurrentVersion = version;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		mCurrentVersion = newVersion;
		mOldVersion = oldVersion;
	}
	
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * XXX: this interface is imported in Android 4.0(ICS)
		 * 		and default impl is:
		 *        throw new SQLiteException("Can't downgrade database from version " +
         *           oldVersion + " to " + newVersion);
		 *  	so here we need to impl it without @Override annotation for the
		 *  	compatibility with Gingerbread.
		 */
		mCurrentVersion = newVersion;
		mOldVersion = oldVersion;
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		SQLiteDatabase db = null;
		int retry = 0;
		
		do {
			try {
				db = super.getWritableDatabase();
			} catch (SQLiteException e) {
				Logger.warn("database failure: %s", e.toString());
				db = null;
			}
			
			if (db != null) {
				break;
			}
			
			retry++;
			Logger.debug("open database failed. retry = %d / %d", retry, RETRY_TIMES);
				
			try {
				Thread.sleep(RETRY_INTERVAL);
			} catch (InterruptedException e) {
				Logger.warn("Interrupted from Thread.sleep(): %s", e.toString());
			}
		} while (retry < RETRY_TIMES);
		
		return db;
	}
	
	public int getNewVersion() {
		return mCurrentVersion;
	}
	
	public int getOldVersion() {
		if (mOldVersion == -1) {
			return mCurrentVersion;
		}

		return mOldVersion;
	}

}
