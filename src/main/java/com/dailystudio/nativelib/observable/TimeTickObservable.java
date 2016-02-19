package com.dailystudio.nativelib.observable;

import java.util.Calendar;
import java.util.Observer;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;

import android.content.Context;

public class TimeTickObservable extends NativeObservable {
	
	private class TimeTickThread extends Thread {
		
		private static final int TIME_SYNC_DELTA = 60; /* 60 second*/
		
		private boolean mEndFlag = false;
		
		private long mCurrentTime = 0;
		private long mSleepTime = 0;
		
		@Override
//		protected Void doInBackground(Void... params) {
		public void run() {
			do {
				mCurrentTime = Calendar.getInstance().getTimeInMillis();

				notifyObservers(Long.valueOf(mCurrentTime));
/*				Logger.debug("notifyObservers(), this = %s, count(%d)",
						TimeTickObservable.this,
						countObservers());
*/				
				final long elapse = (mCurrentTime % CalendarUtils.MINUTE_IN_MILLIS) / CalendarUtils.SECOND_IN_MILLIS;
				mSleepTime = (TIME_SYNC_DELTA - elapse);

				Logger.debug("time(%s), elapse(%d sec), sleep(%d sec)",
						CalendarUtils.timeToReadableString(mCurrentTime),
						elapse,
						mSleepTime);

				try {
					Thread.sleep(mSleepTime * CalendarUtils.SECOND_IN_MILLIS);
				} catch (InterruptedException e) {
//					e.printStackTrace();
					break;
				}

			} while (!mEndFlag);
			
//			return null;
		}
		
		public void cancel() {
			mEndFlag = true;
		}
		
	}
	
	private TimeTickThread mTickThread;

	public TimeTickObservable(Context context) {
		super(context);
	}

	@Override
	protected void onCreate() {
	}

	@Override
	protected void onDestroy() {
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
		
		startTimeTickThread();
	}
	
	@Override
	public synchronized void deleteObserver(Observer observer) {
		super.deleteObserver(observer);
		
		if (countObservers() == 0) {
			stopTimeTickThread();
		}
	}
	
	private void startTimeTickThread() {
		if (mTickThread == null) {
			mTickThread = new TimeTickThread();
			mTickThread.start();
		}
	}
	
	private void stopTimeTickThread() {
		if (mTickThread != null) {
			mTickThread.cancel();
			mTickThread = null;
		}
	}
	
	public synchronized void reset() {
		stopTimeTickThread();
		startTimeTickThread();
	}
	
}
