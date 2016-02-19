package com.dailystudio.dataobject.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.utils.ClassNameUtils;

public abstract class DatabaseObserver {

	static final String ACTION_DATABASE_CHANGED_SUFFIX = ".intent.ACTION_DATABASE_CHANGED";
	
	protected Context mContext;
	protected Class<? extends DatabaseObject> mObjectClass;

	public DatabaseObserver(Context context, Class<? extends DatabaseObject> klass) {
		mContext = context;
		
		mObjectClass = klass;
	}
	
	public Class<? extends DatabaseObject> getObserverdObjectClass() {
		return mObjectClass;
	}
	
	public void register() {
		if (mContext == null || mObjectClass == null) {
			return;
		}
		
		final String broadcastAction = composeBroadcaseAction(mObjectClass);
		if (broadcastAction == null) {
			return;
		}
		
		IntentFilter filter = new IntentFilter(broadcastAction);
		
		try {
			final Context appContext = mContext.getApplicationContext();
			
			appContext.registerReceiver(mReceiver, filter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unregister() {
		if (mContext == null || mObjectClass == null) {
			return;
		}
		
		try {
			final Context appContext = mContext.getApplicationContext();
			
			appContext.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	abstract protected void onDatabaseChanged(Context context, 
			Class<? extends DatabaseObject> objectClass);
	
	static String composeBroadcaseAction(Class<? extends DatabaseObject> klass) {
		String pkgName = ClassNameUtils.getPackageName(klass);
		if (pkgName == null) {
			return null;
		}
			
		String className = ClassNameUtils.getClassName(klass);
		if (className == null) {
			return null;
		}
			
		return String.format("%s.%s%s", 
				pkgName, 
				className.replace('$', '_'),
				ACTION_DATABASE_CHANGED_SUFFIX);

	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			onDatabaseChanged(context, mObjectClass);
		}
		
	};

}
