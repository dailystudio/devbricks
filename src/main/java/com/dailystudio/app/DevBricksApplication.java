package com.dailystudio.app;

import android.app.Application;
import android.content.Context;

import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.development.Logger;
import com.dailystudio.BuildConfig;

public class DevBricksApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		final Context appContext = getApplicationContext();
		
		GlobalContextWrapper.bindContext(appContext);
		
		checkAndSetDebugEnabled();
	}
	
	@Override
	public void onTerminate() {
		final Context appContext = getApplicationContext();
		
		GlobalContextWrapper.unbindContext(appContext);

		super.onTerminate();
	}
	
	private void checkAndSetDebugEnabled() {
		boolean handled = false;
		
		if (Logger.isDebugSuppressed()
				|| Logger.isPackageDebugSuppressed(getPackageName())) {
			Logger.setDebugEnabled(false);
			
			handled = true;
		}
		
		if (Logger.isDebugForced()
				|| Logger.isPackageDebugForced(getPackageName())) {
			Logger.setDebugEnabled(true);
			
			handled = true;
		} 
		
		if (!handled) {
			Logger.setDebugEnabled(BuildConfig.DEBUG);
		}
	}
	
}
