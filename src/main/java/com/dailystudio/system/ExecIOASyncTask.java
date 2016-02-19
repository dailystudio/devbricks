package com.dailystudio.system;

import android.os.AsyncTask;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

abstract class ExecIOASyncTask extends AsyncTask<Void, Void, Void> {

	protected String mTag;
	protected volatile boolean mEndFlag = false;
	
	public ExecIOASyncTask(String tag) {
		mTag = tag;
	}
	
	public void start() {
		mEndFlag = false;

		executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void)null);
	}
	
	public void stop() {
		mEndFlag = true;
		
		cancel(true);
	}
	
	public void waitForStop() {
		try {
			get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			e.printStackTrace();
		}
	}
	
}
