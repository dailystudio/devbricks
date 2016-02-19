package com.dailystudio.system;

import com.dailystudio.development.Logger;

import android.test.AndroidTestCase;

public class CommandFinderTest extends AndroidTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testFindSuCommand() {
		String suCmd = CommandFinder.findSuCommand();
		assertNotNull(suCmd);
		
		Logger.info("su command: %s", suCmd);
	}

	public void testFindShCommand() {
		String shCmd = CommandFinder.findShCommand();
		assertNotNull(shCmd);
		
		Logger.info("sh command: %s", shCmd);
	}

}
