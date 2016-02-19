package com.dailystudio.app.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;
import com.dailystudio.utils.ClassNameUtils;

public abstract class AsyncChecker extends AsyncTasksQueueExecutor 
    implements Runnable {
   
    private final static String KEY_CHECK_TIMESTAMP = "check-timestamp";

	private class CheckerAsyncTask 
		extends QueuedAsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			if (mContext == null) {
				return null;
			}
			
			final long now = System.currentTimeMillis();
			final long lastTimestamp = getLastCheckTimestamp(mContext);
		
			doCheck(now, lastTimestamp);
			
			Logger.debug("CHECK DONE: now = %d(%s)",
					now,
					CalendarUtils.timeToReadableString(now));
			
			setLastCheckTimestamp(mContext, now);
			
			return null;
		}
		
	}
	
    protected Context mContext;

    public AsyncChecker(Context context) {
        mContext = context;
    }

    @Override
    public void run() {
        pendingOrExecuteTask(new CheckerAsyncTask());
    }
    
	public long getLastCheckTimestamp(Context context) {
		if (context == null) {
			return -1l;
		}
		
		final String prefName = getCheckerPrefName();
		if (prefName == null) {
			return -1l;
		}
		
		final String timestampKey = getCheckTimestampKey();
		if (timestampKey == null) {
			return -1l;
		}
		
		final SharedPreferences pref = context.getSharedPreferences(
				prefName, Context.MODE_PRIVATE);
		if (pref == null) {
			return -1l;
		}
		
		return pref.getLong(timestampKey, -1l);
	}
	
	protected void setLastCheckTimestamp(Context context, long timestamp) {
		if (context == null || timestamp < 0) {
			return;
		}
		
		final String prefName = getCheckerPrefName();
		if (prefName == null) {
			return;
		}
		
		final String timestampKey = getCheckTimestampKey();
		if (timestampKey == null) {
			return;
		}
		
		final SharedPreferences pref = context.getSharedPreferences(
				prefName, Context.MODE_PRIVATE);
		if (pref == null) {
			return;
		}
		
		final Editor editor = pref.edit();
		if (editor == null) {
			return;
		}
		
		editor.putLong(timestampKey, timestamp);
		editor.commit();
	}
	
    protected String getCheckerPrefName() {
        final Class<?> klass = getClass();
        
        String pkgName = ClassNameUtils.getPackageName(klass);
        if (pkgName == null) {
            return null;
        }
            
        String className = ClassNameUtils.getClassName(klass);
        if (className == null) {
            return null;
        }
            
        return String.format("%s.%s.db", 
                pkgName, 
                className.replace('.', '_').replace('$', '_'));
    }
    
    protected String getCheckTimestampKey() {
        return KEY_CHECK_TIMESTAMP;
    }
    
    abstract protected void doCheck(long now, long lastTimestamp);

}
