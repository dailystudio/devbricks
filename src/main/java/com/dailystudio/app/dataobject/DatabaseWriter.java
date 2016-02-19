package com.dailystudio.app.dataobject;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.Query;

import android.content.Context;

public class DatabaseWriter<T extends DatabaseObject> extends BaseDatabaseStream<T> {

	public DatabaseWriter(Context context, 
			Class<T> objectClass) {
		this(context, null, objectClass);
	}
	
	public DatabaseWriter(Context context, 
			String authority,
			Class<T> objectClass) {
		this(context, authority, objectClass, DatabaseObject.VERSION_LATEST);
	}
	
	public DatabaseWriter(Context context, 
			Class<T> objectClass,
			int version) {
		this(context, null, objectClass, version);
	}

	public DatabaseWriter(Context context, 
			String authority, Class<T> klass, int version) {
		super(context, authority, klass, version);
	}
		
	public long insert(T object) {
		final DatabaseConnectivity connectivity = getConnectivity();
		if (connectivity == null) {
			return 0l;
		}
		
		return connectivity.insert(object);
	}
	
	public int insert(T[] objects) {
		final DatabaseConnectivity connectivity = getConnectivity();
		if (connectivity == null) {
			return 0;
		}
		
		return connectivity.insert(objects);
	}
	
	public int update(Query query, T object) {
		final DatabaseConnectivity connectivity = getConnectivity();
		if (connectivity == null) {
			return 0;
		}
		
		return connectivity.update(query, object);
	}
	
	public int delete(Query query) {
		final DatabaseConnectivity connectivity = getConnectivity();
		if (connectivity == null) {
			return 0;
		}
		
		return connectivity.delete(query);
	}

}
