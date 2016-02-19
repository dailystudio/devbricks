package com.dailystudio.dataobject.database;

import com.dailystudio.manager.ISingletonObject;

import android.database.sqlite.SQLiteDatabase;

class OpenedDatabase implements ISingletonObject<Long> {

	private long mSerial;
	private SQLiteDatabase mDatabase;
	
	public OpenedDatabase(long serial, SQLiteDatabase db) {
		mSerial = serial;
		mDatabase = db;
	}
	
	public void close() {
		if (mDatabase == null) {
			return;
		}
		
		if (!mDatabase.isOpen()) {
			return;
		}
		
		mDatabase.close();
	}

	@Override
	public Long getSingletonKey() {
		return mSerial;
	}
	
}
