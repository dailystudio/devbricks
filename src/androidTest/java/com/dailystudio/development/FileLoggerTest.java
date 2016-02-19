package com.dailystudio.development;

import android.test.AndroidTestCase;

public class FileLoggerTest extends AndroidTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
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

}
