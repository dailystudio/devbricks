package com.dailystudio.app.dataobject.asynctask;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;

public abstract class DatabaseObjectsAsyncTask<D extends DatabaseObject, P extends DatabaseObject, C extends DatabaseObject> 
	extends ProjectedDatabaseObjectsAsyncTask<D, D> {

	public DatabaseObjectsAsyncTask(Context context) {
		super(context);
	}

}
