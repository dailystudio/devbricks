package com.dailystudio.job;

import com.dailystudio.development.Logger;
import com.dailystudio.factory.Factory;
import com.dailystudio.test.ActivityTestCase;

public class JobTest extends ActivityTestCase {
	
	private static class SimpleJob extends Job {

		int count = 0;
		
		public SimpleJob() {
			super();
		}
		
		public SimpleJob(boolean runInCreatorThread) {
			super(runInCreatorThread);
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
	
	public void testCreateJob() {
		Job job = null;
		
		job = new SimpleJob();
		assertNotNull(job);
		assertEquals(job.isRunInCreatorThread(), false);
		
		job = new SimpleJob(true);
		assertNotNull(job);
		assertEquals(job.isRunInCreatorThread(), true);
	}
	
	public void testSetRunInCreatorThread() {
		Job job = null;
		
		job = new SimpleJob();
		assertNotNull(job);
		assertEquals(job.isRunInCreatorThread(), false);
		
		job.setRunInCreatorThread(true);
		assertEquals(job.isRunInCreatorThread(), true);
		
		job.setRunInCreatorThread(false);
		assertEquals(job.isRunInCreatorThread(), false);
	}
	
	public void testDoJob() {
		Job job = null;
		
		job = new SimpleJob();
		assertNotNull(job);
		assertEquals(job.isRunInCreatorThread(), false);

		Thread thread = new Thread(job);
		assertNotNull(thread);

		boolean success = false;
		synchronized (job) {
			thread.start();
			
			Logger.debug("WAIT FOR job(%s)", job);
			try {
				job.wait();
				Logger.debug("NOTIFIED WITH job(%s)", job);
				success = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
				Logger.debug("FAILD FOR job(%s) WAITING", job);
				success = false;
			}
		}
		
		assertTrue(success);
		assertEquals(3, ((SimpleJob)job).count);
	}
	
}
