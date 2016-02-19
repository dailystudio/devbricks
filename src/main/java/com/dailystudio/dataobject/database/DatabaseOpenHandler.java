package com.dailystudio.dataobject.database;

import com.dailystudio.development.Logger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseOpenHandler extends SQLiteOpenHelper {

	private static final int RETRY_TIMES = 3;
	private static final int RETRY_INTERVAL = 500;
	
	private int mCurrentVersion;
	private int mOldVersion = -1;
	
	public DatabaseOpenHandler(Context context, String name, int version) {
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
				Logger.warnning("database failure: %s", e.toString());
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
				Logger.warnning("Interrupted from Thread.sleep(): %s", e.toString());
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
