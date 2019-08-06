package com.dailystudio.app;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.dailystudio.BuildConfig;
import com.dailystudio.GlobalContextWrapper;
import com.dailystudio.development.Logger;

public class DevBricksMultiDexApplication extends MultiDexApplication {

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

	@Override
	public void attachBaseContext(Context base) {
		MultiDex.install(base);
		super.attachBaseContext(base);
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

	protected boolean isDebugBuild() {
		return BuildConfig.DEBUG;
	}

}
