package com.dailystudio.job;

public abstract class Job implements Runnable {
	
	public interface OnFinishedListener {
		
		public void onFinished(JobExecutor executor, Job job);
		
	}
	
	private volatile boolean mRunInCreatorThread;
	
	private OnFinishedListener mOnFinishedListener = null;
	
	public Job() {
		this(false);
	}
	
	public Job(boolean runInCreatorThread) {
		mRunInCreatorThread = runInCreatorThread;
	}
	
	public void setRunInCreatorThread(boolean runInCreatorThread) {
		mRunInCreatorThread = runInCreatorThread;
	}
	
	public boolean isRunInCreatorThread() {
		return mRunInCreatorThread;
	}

	public void setOnFinishedListener(OnFinishedListener l) {
		mOnFinishedListener = l;
	}
	
	public OnFinishedListener getOnFinishedListener() {
		return mOnFinishedListener;
	}
	
	@Override
	public void run() {
		doJob();
		
		synchronized (this) {
			this.notifyAll();
		}
	}

	abstract protected void doJob();
	
}
