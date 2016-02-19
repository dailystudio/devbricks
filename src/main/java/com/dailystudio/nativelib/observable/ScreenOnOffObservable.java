package com.dailystudio.nativelib.observable;

import java.util.Observer;

import com.dailystudio.development.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScreenOnOffObservable extends NativeObservable {
	
	private BroadcastReceiver mScreenOnOffReceiver = null;
	
	public ScreenOnOffObservable(Context context) {
		super(context);
	}

	@Override
	protected void onCreate() {
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);

		mScreenOnOffReceiver = new ScreenOnOffChanged();
		if (mScreenOnOffReceiver != null) {
			mContext.registerReceiver(mScreenOnOffReceiver, filter);
		}
	}

	@Override
	protected void onDestroy() {
		if (mScreenOnOffReceiver != null) {
			try {
				mContext.unregisterReceiver(mScreenOnOffReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		mScreenOnOffReceiver = null;
	}

	@Override
	protected void onPause() {
	}

	@Override
	protected void onResume() {
	}

	@Override
	public synchronized void addObserver(Observer observer) {
		super.addObserver(observer);
	}
	
	@Override
	public synchronized void deleteObserver(Observer observer) {
		super.deleteObserver(observer);
	}
	
	private class ScreenOnOffChanged extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.debug("intent = %s", intent);
			if (intent == null) {
				return;
			}
			
			notifyObservers();
		}
		
	}
	
}
