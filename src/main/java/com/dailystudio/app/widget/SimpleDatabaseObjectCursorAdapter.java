package com.dailystudio.app.widget;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DatabaseObjectFactory;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

public class SimpleDatabaseObjectCursorAdapter<T extends DatabaseObject>
	extends SimpleCursorAdapter {

	protected Class<? extends DatabaseObject> mObjectClass = null;
	
	protected int mVersion = DatabaseObject.VERSION_LATEST;
	
	public SimpleDatabaseObjectCursorAdapter(Context context, 
			int layout, Class<? extends T> klass) {
		this(context, layout, klass, DatabaseObject.VERSION_LATEST);
	}
	
	public SimpleDatabaseObjectCursorAdapter(Context context, 
			int layout, Class<? extends T> klass, int version) {
		super(context, layout, null,
				new String[0], new int[0], 0);
		
		mObjectClass = klass;
		mVersion = version;
	}

	public T dumpItem(int position) {
		if (position < 0 || position >= getCount()) {
			return null;
		}
		
		int oldPos = -1;
		final Cursor c = getCursor();
		if (c != null) {
			oldPos = c.getPosition();
		}
		
		T item = null;
		Object o = getItem(position);
		if (o instanceof Cursor) {
			item = dumpItem((Cursor)o);
		}
		
		if (c != null && oldPos != -1) {
			c.moveToPosition(oldPos);
		}

		return item;
	}

	@SuppressWarnings("unchecked")
	public T dumpItem(Cursor c) {
		if (c == null) {
			return null;
		}
		
		DatabaseObject object = 
			DatabaseObjectFactory.createDatabaseObject(mObjectClass, mVersion);
		
		if (object != null) {
			object.fillValuesFromCursor(c);
		}

		return (T)object;
	}

}
