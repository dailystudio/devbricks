package com.dailystudio.datetime.dataobject;

import android.content.Context;

import com.dailystudio.app.dataobject.DatabaseWriter;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;

public class TimeBasedDatabaseWriter<T extends TimeBasedDatabaseObject> extends DatabaseWriter<T> {

	public TimeBasedDatabaseWriter(Context context,
								   Class<T> objectClass) {
		this(context, null, objectClass);
	}
	
	public TimeBasedDatabaseWriter(Context context,
								   String authority,
								   Class<T> objectClass) {
		this(context, authority, objectClass, DatabaseObject.VERSION_LATEST);
	}
	
	public TimeBasedDatabaseWriter(Context context,
								   Class<T> objectClass,
								   int version) {
		this(context, null, objectClass, version);
	}

	public TimeBasedDatabaseWriter(Context context,
								   String authority, Class<T> klass, int version) {
		super(context, authority, klass, version);
	}
		
	public int update(T object) {
		if (object == null) {
			return 0;
		}
		
		final DatabaseConnectivity connectivity =
			getConnectivity();
		if (connectivity == null) {
			return 0;
		}
		
		Query query = getQuery();
		ExpressionToken token =
			TimeBasedDatabaseObject.COLUMN_ID.eq(object.getId());
		query.setSelection(token);

		return connectivity.update(query, object);
	}
	
	public int delete(T object) {
		if (object == null) {
			return 0;
		}
		
		final DatabaseConnectivity connectivity =
			getConnectivity();
		if (connectivity == null) {
			return 0;
		}
		
		Query query = getQuery();
		ExpressionToken token =
			TimeBasedDatabaseObject.COLUMN_ID.eq(object.getId());
		query.setSelection(token);

		return connectivity.delete(query);
	}

}
