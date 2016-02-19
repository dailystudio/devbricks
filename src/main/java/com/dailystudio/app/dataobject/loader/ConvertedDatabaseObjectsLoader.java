package com.dailystudio.app.dataobject.loader;

import java.util.List;

import com.dailystudio.app.loader.AbsAsyncDataLoader;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.Query;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

public abstract class ConvertedDatabaseObjectsLoader<D extends DatabaseObject, P extends DatabaseObject, C extends DatabaseObject> 
	extends AbsAsyncDataLoader<List<C>> {

    final ForceLoadContentObserver mObserver = new ForceLoadContentObserver();

	public ConvertedDatabaseObjectsLoader(Context context) {
		super(context);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<C> loadInBackground() {
		if (mObserver != null) {
			getContext().getContentResolver().unregisterContentObserver(mObserver);
		}
		
		final Class<D> objectClass = getObjectClass();
		if (objectClass == null) {
			return null;
		}
		
		final DatabaseConnectivity connectivity = 
			getDatabaseConnectivity(objectClass);
		if (connectivity == null) {
			return null;
		}
				
		final Query query = getQuery(objectClass);
		if (query == null) {
			return null;
		}
		
		final Class<? extends DatabaseObject> projectionClass =
			getProjectionClass();
	
		List<DatabaseObject> data = null;
		if (projectionClass == null) {
			data = connectivity.query(query);
		} else {
			data = connectivity.query(query, projectionClass);
		}
		
		Uri uri = connectivity.getDatabaseObserverUri();
		if (uri != null) {
			ContentResolver cr = getContext().getContentResolver();
			
			cr.registerContentObserver(uri, true, mObserver);
		}
		
		return (List<C>)data;
	}
	
	protected DatabaseConnectivity getDatabaseConnectivity(
			Class<? extends DatabaseObject> objectClass) {
		return new DatabaseConnectivity(getContext(), objectClass);
	}
	
	protected Query getQuery(Class<D> klass) {
		return new Query(klass);
	}
	
	protected Class<P> getProjectionClass() {
		return null;
	}

	abstract protected Class<D> getObjectClass();
	
}
