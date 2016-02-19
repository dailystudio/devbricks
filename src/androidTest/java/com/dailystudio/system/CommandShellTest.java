package com.dailystudio.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;

import com.dailystudio.development.Logger;
import com.dailystudio.system.CommandShell.PendingReturnHandler;
import com.dailystudio.test.ActivityTestCase;
import com.dailystudio.test.R;

public class CommandShellTest extends ActivityTestCase {
	
	private Object mLockObject = new Object();
	private int mPendingRet = 0;
	private int mPendingId = 0;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testComposeRetureValue() {
		assertEquals(0x00000000, CommandShell.composeReturnValue(CommandShell.ERR_NONE, 0x00));
		assertEquals(0x000000FF, CommandShell.composeReturnValue(CommandShell.ERR_NONE, 0xFF));
		assertEquals(0x00010000, CommandShell.composeReturnValue(CommandShell.ERR_NULL_ARGS, 0x00));
		assertEquals(0x00020000, CommandShell.composeReturnValue(CommandShell.ERR_SHELL_NOT_FOUND, 0x00));
		assertEquals(0x00030000, CommandShell.composeReturnValue(CommandShell.ERR_FAILED, 0x00));
		assertEquals(0x00040000, CommandShell.composeReturnValue(CommandShell.ERR_INTERRUPTED, 0x00));
		assertEquals(0x00050000, CommandShell.composeReturnValue(CommandShell.ERR_PENDING, 0x00));
		assertEquals(0x00010000, CommandShell.composeReturnValue(CommandShell.ERR_NULL_ARGS, 0xFF));
		assertEquals(0x00020000, CommandShell.composeReturnValue(CommandShell.ERR_SHELL_NOT_FOUND, 0xCD));
		assertEquals(0x00030000, CommandShell.composeReturnValue(CommandShell.ERR_FAILED, 0xAB));
		assertEquals(0x00040000, CommandShell.composeReturnValue(CommandShell.ERR_INTERRUPTED, 0x12));
		assertEquals(0x00050012, CommandShell.composeReturnValue(CommandShell.ERR_PENDING, 0x12));
	}
	
