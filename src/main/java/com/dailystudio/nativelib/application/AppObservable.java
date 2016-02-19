package com.dailystudio.nativelib.application;

import com.dailystudio.development.Logger;
import com.dailystudio.nativelib.observable.NativeObservable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class AppObservable extends NativeObservable {

	private BroadcastReceiver mAppReceiver = null;
	
	public AppObservable(Context context) {
		super(context);
	}

	@Override
	protected void onCreate() {
    	IntentFilter appFilter = new IntentFilter();

    	appFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
    	appFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
    	appFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
    	appFilter.addDataScheme("package");
    	appFilter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);

    	IntentFilter extAppFilter = new IntentFilter();

    	extAppFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
    	extAppFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
    	extAppFilter.setPriority(IntentFilter.SYSTEM_LOW_PRIORITY);
		

		mAppReceiver = new AppBroadcastReceiver();
		if (mAppReceiver != null) {
			mContext.registerReceiver(mAppReceiver, appFilter);
			mContext.registerReceiver(mAppReceiver, extAppFilter);
		}
	}

	@Override
	protected void onDestroy() {
		if (mAppReceiver != null) {
			try {
				mContext.unregisterReceiver(mAppReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		mAppReceiver = null;
	}

	@Override
	protected void onPause() {
	}

	@Override
	protected void onResume() {
	}

    private class AppBroadcastReceiver extends BroadcastReceiver {
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Logger.debug("intent(%s)", intent);
	    	notifyObservers(intent);
	    }
	    
    };
    
}
