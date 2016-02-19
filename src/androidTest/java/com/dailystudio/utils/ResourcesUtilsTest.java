package com.dailystudio.utils;

import java.io.File;

import com.dailystudio.system.CommandShell;
import com.dailystudio.test.ActivityTestCase;
import com.dailystudio.test.R;
import com.dailystudio.utils.ResourcesUtils;

public class ResourcesUtilsTest extends ActivityTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetFilePath() {
		String expected = getAbsolutePath("test_welcome.sh");
		assertNotNull(expected);
		String actual = ResourcesUtils.getFilePath(mTargetContext, "test_welcome.sh");
		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	public void testCopyRawToFile() {
		boolean success = false;
		
		success = ResourcesUtils.copyRawToFile(mContext, R.raw.test_welcome, mTargetContext, "test_welcome.sh");
		assertEquals(true, success);
		
		markAsExecutable("test_welcome.sh");
		
		int ret = CommandShell.exec(
				new String[] {getAbsolutePath("test_welcome.sh")});
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(ret));
		assertEquals(0, CommandShell.getExitValue(ret));
	}

	public void testCopyRawToExecutable() {
		boolean success = false;
		
		success = ResourcesUtils.copyRawToExecutable(mContext, R.raw.test_welcome, mTargetContext, "test_welcome.sh");
		assertEquals(true, success);
		
		int ret = CommandShell.exec(
				new String[] {getAbsolutePath("test_welcome.sh")});
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(ret));
		assertEquals(0, CommandShell.getExitValue(ret));
	}

	private String getAbsolutePath(String targetFile) {
		final File baseDir = mTargetContext.getFilesDir();
		assertNotNull(baseDir);
		
		final File destFile = new File(baseDir, targetFile);
		assertNotNull(destFile);
		
		return destFile.getAbsolutePath();
	}
	
	private void markAsExecutable(String targetFile) {
		assertNotNull(targetFile);
		
		String[] cmds = {
			String.format("chmod 777 %s", getAbsolutePath(targetFile)),
		};
		
		CommandShell.exec(cmds);
	}
	
}
