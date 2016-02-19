package com.dailystudio.app.dataobject.asynctask;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;

public abstract class ProjectedDatabaseObjectsAsyncTask<D extends DatabaseObject, P extends DatabaseObject> 
	extends ConvertedDatabaseObjectsAsyncTask<D, P, P> {

	public ProjectedDatabaseObjectsAsyncTask(Context context) {
		super(context);
	}

}
