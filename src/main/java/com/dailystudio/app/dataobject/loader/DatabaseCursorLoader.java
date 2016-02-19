package com.dailystudio.app.dataobject.loader;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.Query;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

public abstract class DatabaseCursorLoader extends CursorLoader {
	
    final ForceLoadContentObserver mObserver;

	public DatabaseCursorLoader(Context context) {
		super(context);
		
		mObserver = new ForceLoadContentObserver();
	}
	
	@Override
	public Cursor loadInBackground() {
		final Class<? extends DatabaseObject> objectClass = getObjectClass();
		if (objectClass == null) {
			return null;
		}
		
		final DatabaseConnectivity connectivity = 
			getDatabaseConnectivity(objectClass);
		
		final Query query = getQuery(objectClass);
		if (query == null) {
			return null;
		}
		
		final Class<? extends DatabaseObject> projectionClass =
			getProjectionClass();
		
		Cursor cursor = null;
		if (projectionClass == null) {
			cursor = connectivity.queryCursor(query);
		} else {
			cursor = connectivity.queryCursor(query, projectionClass);
		}
		
        if (cursor != null) {
            cursor.getCount();
            cursor.registerContentObserver(mObserver);
        }

        return cursor;
	}
	
	protected DatabaseConnectivity getDatabaseConnectivity(
			Class<? extends DatabaseObject> objectClass) {
		return new DatabaseConnectivity(getContext(), objectClass);
	}
	
	protected Query getQuery(Class<? extends DatabaseObject> klass) {
		return new Query(klass);
	}
	
	protected Class<? extends DatabaseObject> getProjectionClass() {
		return null;
	}
	
	abstract protected Class<? extends DatabaseObject> getObjectClass();

}
