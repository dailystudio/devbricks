package com.dailystudio.job;

import com.dailystudio.development.Logger;
import com.dailystudio.factory.Factory;
import com.dailystudio.job.Job.OnFinishedListener;
import com.dailystudio.test.ActivityTestCase;

public class JobExecutorTest extends ActivityTestCase {
	
	private static class JobA extends Job {

		private int count = 0;
		
		private JobA() {
			super();
		}
		
		@Override
		protected void doJob() {
			for (int i = 0; i < 3; i++) {
				count++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private static class JobB extends Job {

		private int count = 3;
		
		private JobB() {
			super();
		}
		
		@Override
		protected void doJob() {
			for (int i = 0; i < 3; i++) {
				count--;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private static class JobFinishedListener implements OnFinishedListener {

		private Job job = null;
		
		private boolean done;
		
		private JobFinishedListener(Job job) {
			this.job = job;
			done = false;
		}
		
		@Override
		public void onFinished(JobExecutor executor, Job job) {
			if (this.job == job) {
				done = true;
			}
		}
		
	}
	
	private class ExecutorThread extends Thread {
		
		private JobExecutor executor;
		private JobFinishedListener la;
		private JobFinishedListener lb;
		
		public ExecutorThread(JobFinishedListener la, JobFinishedListener lb) {
			executor = new JobExecutor(mContext, true); 
			this.la = la;
			this.lb = lb;
		}
		
		@Override
		public void run() {
			executor.start();
			
			boolean endFlag = false;
			for(; !endFlag ;) {
				Logger.debug("la.done(%s), lb.done(%s)", 
						la.done,
						lb.done);
				if (la.done && lb.done) {
					endFlag = true;
					executor.stop();
					
					synchronized (executor) {
						executor.notifyAll();
					}
					
					continue;
				}
				
				try {
					Logger.debug("SLEEP 500ms and check again");
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		Factory.bindContext(mContext);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		Factory.unbindContext(mContext);
	}
	
	public void testRunJobExecutor() {
		JobA jobA = new JobA();
		assertNotNull(jobA);
		assertEquals(jobA.isRunInCreatorThread(), false);
		JobFinishedListener lA = new JobFinishedListener(jobA);
		jobA.setOnFinishedListener(lA);
		
		JobB jobB = new JobB();
		assertNotNull(jobB);
		assertEquals(jobB.isRunInCreatorThread(), false);
		JobFinishedListener lB = new JobFinishedListener(jobB);
		jobB.setOnFinishedListener(lB);
		
		ExecutorThread thread = new ExecutorThread(lA, lB);
		assertNotNull(thread);
		
		JobExecutor executor = thread.executor;
		assertNotNull(executor);

		thread.start();
		
		executor.scheduleJob(jobA);
		executor.scheduleJob(jobB);
		
		synchronized (executor) {
			try {
				Logger.debug("WAIT FOR executor(%s)", executor);
				executor.wait();
				Logger.debug("NOTIFIED WITH executor(%s)", executor);
			} catch (InterruptedException e) {
				Logger.debug("FAILD FOR executor(%s) WAITING", executor);
				e.printStackTrace();
			}
		}
		
		assertEquals(3, jobA.count);
		assertEquals(0, jobB.count);

		assertTrue(lA.done);
		assertTrue(lB.done);
	}
	
	
	
}
