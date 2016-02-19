package com.dailystudio.app.dataobject;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.Query;

class BaseDatabaseStream <T extends DatabaseObject> {

	private DatabaseConnectivity mConnectivity;
	protected QueryBuilder mQueryBuilder;
	
	public BaseDatabaseStream(Context context, 
			Class<T> objectClass) {
		this(context, null, objectClass);
	}
	
	public BaseDatabaseStream(Context context, 
			String authority,
			Class<T> objectClass) {
		this(context, authority, objectClass, DatabaseObject.VERSION_LATEST);
	}
	
	public BaseDatabaseStream(Context context, 
			Class<T> objectClass,
			int version) {
		this(context, null, objectClass, version);
	}

	public BaseDatabaseStream(Context context,
			String authority, Class<T> klass, int version) {
		mConnectivity = 
			new DatabaseConnectivity(context, authority, klass, version);
		
		mQueryBuilder = onCreateQueryBuilder(klass);
	}
	
	protected QueryBuilder onCreateQueryBuilder(Class<T> klass) {
		return new QueryBuilder(klass);
	}

	protected DatabaseConnectivity getConnectivity() {
		return mConnectivity;
	}
	
	public Query getQuery() {
		if (mQueryBuilder == null) {
			return null;
		}
		
		return mQueryBuilder.getQuery();
	}
	
}
