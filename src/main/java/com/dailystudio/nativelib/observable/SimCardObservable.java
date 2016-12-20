package com.dailystudio.nativelib.observable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.dailystudio.development.Logger;

/**
 * Created by nanye on 16/10/12.
 */

public class SimCardObservable extends NativeObservable {

    private SimCardChangedReceiver mSimCardChangedReceiver = null;

    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    public SimCardObservable(Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        mSimCardChangedReceiver = new SimCardChangedReceiver();
        if (mSimCardChangedReceiver != null) {
            IntentFilter filter = new IntentFilter(ACTION_SIM_STATE_CHANGED);

            try {
                mContext.registerReceiver(mSimCardChangedReceiver, filter);
            } catch (Exception e) {
                Logger.warnning("register receiver failure: %s",
                        e.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mSimCardChangedReceiver != null) {
            try {
                mContext.unregisterReceiver(mSimCardChangedReceiver);
            } catch (Exception e) {
                Logger.warnning("unregister receiver failure: %s",
                        e.toString());
            }
        }

        mSimCardChangedReceiver = null;
    }

    @Override
    protected void onPause() {
    }

    @Override
    protected void onResume() {
    }

    private class SimCardChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            final String action = intent.getAction();
            if (action == null) {
                return;
            }

            if (ACTION_SIM_STATE_CHANGED.equals(action)) {
                notifyObservers();
            }
        }

    }

}
