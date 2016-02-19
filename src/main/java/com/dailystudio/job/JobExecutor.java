package com.dailystudio.job;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.dailystudio.development.Logger;
import com.dailystudio.job.Job.OnFinishedListener;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

public class JobExecutor extends AsyncTask<Void, Job, Void> {
	
	private class LooperThread extends Thread {
		
		public void run() {
			Looper.prepare();
			
			mHandler = new Handler();
			
			Looper.loop();
		}
	      
	}
	
	private class NotifyJobListenersRunnable implements Runnable {
		
		private Job mJob;
		
		private NotifyJobListenersRunnable(Job job) {
			mJob = job;
		}
		
		@Override
		public void run() {
			if (mJob == null) {
				return;
			}

			final OnFinishedListener l = mJob.getOnFinishedListener();
			if (l == null) {
				return;
			}
			
			l.onFinished(JobExecutor.this, mJob);
		}
		
	}
	
	protected Context mContext;
	
	private BlockingQueue<Job> mPendingQueue;
	
	private volatile boolean mEndFlag = false;
	private Object mWaitObject = new Object();
	
	private Handler mHandler;

	public JobExecutor(Context context) {
		this(context, false);
	}
	
	JobExecutor(Context context, boolean handlerInThread) {
		mContext = context;
		
		mPendingQueue = new LinkedBlockingQueue<Job>();
		
		if (handlerInThread) {
			new LooperThread().start();
		} else {
			mHandler = new Handler();
		}
	}
	
	public void start() {
		Status status = getStatus();
		if (status == Status.RUNNING) {
			return;
		}
		
		executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
	}
	
	public void stop() {
		mEndFlag = true;
		
		cancel(true);
		clearJobs();
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
//		Logger.debug("TRACE START EXECUTOR: %s(0x%08x)", 
//				Thread.currentThread().getName(),
//				hashCode());
		if (mPendingQueue == null) {
			return null;
		}
		
		Job job = null;
		for ( ; !mEndFlag; ) {
			try {
				job = mPendingQueue.poll(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				Logger.warnning("Interrupted from poll(): %s", e.toString());
				
				job = null;
			}
			
			if (mEndFlag) {
				continue;
			}

			if (job == null) {
				synchronized (mWaitObject) {
					try {
						mWaitObject.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
						Logger.debug("Interrupted from wait()");
					}
				}
				
				continue;
			}
			
			if (job.isRunInCreatorThread() == false) {
				job.run();
			} else {
				synchronized (job) {
					mHandler.post(job);

					try {
						job.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
//			Logger.debug("FINISHED JOB: %s", job);
			notifyOnFinishedListener(job);
		}
		
//		Logger.debug("TRACE FINISH EXECUTOR: %s(0x%08x)", 
//				Thread.currentThread().getName(),
//				hashCode());
		return null;
	}
	
	private void notifyOnFinishedListener(Job job) {
		if (job == null || job.getOnFinishedListener() == null) {
			return;
		}
		
		final NotifyJobListenersRunnable runnable = 
			new NotifyJobListenersRunnable(job);
		
		mHandler.post(runnable);
	}
	
	public void scheduleJob(Job job) {
		if (job == null) {
			return;
		}
		
		mPendingQueue.add(job);
		
		synchronized (mWaitObject) {
			mWaitObject.notifyAll();
		}
	}
	
	public void cancelJob(Job job) {
		if (job == null) {
			return;
		}
		
		mPendingQueue.remove(job);
		
		synchronized (mWaitObject) {
			mWaitObject.notifyAll();
		}
	}

	public int countJobs() {
		return mPendingQueue.size();
	}
	
	public void clearJobs() {
		mPendingQueue.clear();
		
		synchronized (mWaitObject) {
			mWaitObject.notifyAll();
		}
	}
	
	@Override
	protected void onCancelled() {
		mEndFlag = true;
		
		synchronized (mWaitObject) {
			mWaitObject.notifyAll();
		}

		super.onCancelled();
	}

}
