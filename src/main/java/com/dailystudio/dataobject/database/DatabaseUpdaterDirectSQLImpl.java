package com.dailystudio.dataobject.database;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;

abstract class DatabaseUpdaterDirectSQLImpl extends AbsDatabaseUpdater {
	
	public DatabaseUpdaterDirectSQLImpl(Context context,
			Class<? extends DatabaseObject> objectClass) {
		super(context, objectClass);
	}

	@Override
	protected AbsDatabaseConnectivity getConnectivity(int version) {
		return new DatabaseConnectivityDirectSQLiteImpl(
				mContext, mObjectClass, version);
	}

}
