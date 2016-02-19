package com.dailystudio.app.dataobject.loader;

import com.dailystudio.dataobject.DatabaseObject;

import android.content.Context;

public abstract class ProjectedDatabaseObjectsLoader<D extends DatabaseObject, P extends DatabaseObject> 
	extends ConvertedDatabaseObjectsLoader<D, P, P> {

	public ProjectedDatabaseObjectsLoader(Context context) {
		super(context);
	}

}
