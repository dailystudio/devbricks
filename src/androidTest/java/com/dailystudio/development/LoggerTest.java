package com.dailystudio.development;


import java.util.Random;

import android.test.AndroidTestCase;

public class LoggerTest extends AndroidTestCase {
	
	private Random mRandom = new Random();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSetDebugEnabled() {
		Logger.setDebugEnabled(true);
		assertTrue(Logger.isDebugEnabled());

		Logger.setDebugEnabled(false);
		assertTrue(!Logger.isDebugEnabled());
	}

	public void testLogOutput() {
		Logger.setDebugEnabled(true);
		Logger.debug("Print DEBUG output: %d", mRandom.nextLong());
		Logger.warnning("Print WARN output: %d", mRandom.nextLong());
		Logger.info("Print INFO output: %d", mRandom.nextLong());
		Logger.error("Print ERROR output: %d", mRandom.nextLong());
		
		Logger.setDebugEnabled(false);
		Logger.debug("Print DEBUG output: %d", mRandom.nextLong());
		Logger.warnning("Print WARN output: %d", mRandom.nextLong());
		Logger.info("Print INFO output: %d", mRandom.nextLong());
		Logger.error("Print ERROR output: %d", mRandom.nextLong());
	}

}
