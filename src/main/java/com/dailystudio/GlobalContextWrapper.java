package com.dailystudio;

import android.content.Context;

import com.dailystudio.development.Logger;

public class GlobalContextWrapper {
    
	private static Context sContext = null;
	
	public synchronized static void bindContext(Context context) {
		if (context == null) {
			return;
		}
		
		final Context appContext = context.getApplicationContext();
		sContext = (appContext == null ? context : appContext);
//		Logger.debug("sContext = %s", sContext);
	}
	
	public synchronized static void unbindContext(Context context) {
		if (sContext != context) {
			return;
		}
		
		sContext = null;
	}
	
    public synchronized static Context getContext() {
    	if (sContext == null) {
    		Logger.warnning("NULL context, please call bindContext() firstly.");
    	}
    	return sContext;
    }

}
