package com.dailystudio.dataobject.database;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DatabaseObjectFactory;
import com.dailystudio.dataobject.query.Query;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public abstract class AbsDatabaseConnectivity {
	
	protected Context mContext = null;
	
	protected Class<? extends DatabaseObject> mObjectClass = null;
	
	protected int mVersion = DatabaseObject.VERSION_LATEST;
	
	public AbsDatabaseConnectivity(Context context, 
			Class<? extends DatabaseObject> objectClass) {
		this(context, objectClass, DatabaseObject.VERSION_LATEST);
	}

	public AbsDatabaseConnectivity(Context context, 
			Class<? extends DatabaseObject> objectClass,
			int version) {
		mContext = context;
		mObjectClass = objectClass;
		
		mVersion = version;
	}

	boolean matchClass(Class<? extends DatabaseObject> klass) {
		return klass.equals(mObjectClass);
	}
	
	final public long insert(DatabaseObject object) {
		if (object == null) {
			return 0l;
		}
		
		if (matchClass(object.getClass()) == false) {
			return 0l;
		}
		
		final long ret = onInsert(object);
		
		if (ret > 0) {
			notifyObservers();
		}
		
		return ret;
	}
	
	final public int insert(DatabaseObject[] objects) {
		if (objects == null || objects.length <= 0) {
			return 0;
		}
		
		List<DatabaseObject> tmp = new ArrayList<DatabaseObject>();
		for (DatabaseObject object: objects) {
			if (matchClass(object.getClass()) == false) {
				continue;
			}
			
			tmp.add(object);
		}
		
		if (tmp.size() <= 0) {
			return 0;
		}
		
		final int ret = onInsert(tmp.toArray(new DatabaseObject[0]));
		
		if (ret > 0) {
			notifyObservers();
		}
		
		return ret;
	}
	
	final public int update(Query query, DatabaseObject object) {
		if (query == null || object == null) {
			return 0;
		}
	
		if (matchClass(query.getObjectClass()) == false) {
			return 0;
		}

		if (matchClass(object.getClass()) == false) {
			return 0;
		}
		
		final int ret =  onUpdate(query, object);
		
		if (ret > 0) {
			notifyObservers();
		}
		
		return ret;
	}
	
	final public int delete(Query query) {
		if (query == null) {
			return 0;
		}
	
		if (matchClass(query.getObjectClass()) == false) {
			return 0;
		}

		final int ret = onDelete(query);
		
		if (ret > 0) {
			notifyObservers();
			
			return ret;
		}
		
		return ret;
	}
	
	final public Cursor queryCursor(Query query) {
		return queryCursor(query, null);
	}

	final public Cursor queryCursor(Query query,
			Class<? extends DatabaseObject> projectionClass) {
		if (query == null) {
			return null;
		}
	
		if (matchClass(query.getObjectClass()) == false) {
			return null;
		}

		return onQueryCursor(query, projectionClass);
	}

	final public List<DatabaseObject> query(Query query) {
		return query(query, null);
	}

	final public List<DatabaseObject> query(Query query,
			Class<? extends DatabaseObject> projectionClass) {
		if (query == null) {
			return null;
		}
	
		if (matchClass(query.getObjectClass()) == false) {
			return null;
		}

		return onQuery(query, projectionClass);
	}
	
	protected void notifyObservers() {
		String action = DatabaseObserver.composeBroadcaseAction(mObjectClass);
		if (action == null) {
			return;
		}
		
		Intent i = new Intent(action);
		
		mContext.sendBroadcast(i);
	}
	
	abstract protected long onInsert(DatabaseObject object);
	abstract protected int onInsert(DatabaseObject[] objects);
	abstract protected int onUpdate(Query query, DatabaseObject object);
	abstract protected int onDelete(Query query);
	abstract protected List<DatabaseObject> onQuery(Query query,
			Class<? extends DatabaseObject> projectionClass);
	abstract protected Cursor onQueryCursor(Query query,
			Class<? extends DatabaseObject> projectionClass);
	
	abstract public DatabaseUpdateInfo checkUpdates();
	
	protected DatabaseObject fromCursor(Cursor c, 
			Class<? extends DatabaseObject> projectionClass) {
		if (c == null) {
			return null;
		}
		
		DatabaseObject object = null;
		
		if (projectionClass != null) {
			object = DatabaseObjectFactory.createDatabaseObject(projectionClass);
		} else {
			object = DatabaseObjectFactory.createDatabaseObject(mObjectClass, mVersion);
		}
		
		if (object != null) {
			object.fillValuesFromCursor(c);
		}

		return object;
	}

	protected String projectionToString(String[] projection) {
		if (projection == null) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder("column [ ");
		
		final int count = projection.length;
		for (int i = 0; i < count; i++) {
			builder.append(projection[i]);
			if (i != (count - 1)) {
				builder.append(", ");
			}
		}
		
		builder.append(" ]");
		
		return builder.toString();
	}
	
	protected String[] createProjection(
			Class<? extends DatabaseObject> projectionClass) {
		if (projectionClass == null) {
			return null;
		}
		
		DatabaseObject prjObject = DatabaseObjectFactory.createDatabaseObject(projectionClass);
		if (prjObject == null) {
			return null;
		}
		
		return prjObject.toSQLProjection();
	}

	public static int getObjectVersion(Class<? extends DatabaseObject> objectClass) {
		if (objectClass == null) {
			return DatabaseObject.VERSION_START;
		}
		
		final DatabaseObject sampleObject =
			DatabaseObjectFactory.createDatabaseObject(objectClass);
		if (sampleObject == null) {
			return DatabaseObject.VERSION_START;
		}
		
		return sampleObject.getVersion();
	}
	
	protected int getDatabaseVersion() {
		if (mVersion != DatabaseObject.VERSION_LATEST) {
			return mVersion;
		}
		
		return getObjectVersion(mObjectClass);
	}
	
}
