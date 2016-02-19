package com.dailystudio.dataobject.database;

import android.content.Context;
import android.content.IntentFilter;

import com.dailystudio.manager.Manager;
import com.dailystudio.manager.SingletonManager;

public class OpenedDatabaseManager extends SingletonManager<Long, OpenedDatabase> {
	
	public static synchronized OpenedDatabaseManager getInstance() {
		return Manager.getInstance(OpenedDatabaseManager.class);
	}
	
	@Override
	public synchronized void addObject(OpenedDatabase object) {
		/*
		 * Synchronzied this method to avoid register and unregister
		 * close db receiver issue;
		 */
		final int prevCount = getCount();
		
		if (prevCount == 0) {
			if (registerReceiver() == false) {
				return;
			}
		}
		
		super.addObject(object);
	}
	
	@Override
	public synchronized OpenedDatabase removeObjectByKey(Long key) {
		/*
		 * Synchronzied this method to avoid register and unregister
		 * close db receiver issue;
		 */
		OpenedDatabase odb = super.removeObjectByKey(key);
		
		if (odb != null) {
			odb.close();
		}
		
		final int count = getCount();
		if (count <= 0) {
			unregisterReceiver();
		}

		return odb;
	}
	
	private boolean registerReceiver() {
		final Context context = getContext();
		if (context == null) {
			return false;
		}
		
		synchronized (mReceiver) {
			
		}
		IntentFilter filter = 
			new IntentFilter(OpenedDatabaseCloseReceiver.ACTION_CLOSE_DATABASE);
		
		boolean successful = false;
		try {
			context.registerReceiver(mReceiver, filter);
			
			successful = true;
		} catch (Exception e) {
			e.printStackTrace();
			
			successful = false;
		}
		
		return successful;
	}

	private void unregisterReceiver() {
		final Context context = getContext();
		if (context == null) {
			return;
		}
		
		try {
			context.unregisterReceiver(mReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private OpenedDatabaseCloseReceiver mReceiver = new OpenedDatabaseCloseReceiver();

}
