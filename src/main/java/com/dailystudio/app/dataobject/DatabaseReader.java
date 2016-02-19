package com.dailystudio.app.dataobject;

import java.util.List;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;

public class DatabaseReader<T extends DatabaseObject> extends BaseDatabaseStream<T> {

	public DatabaseReader(Context context, 
			Class<T> objectClass) {
		this(context, null, objectClass);
	}
	
	public DatabaseReader(Context context, 
			String authority,
			Class<T> objectClass) {
		this(context, authority, objectClass, DatabaseObject.VERSION_LATEST);
	}
	
	public DatabaseReader(Context context, 
			Class<T> objectClass,
			int version) {
		this(context, null, objectClass, version);
	}

	public DatabaseReader(Context context,
			String authority, Class<T> klass, int version) {
		super(context, authority, klass, version);
	}
	
	protected QueryBuilder onCreateQueryBuilder(Class<T> klass) {
		return new QueryBuilder(klass);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> query(Query query) {
		final DatabaseConnectivity connectivity = getConnectivity();
		if (connectivity == null) {
			return null;
		}
		
		List<DatabaseObject> objects = 
			connectivity.query(query, null);
		if (objects == null || objects.size() < 0) {
			return null;
		}
		
		return (List<T>)objects;
	}
	
	public List<DatabaseObject> query(Query query,
			Class<? extends DatabaseObject> projectionClass) {
		final DatabaseConnectivity connectivity = getConnectivity();
		if (connectivity == null) {
			return null;
		}
		
		return connectivity.query(query, projectionClass);
	}
	
	public List<T> queryTopN(Query query, int topN) {
		if (query == null) {
			return null;
		}
		
		ExpressionToken limitToken = new ExpressionToken(topN);
		if (limitToken != null) {
			query.setLimit(limitToken);
		}
	
		return query(query);
	}
	
	public T queryLastOne(Query query) {
		if (query == null) {
			return null;
		}
		
		List<T>  objects = queryTopN(query, 1);
		if (objects == null || objects.size() <= 0) {
			return null;
		}
		
		return objects.get(0);
	}
	
	public long queryCount(Query query) {
		if (query == null) {
			return 0l;
		}
		
		List<DatabaseObject> objects = query(query, CountObject.class);
		if (objects == null || objects.size() <= 0) {
			return 0l;
		}
		
		DatabaseObject object0 = objects.get(0);
		if (object0 instanceof CountObject == false) {
			return 0l;
		}
		
		return ((CountObject)object0).getCount();
	}
	
}
