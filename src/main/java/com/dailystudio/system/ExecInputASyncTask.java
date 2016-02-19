package com.dailystudio.system;

import java.io.IOException;
import java.io.OutputStream;

import com.dailystudio.development.Logger;

class ExecInputASyncTask extends ExecIOASyncTask {

	private OutputStream mOutputStream;
	private String[] mCommands;
	
	public ExecInputASyncTask(String tag, OutputStream output, String[] cmds) {
		super(tag);
		
		mOutputStream = output;
		mCommands = cmds;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		final OutputStream output = mOutputStream;
		if (output == null) {
			return null;
		}
		
		if (mCommands == null) {
			return null;
		}
		
		final int count = mCommands.length;
		
		StringBuilder cmdBuilder = null;
		for (int i = 0; i < count && !mEndFlag; i++) {
			Logger.info("%s: write cmd[%s]", mTag, mCommands[i]);
			
			cmdBuilder = new StringBuilder(mCommands[i]);
			cmdBuilder.append("\n");
			try {
				output.write(cmdBuilder.toString().getBytes());
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