	public void testComposeErrorCode() {
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(0x000000FF));
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(0x00001234));
		assertEquals(CommandShell.ERR_NULL_ARGS, CommandShell.getErrorCode(0x00010000));
		assertEquals(CommandShell.ERR_SHELL_NOT_FOUND, CommandShell.getErrorCode(0x00020000));
		assertEquals(CommandShell.ERR_FAILED, CommandShell.getErrorCode(0x00030000));
		assertEquals(CommandShell.ERR_INTERRUPTED, CommandShell.getErrorCode(0x00040000));
		assertEquals(CommandShell.ERR_NULL_ARGS, CommandShell.getErrorCode(0x000100FF));
		assertEquals(CommandShell.ERR_SHELL_NOT_FOUND, CommandShell.getErrorCode(0x00020000));
		assertEquals(CommandShell.ERR_FAILED, CommandShell.getErrorCode(0x0003FFFF));
		assertEquals(CommandShell.ERR_INTERRUPTED, CommandShell.getErrorCode(0x0004ABCD));
	}
	
	public void testComposeExitValue() {
		assertEquals(0xFF, CommandShell.getExitValue(0x000000FF));
		assertEquals(0x1234, CommandShell.getExitValue(0x00001234));
		assertEquals(0xAA, CommandShell.getExitValue(0x000500AA));
		assertEquals(0x3333, CommandShell.getExitValue(0x00053333));
		assertEquals(0, CommandShell.getExitValue(0x00010000));
		assertEquals(0, CommandShell.getExitValue(0x00020000));
		assertEquals(0, CommandShell.getExitValue(0x00030000));
		assertEquals(0, CommandShell.getExitValue(0x00040000));
		assertEquals(0, CommandShell.getExitValue(0x000100FF));
		assertEquals(0, CommandShell.getExitValue(0x0002FFFF));
		assertEquals(0, CommandShell.getExitValue(0x0003ABCD));
		assertEquals(0, CommandShell.getExitValue(0x00045678));
	}
	
	public void testExecShellCommand() {
		int ret = CommandShell.execAndWaitFor("ls -l");
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(ret));
		assertEquals(0, CommandShell.getExitValue(ret));
	}
	
	public void testExecShellCommands() {
		int ret = CommandShell.execAndWaitFor(
				new String[]{ "echo A", "echo B", "echo C"});
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(ret));
		assertEquals(0, CommandShell.getExitValue(ret));
	}

	public void testExecAWelcomeScript() {
		copyScriptFromRaw(R.raw.test_welcome, "test_welcome.sh");
		
		int ret = CommandShell.execAndWaitFor(
				new String[]{getAbsolutePath("test_welcome.sh")});
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(ret));
		assertEquals(0, CommandShell.getExitValue(ret));
	}

	public void testExitValueOfExec() {
		copyScriptFromRaw(R.raw.test_exit_with_n1, "test_exit_with_n1.sh");
		
		/*
		 * XXX: run as a super user, otherwise we could exec
		 * 		script in /data/data/ directory even its permission
		  * 	is 777
		 */
		int ret = CommandShell.execAndWaitFor(
				new String[]{getAbsolutePath("test_exit_with_n1.sh")}, true);
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(ret));
		assertEquals(1, CommandShell.getExitValue(ret));
	}
	
	public void testExecErrorScript() {
		copyScriptFromRaw(R.raw.test_run_with_error, "test_run_with_error.sh");
		
		/*
		 * XXX: run as a super user, otherwise we could exec
		 * 		script in /data/data/ directory even its permission
		  * 	is 777
		 */
		int ret = CommandShell.execAndWaitFor(
				new String[]{getAbsolutePath("test_run_with_error.sh")}, true);
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(ret));
		assertEquals(127, CommandShell.getExitValue(ret));
	}
	
	public void testExecAsSuperUsser() {
		int ret = CommandShell.execAndWaitFor(
				new String[]{ "cd /data/app", "ls -l"}, true);
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(ret));
		assertEquals(0, CommandShell.getExitValue(ret));
	}
	
	public void testExecWithPendingReturn() {
		copyScriptFromRaw(R.raw.test_sleep_10s, "test_sleep_10s.sh");
		/*
		 * XXX: run as a super user, otherwise we could exec
		 * 		script in /data/data/ directory even its permission
		  * 	is 777
		 */
		int ret = CommandShell.exec(
				getAbsolutePath("test_sleep_10s.sh"), true,
				mHandler);

		assertEquals(CommandShell.ERR_PENDING, CommandShell.getErrorCode(ret));
		
		mPendingId = CommandShell.getExitValue(ret);
		Logger.debug("ret(0x%08x), mPendingId(%d)", ret, mPendingId);
		
		try {
			synchronized (mLockObject) {
				Logger.debug("[WAIT FOR RESULT]");
				mLockObject.wait();
				Logger.debug("[RETURN TO CHECK]");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertEquals(CommandShell.ERR_NONE, CommandShell.getErrorCode(mPendingRet));
		assertEquals(11, CommandShell.getExitValue(mPendingRet));
	}
	
	private String getAbsolutePath(String targetFile) {
		final File baseDir = mTargetContext.getFilesDir();
		assertNotNull(baseDir);
		
		final File destFile = new File(baseDir, targetFile);
		assertNotNull(destFile);
		
		return destFile.getAbsolutePath();
	}
	
	private void copyScriptFromRaw(int resId, String targetFile) {
		assertEquals(true, (resId > 0));
		assertNotNull(targetFile);
		
		InputStream input = mContext.getResources().openRawResource(resId);
		assertNotNull(input);
		
		FileOutputStream output = null;
		try {
			output = mTargetContext.openFileOutput(targetFile, Context.MODE_WORLD_READABLE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			output = null;
		}
		
		assertNotNull(output);
		
		InputStreamReader reader = new InputStreamReader(input);
		assertNotNull(reader);
		
		BufferedReader bufReader = new BufferedReader(reader);
		assertNotNull(bufReader);
		
		OutputStreamWriter writer = new OutputStreamWriter(output);
		assertNotNull(writer);
		
		BufferedWriter bufWriter = new BufferedWriter(writer);
		assertNotNull(bufWriter);
		
		try {
			byte[] buffer = new byte[30];
			int read = 0;
			while ((read = input.read(buffer, 0, 30)) > 0) {
				output.write(buffer, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			bufReader.close();

			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		markAsExecutable(targetFile);
	}
	
	private void markAsExecutable(String targetFile) {
		assertNotNull(targetFile);
		
		String[] cmds = {
			String.format("chmod 777 %s", getAbsolutePath(targetFile)),
		};
		
		CommandShell.exec(cmds);
	}
	
	private PendingReturnHandler mHandler = new PendingReturnHandler() {
		
		@Override
		public void onReturned(int pendingId, int ret) {
			Logger.debug("mPendingId(%d), pendingId(%d), ret(%d)",
					mPendingId,
					pendingId,
					ret);
			if (mPendingId != pendingId) {
				return;
			}
			
			mPendingRet = ret;
			
			synchronized (mLockObject) {
				mLockObject.notifyAll();
			}
			
		}
		
	};
	
}
