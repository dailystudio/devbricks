package com.dailystudio.dataobject.database;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;

public class DatabaseUpdater extends AbsDatabaseUpdater {
	
	public DatabaseUpdater(Context context,
			Class<? extends DatabaseObject> objectClass) {
		super(context, objectClass);
	}

	@Override
	protected AbsDatabaseConnectivity getConnectivity(int version) {
		return new DatabaseConnectivity(
				mContext, mObjectClass, version);
	}

}
