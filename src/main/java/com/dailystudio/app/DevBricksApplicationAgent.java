package com.dailystudio.app;

import android.app.Application;
import android.content.Context;

import com.dailystudio.BuildConfig;
import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.development.Logger;

class DevBricksApplicationAgent {

	private Application mApp;

	DevBricksApplicationAgent(Application application) {
		mApp = application;
	}

	public void onCreate() {
		final Context appContext = getApplicationContext();
		
		GlobalContextWrapper.bindContext(appContext);
		
		checkAndSetDebugEnabled();
	}

	public void onTerminate() {
		final Context appContext = getApplicationContext();
		
		GlobalContextWrapper.unbindContext(appContext);
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
			Logger.setDebugEnabled(isDebugBuild());
		}

		Logger.setSecureDebugEnabled(isDebugBuild());
	}

	private Context getApplicationContext() {
		if (mApp == null) {
			return null;
		}

		return mApp.getApplicationContext();
	}

	private String getPackageName() {
		if (mApp == null) {
			return null;
		}

		return mApp.getPackageName();
	}

	protected boolean isDebugBuild() {
		return BuildConfig.DEBUG;
	}
	
}
