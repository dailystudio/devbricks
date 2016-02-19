package com.dailystudio.app.dataobject;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.Query;

public class QueryBuilder {
	
	private Class<? extends DatabaseObject> mObjectClass;

	public QueryBuilder (Class<? extends DatabaseObject> klass) {
		mObjectClass = klass;
	}

	public Query getQuery() {
		if (mObjectClass == null) {
			return null;
		}
		
		return new Query(mObjectClass);
	}
	
}
