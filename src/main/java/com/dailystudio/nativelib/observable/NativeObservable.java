package com.dailystudio.nativelib.observable;

import java.util.Observer;

import com.dailystudio.manager.ISingletonObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class NativeObservable extends java.util.Observable 
	implements ISingletonObject<Class<? extends NativeObservable>> {
	
	protected Context mContext;
	
	public NativeObservable(Context context) {
		mContext = context;
		
		initMembers();
	}
	
	private void initMembers() {
	}

	@Override
	public void addObserver(Observer observer) {
		if (observer == null) {
			return;
		}
	
		final int count = countObservers();
		
		super.addObserver(observer);
		
		if (count == 0) {
			onResume();
		}
	}
	
	@Override
	public synchronized void deleteObserver(Observer observer) {
		if (observer == null) {
			return;
		}
	
		super.deleteObserver(observer);

		final int count = countObservers();
		if (count == 0) {
			onPause();
		}
	}
	
	@Override
	public synchronized void deleteObservers() {
		super.deleteObservers();
		
		onPause();
	}
	
	@Override
	public void notifyObservers() {
		notifyObservers(null);
	}
	
	@Override
	public void notifyObservers(Object data) {
		if (mHandler == null) {
			return;
		}
		
		mHandler.removeMessages(NOTIFY_LISTENERS);
		
		Message msg = mHandler.obtainMessage(NOTIFY_LISTENERS, data);
		
		mHandler.sendMessage(msg);
	}
	
	private synchronized void realNotifyObservers(Object data) {
		setChanged();
		super.notifyObservers(data);
	}
	
	@Override
	public Class<? extends NativeObservable> getSingletonKey() {
		return getClass();
	}

	protected abstract void onCreate();
	protected abstract void onDestroy();
	
	protected abstract void onPause();
	protected abstract void onResume();
	
	private static final int NOTIFY_LISTENERS = 0x0;

	private Handler mHandler = new Handler(){
		
    	@Override
		public void handleMessage(Message msg) {
    		if (msg == null) {
    			return;
    		}
    		
            switch (msg.what) {
            	case NOTIFY_LISTENERS:
            		realNotifyObservers(msg.obj);
            		break;
            }
		}
		
	};

}
