package com.dailystudio.dataobject.database;

import java.util.List;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DatabaseObjectCopier;
import com.dailystudio.dataobject.DatabaseObjectFactory;
import com.dailystudio.dataobject.query.Query;

public abstract class AbsDatabaseUpdater {
	
	protected Context mContext;
	
	protected Class<? extends DatabaseObject> mObjectClass;
	
	public AbsDatabaseUpdater(Context context, 
			Class<? extends DatabaseObject> objectClass) {
		mContext = context;
		mObjectClass = objectClass;
	}
	
	public void doUpdate(int newVersion, int oldVersion) {
		if (mContext == null || mObjectClass == null) {
			return;
		}
		
		AbsDatabaseConnectivity connectivity = null;
		
		connectivity = getConnectivity(oldVersion);
		if (connectivity == null) {
			return;
		}
		
		Query query = new Query(mObjectClass);
		
		List<DatabaseObject> oldObjects = connectivity.query(query);
		
		connectivity.delete(query);
		
		if (oldObjects == null) {
			return;
		}
		
		final int N = oldObjects.size();
		if (N <= 0) {
			return;
		}
		
		DatabaseObject newObjects[] = new DatabaseObject[N];

		DatabaseObject oldObj = null;
		DatabaseObject newObj = null;
		for (int i = 0; i < N; i++) {
			oldObj = oldObjects.get(i);
			
			newObj = DatabaseObjectFactory.createDatabaseObject(
					mObjectClass, newVersion);
			
			updateObject(newObj, oldObj);
			
			newObjects[i] = newObj;
		}
		
		connectivity = getConnectivity(newVersion);
		if (connectivity == null) {
			return;
		}

		connectivity.insert(newObjects);
	}
	
	protected void updateObject(DatabaseObject newObject, DatabaseObject oldObject) {
		if (newObject == null || oldObject == null) {
			return;
		}

		DatabaseObjectCopier copier = new DatabaseObjectCopier();
		
		copier.fillObject(newObject, oldObject);
	}
	
	abstract protected AbsDatabaseConnectivity getConnectivity(int version);
	
}
