package com.dailystudio.app.dataobject.loader;

import com.dailystudio.dataobject.DatabaseObject;

import android.content.Context;

public abstract class DatabaseObjectsLoader<D extends DatabaseObject> 
	extends ProjectedDatabaseObjectsLoader<D, D> {

	public DatabaseObjectsLoader(Context context) {
		super(context);
	}
	
}
