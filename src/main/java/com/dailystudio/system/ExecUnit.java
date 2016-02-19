package com.dailystudio.system;

import java.io.InputStream;
import java.io.OutputStream;

import com.dailystudio.system.CommandShell.PendingReturnHandler;

class ExecUnit {
	
	int pendingId;
	
	private Process mProcess;
	private PendingReturnHandler mHandler;
	
	private String[] mCommands;
	
	private ExecOutputASyncTask mStdoutTask;
	private ExecOutputASyncTask mStderrTask;
	private ExecInputASyncTask mStdinTask;
	
	private InputStream mErrStream;
	private InputStream mOutputStream;
	
	ExecUnit(Process process, String[] commands, PendingReturnHandler handler) {
		initMembers();
		
		mProcess = process;
		mCommands = commands;
		mHandler = handler;
	}
	
	private void initMembers() {
		mProcess = null;
		
		mCommands = null;
		
		mStdoutTask = null;
		mStderrTask = null;
		mStdinTask = null;
		
		mErrStream = null;
		mOutputStream = null;
	}
	
	public Process getProcess() {
		return mProcess;
	}
	
	public PendingReturnHandler getHandler() {
		return mHandler;
	}
	
	public InputStream getErrStream() {
		return mErrStream;
	}
	
	public InputStream getOutputStream() {
		return mOutputStream;
	}
	
	public void execute(boolean handleOutputs) {
		if (mProcess == null) {
			return;
		}

		final InputStream stdout = mProcess.getInputStream();
		final InputStream stderr = mProcess.getErrorStream();
		final OutputStream stdin = mProcess.getOutputStream();
		
		if (handleOutputs) {
			if (stdout != null) {
				mStdoutTask = new ExecOutputASyncTask("stdout", stdout);
				if (mStdoutTask != null) {
					mStdoutTask.start();
				}
			}
			
			
			if (stderr != null) {
				mStderrTask = new ExecOutputASyncTask("stderr", stderr);
				if (mStderrTask != null) {
					mStderrTask.setIgnoreEmptyLines(true);
					mStderrTask.start();
				}
			}
			
		} else {
			mOutputStream = stdout;
			mErrStream = stderr;
		}

		if (stdin != null) {
			mStdinTask = new ExecInputASyncTask("stdin", stdin, mCommands);
			if (mStdinTask != null) {
				mStdinTask.start();
			}
		}
	}
	
	public void cancel() {
		if (mStdoutTask != null) {
			mStdoutTask.stop();
			mStdoutTask = null;
		}
		
		if (mStderrTask != null) {
			mStderrTask.stop();
			mStderrTask = null;
		}

		if (mStdinTask != null) {
			mStdinTask.stop();
			mStdinTask = null;
		}
	}
	
	public void waitFor() {
		if (mStdoutTask != null) {
			mStdoutTask.waitForStop();
			mStdoutTask = null;
		}
		
		if (mStderrTask != null) {
			mStderrTask.waitForStop();
			mStderrTask = null;
		}
		if (mStdinTask != null) {
			mStdinTask.waitForStop();
			mStdinTask = null;
		}
	}
	
}

