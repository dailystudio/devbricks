package com.dailystudio.nativelib.observable;

import java.util.Observer;

import com.dailystudio.development.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

public class ConnectivityObservable extends NativeObservable {
	
	private BroadcastReceiver mConnectivityReceiver = null;
	
	public ConnectivityObservable(Context context) {
		super(context);
	}

	@Override
	protected void onCreate() {
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		
		mConnectivityReceiver = new ConnectivityReceiver();
		if (mConnectivityReceiver != null) {
			mContext.registerReceiver(mConnectivityReceiver, filter);
		}
	}

	@Override
	protected void onDestroy() {
		if (mConnectivityReceiver != null) {
			try {
				mContext.unregisterReceiver(mConnectivityReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		mConnectivityReceiver = null;
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
	
	private class ConnectivityReceiver extends BroadcastReceiver {

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
			
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
//				final NetworkInfo networkInfo = 
//					intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//				final NetworkInfo otherInfo = 
//					intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
//				final String extraInfo = 
//					intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
//				final String reason = 
//					intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
//				Logger.debug("networkInfo = %s", networkInfo);
//				Logger.debug("otherInfo = %s", otherInfo);
//				Logger.debug("extraInfo = %s", extraInfo);
//				Logger.debug("reason = %s", reason);

				notifyObservers();
			}
		}
		
	}
	
}
