package com.dailystudio.system;

import android.os.AsyncTask;

import java.io.IOException;

import com.dailystudio.development.Logger;

public class CommandShell {
	
	public static interface PendingReturnHandler {
		
		public void onReturned(int pendingId, int ret);
		
	}
	
	private static final int ERR_MASK_BIT = 16;
	
	public static final int ERR_NONE = 0x00;
	public static final int ERR_NULL_ARGS = 0x01;
	public static final int ERR_SHELL_NOT_FOUND = 0x02;
	public static final int ERR_FAILED = 0x03;
	public static final int ERR_INTERRUPTED = 0x04;
	public static final int ERR_PENDING = 0x05;
	
	private static final int EXEC_NULL_ARGS = composeReturnValue(ERR_NULL_ARGS, 0x00);
	private static final int EXEC_FAILED = composeReturnValue(ERR_FAILED, 0x00);
	private static final int EXEC_SHELL_NOT_FOUND = composeReturnValue(ERR_SHELL_NOT_FOUND, 0x00);
	
	private volatile static int sPendingIdSeed = 0;
	
	private static class PendingReturnASyncTask extends AsyncTask<ExecUnit, Void, Integer> {

		@Override
		protected Integer doInBackground(ExecUnit... params) {
			if (params == null || params.length <= 0) {
				return 0;
			}
			
			ExecUnit unit = params[0];
			
			return waitForResult(unit);
		}
		
	}

	public static int execAndWaitFor(String command) {
		return execAndWaitFor(command, false);
	}

	public static int execAndWaitFor(String[] commands) {
		return execAndWaitFor(commands, false);
	}
	
	public static int execAndWaitFor(String command, boolean asSuperUsser) {
		return execAndWaitFor(new String[] { command }, asSuperUsser);
	}
	
	public static int execAndWaitFor(String[] commands, boolean asSuperUsser) {
		return exec(commands, asSuperUsser, true, null);
	}
	
	public static int exec(String command) {
		return exec(command, false);
	}

	public static int exec(String command, PendingReturnHandler handler) {
		return exec(command, false, handler);
	}

	public static int exec(String command, boolean asSuperUsser) {
		return exec(new String[] { command }, asSuperUsser);
	}
	
	static int exec(String command, boolean asSuperUsser, 
			PendingReturnHandler handler) {
		return exec(new String[] { command }, asSuperUsser, false, handler);
	}
	
	public static int exec(String[] commands) {
		return exec(commands, false);
	}
	
	public static int exec(String[] commands, PendingReturnHandler handler) {
		return exec(commands, false, handler);
	}
	
	public static int exec(String[] commands, boolean asSuperUsser) {
		return exec(commands, asSuperUsser, false, null);
	}
	
	public static int exec(String[] commands, boolean asSuperUsser,
			PendingReturnHandler handler) {
		return exec(commands, asSuperUsser, false, handler);
	}
	
	public static int exec(String[] commands, ExecOuputStreams streams) {
		return exec(commands, false, streams);
	}
	
	public static int exec(String[] commands, PendingReturnHandler handler, 
			ExecOuputStreams streams) {
		return exec(commands, false, handler, streams);
	}
	
	public static int exec(String[] commands, boolean asSuperUsser,
			ExecOuputStreams streams) {
		return exec(commands, asSuperUsser, false, null, streams);
	}
	
	public static int exec(String[] commands, boolean asSuperUsser,
			PendingReturnHandler handler, ExecOuputStreams streams) {
		return exec(commands, asSuperUsser, false, handler, streams);
	}
	
	static int exec(String[] commands, boolean asSuperUsser, boolean waitFor, 
			PendingReturnHandler handler) {
		return exec(commands, asSuperUsser, waitFor, handler, null);
	}
	
	static int exec(String[] commands, boolean asSuperUsser, boolean waitFor, 
			PendingReturnHandler handler, ExecOuputStreams streams) {
		printCmds(commands);
		
		if (commands == null) {
			return EXEC_NULL_ARGS;
		}
		
		String shellCommand = null;
		if (asSuperUsser) {
			shellCommand = CommandFinder.findSuCommand();
		} else {
			shellCommand = CommandFinder.findShCommand();
		}
		
		if (shellCommand == null) {
			return EXEC_SHELL_NOT_FOUND;
		}
		
		Runtime runtime = Runtime.getRuntime();
		if (runtime == null) {
			return EXEC_FAILED;
		}
		
		Process process = null;
		
		try {
			process = runtime.exec(shellCommand);
		} catch (IOException e) {
			e.printStackTrace();

			process = null;
		}

		if (process == null) {
			return EXEC_FAILED;
		}
		
		ExecUnit unit = null;
		
		unit = new ExecUnit(process, commands, handler);
		
		unit.execute((streams == null));
		
		if (streams != null) {
			streams.mExecUnit = unit;
			streams.outputStream = unit.getOutputStream();
			streams.errStream = unit.getErrStream();
		}
		
		if (waitFor == false) {
			if (handler == null) {
				return composeReturnValue(ERR_NONE, 0);
			}
			
			PendingReturnASyncTask pendingTask = new PendingReturnASyncTask();
			
			unit.pendingId = sPendingIdSeed++;
			
			pendingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, unit);

			return composeReturnValue(ERR_PENDING, unit.pendingId);
		}
		
		return waitForResult(unit);
	}
	
	static int waitForResult(ExecUnit unit) {
		if (unit == null) {
			return EXEC_NULL_ARGS;
		}
		
		final PendingReturnHandler handler = unit.getHandler();

		final Process process = unit.getProcess();
		if (process == null) {
			if (handler != null) {
				handler.onReturned(unit.pendingId, EXEC_FAILED);
			}
			
			return EXEC_FAILED;
		}
		
		int exitValue = 0;
		int err = ERR_NONE;
		try {
			unit.waitFor();
			exitValue = process.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			
			err = ERR_INTERRUPTED;
		} finally {
			unit.cancel();
		}
		
		process.destroy();

		int ret = composeReturnValue(err, exitValue);
		
		Logger.debug("err(%d), exitValue(%d) = ret(%d)",
				err,
				exitValue,
				ret);

		if (handler != null) {
			handler.onReturned(unit.pendingId, ret);
		}
		
		return ret;
	}
	
	public static int getExitValue(int ret) {
		int err = getErrorCode(ret);
		int exitValue = (ret & 0xFFFF);
		
		if (err != ERR_NONE
				&& err != ERR_PENDING) {
			return 0;
		}
		
		return exitValue;
	}
	
	public static int getErrorCode(int ret) {
		return ((ret >> ERR_MASK_BIT) & 0xFFFF);
	}
	
	static int composeReturnValue(int error, int exitValue) {
		int higher = (error & 0xFFFF);
		int lower = (exitValue & 0xFFFF);
		
		if (higher != ERR_NONE
				&& higher != ERR_PENDING) {
			lower = 0x0;
		}
		return (higher << ERR_MASK_BIT | lower);
	}
	
	private static void printCmds(String[] cmds) {
		if (cmds == null) {
			return;
		}
		
		final int count = cmds.length;
		if (count <= 0) {
			return;
		}
		
		for (int i = 0; i < count; i++) {
			Logger.debug("cmds[%d]: %s", i, cmds[i]);
		}
	}
	
}
