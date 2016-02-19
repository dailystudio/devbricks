package com.dailystudio.app.dataobject.asynctask;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.database.DatabaseConnectivity;
import com.dailystudio.dataobject.query.Query;

public abstract class ConvertedDatabaseObjectsAsyncTask<D extends DatabaseObject, P extends DatabaseObject, C extends DatabaseObject> 
	extends AsyncTask<Void, Void, List<C>> {

	private Context mContext;
	
	public ConvertedDatabaseObjectsAsyncTask(Context context) {
		mContext = context;
	}
	
	public Context getContext() {
		return mContext;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List<C> doInBackground(Void... params) {
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
		
		final Class<P> projectionClass =
			getProjectionClass();
	
		List<DatabaseObject> data = null;
		if (projectionClass == null) {
			data = connectivity.query(query);
		} else {
			data = connectivity.query(query, projectionClass);
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
