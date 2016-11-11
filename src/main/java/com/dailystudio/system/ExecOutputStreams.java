package com.dailystudio.system;

import java.io.InputStream;

public class ExecOutputStreams {

	public InputStream errStream;
	public InputStream outputStream;
	
	ExecUnit mExecUnit;
	
	public void close() {
		if (mExecUnit == null) {
			return;
		}
		
		mExecUnit.cancel();
	}
	
}
