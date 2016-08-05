package com.dailystudio.development;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.dailystudio.test.PermCheckActivity;

public class FileLoggerTest extends ActivityInstrumentationTestCase2<PermCheckActivity> {

	private Object mPermCheckLock = new Object();

	public FileLoggerTest() {
		super("com.dailystudio.test", PermCheckActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		synchronized (mPermCheckLock) {
			try {
				mPermCheckThread.start();

				Logger.debug("wait for perm check result");
				mPermCheckLock.wait();
				Logger.debug("release for perm check result");
			} catch (InterruptedException e) {
				Logger.warnning("wait for lock interrupted: %s",
						e.toString());
			} catch (Exception e) {
				Logger.warnning("check perm failed: %s", e.toString());
			}
		}

	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDebug() {
		FileLogger logger = new FileLogger("/sdcard/test_debug.log");
		assertNotNull(logger);
		
		int ret = 0;
		for (int i = 0; i < 10; i++) {
			ret = logger.debug("TestOutput: [%03d]", i);
			assertEquals(70, ret);
		}
		
		logger.close();
	}

	private Thread mPermCheckThread = new Thread(new Runnable() {
		@Override
		public void run() {
			final Context context = getActivity();
			boolean checked = false;

			for (;;) {
				synchronized (mPermCheckLock) {
					checked = PermCheckActivity.isPermChecked(context);

					Logger.debug("perm check checked: %s",
							context,
							checked);

					if (checked) {

						PermCheckActivity.setkPermChecked(context, false);
						mPermCheckLock.notifyAll();
						break;
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Logger.debug("sleep is interrupted: %s", e.toString());
					}
				}
			}
		}
	});

}
