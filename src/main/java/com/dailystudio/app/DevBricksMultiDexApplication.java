package com.dailystudio.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.dailystudio.BuildConfig;

public class DevBricksMultiDexApplication extends MultiDexApplication {

	private DevBricksApplicationAgent mDevBricksAgent;

	@Override
	public void onCreate() {
		super.onCreate();

		mDevBricksAgent = new DevBricksApplicationAgent(this);
		mDevBricksAgent.onCreate();
	}
	
	@Override
	public void onTerminate() {
		if (mDevBricksAgent != null) {
			mDevBricksAgent.onTerminate();
		}

		super.onTerminate();
	}

	@Override
	public void attachBaseContext(Context base) {
		MultiDex.install(base);
		super.attachBaseContext(base);
	}

	protected boolean isDebugBuild() {
		if (mDevBricksAgent != null) {
			return mDevBricksAgent.isDebugBuild();
		}

		return false;
	}

}
