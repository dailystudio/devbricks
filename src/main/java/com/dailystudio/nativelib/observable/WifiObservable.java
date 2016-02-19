package com.dailystudio.nativelib.observable;

import java.util.Observer;

import com.dailystudio.development.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

public class WifiObservable extends NativeObservable {
	
	private BroadcastReceiver mWifiReceiver = null;
	
	public WifiObservable(Context context) {
		super(context);
	}

	@Override
	protected void onCreate() {
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		

		mWifiReceiver = new WifiChangedReceiver();
		if (mWifiReceiver != null) {
			mContext.registerReceiver(mWifiReceiver, filter);
		}
	}

	@Override
	protected void onDestroy() {
		if (mWifiReceiver != null) {
			try {
				mContext.unregisterReceiver(mWifiReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		mWifiReceiver = null;
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
	
	private class WifiChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.debug("intent = %s", intent);
			if (intent == null) {
				return;
			}
			final String action = intent.getAction();
			if (action == null) {
				return;
			}
			
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				final int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 
						WifiManager.WIFI_STATE_UNKNOWN);
				
				if (state == WifiManager.WIFI_STATE_ENABLED
						|| state == WifiManager.WIFI_STATE_DISABLED) {
					notifyObservers();
				}
			}
		}
		
	}
	
}
