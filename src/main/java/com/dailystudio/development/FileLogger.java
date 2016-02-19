package com.dailystudio.development;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

public class FileLogger {
	
	private static final String DEBUG_MSG_TEMPL = "%s(): %s";
	private static final String FORMAT_TEPML_DATETIME = "yyyy-MM-dd HH:mm:ss:SSS";
	private static final char SPLITTER = '\t';
	private static final char LINE_TERM = '\n';
	
	private String mFileName = null;
	private OutputStream mOutputStream = null;
	
	public FileLogger(String fname) {
		mFileName = fname;
	}
	
	private boolean checkOrOpenOutput() {
		if (mOutputStream != null) {
			return true;
		}
		
		if (mFileName == null) {
			return false;
		}
		
		File outputFile = new File(mFileName);
		
		if (outputFile.exists() == false) {
			boolean created = false;
			try {
				created = outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				created = false;
			}
			
			if (created == false) {
				return false;
			}
		}
		
		try {
			mOutputStream = new FileOutputStream(outputFile, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mOutputStream = null;
		}
		
		return (mOutputStream != null);
	}
	
	private int output(String format, Object... args) {
		if (mOutputStream == null) {
			if (checkOrOpenOutput() == false) {
				return 0;
			}
		}
		
		final String compose = String.format(DEBUG_MSG_TEMPL, 
				Logger.getCallingMethodName(2), format);
				
		final String time = 
			new SimpleDateFormat(FORMAT_TEPML_DATETIME).format(
					System.currentTimeMillis());
		
		StringBuilder builder = new StringBuilder(time);
		
		builder.append(SPLITTER);
		builder.append(Logger.getCallingSimpleClassName(2));
		builder.append(SPLITTER);
		builder.append(String.format(compose, args));
		builder.append(LINE_TERM);
		
		byte[] bytes = builder.toString().getBytes();
		if (bytes == null) {
			return 0;
		}
		
		boolean success = false;
		if (mOutputStream != null) {
			try {
				mOutputStream.write(bytes);
				mOutputStream.flush();
				success = true;
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
			}
		}
		
		return (success ? bytes.length : 0);
	}

	public int info(String format, Object... args) {
		return output(format, args);
	}

	public int debug(String format, Object... args) {
		return output(format, args);
	}
	
	public int warnning(String format, Object... args) {
		return output(format, args);
	}

	public void close() {
		if (mOutputStream != null) {
			try {
				mOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		mOutputStream = null;
	}
	
}
