package com.dailystudio.dataobject.database;

import com.dailystudio.development.Logger;
import com.dailystudio.manager.Manager;
import com.dailystudio.manager.SingletonManager;

public class OpenedDatabaseManager extends SingletonManager<Long, OpenedDatabase> {

	final static boolean ODM_DEBUG = false;
	
	public static synchronized OpenedDatabaseManager getInstance() {
		return Manager.getInstance(OpenedDatabaseManager.class);
	}
	
	@Override
	public synchronized void addObject(OpenedDatabase object) {
		if (ODM_DEBUG) {
			Logger.debug("[ODM] add new db: serial = %s",
					object.getSingletonKey());
		}

		super.addObject(object);
	}
	
	@Override
	public synchronized OpenedDatabase removeObjectByKey(Long key) {
		if (ODM_DEBUG) {
			Logger.debug("[ODM] delete existed db: serial = %s", key);
		}

		OpenedDatabase odb = super.removeObjectByKey(key);
		
		if (odb != null) {
			odb.close();
		}
		
		return odb;
	}

}
