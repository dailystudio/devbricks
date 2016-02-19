package com.dailystudio.dataobject.database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

class OpenedDatabaseCloseReceiver extends BroadcastReceiver {

	public final static String ACTION_CLOSE_DATABASE = "dailystudio.intent.ACTION_CLOSE_DATABASE";
	
	public final static String EXTRA_SERIAL = "serial";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (context == null || intent == null) {
			return;
		}

		final String action = intent.getAction();
		if (ACTION_CLOSE_DATABASE.equals(action)) {
			long serial = intent.getLongExtra(EXTRA_SERIAL, 0);
			if (serial <= 0) {
				return;
			}
			
			OpenedDatabaseManager odbmgr = 
				OpenedDatabaseManager.getInstance();
			if (odbmgr == null) {
				return;
			}
			
			OpenedDatabase db = odbmgr.removeObjectByKey(serial);
			
			odbmgr.removeObject(db);
/*			
			Logger.debug("CLOSE: serial = %d, db = %s [remained: %d]", 
					serial, db, odbmgr.getCount());
*/		}
	}

}
