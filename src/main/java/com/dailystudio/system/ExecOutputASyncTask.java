package com.dailystudio.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.dailystudio.development.Logger;

class ExecOutputASyncTask extends ExecIOASyncTask {

	private InputStream mInputStream;
	
	private boolean mIgnoreEmptyLines;
	
	public ExecOutputASyncTask(String tag, InputStream input) {
		super(tag);
		
		mInputStream = input;
		
		mIgnoreEmptyLines = false;
	}
	
	public void setIgnoreEmptyLines(boolean ignore) {
		mIgnoreEmptyLines = ignore;
	}
	
	void printOutput() {
		Logger.debug("mInputStream = %s", mInputStream);
		final InputStream input = mInputStream;
		if (input == null) {
			return;
		}
		
		final InputStreamReader reader = new InputStreamReader(input);
		final BufferedReader lineReader = new BufferedReader(reader);
		
		String line = null;
		try {
			while ((line = lineReader.readLine()) != null) {
				if (mEndFlag) {
					break;
				}
				
				if (mIgnoreEmptyLines && line.length() <= 0) {
					continue;
				}
				
				Logger.info("%s: [%s]", mTag, line);
			}

			lineReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		printOutput();
		return null;
	}

	@Override
	public void stop() {
//		printOutput();
		super.stop();
	}
	
}
