package com.dailystudio.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dailystudio.system.CommandShell;

import android.content.Context;
import android.content.res.Resources;

public class ResourcesUtils {

	private final static int COPY_BUFFER_SIZE = (1024 * 16);
	
	public static boolean copyRawToFile(Context srcContext, int resId, String fname) {
		return copyRawToFile(srcContext, resId, null, fname);
	}
	
	public static boolean copyRawToExecutable(Context srcContext, int resId, String fname) {
		return copyRawToExecutable(srcContext, resId, null, fname);
	}

	public static boolean copyRawToExecutable(Context srcContext, int resId, Context dstContext, String fname) {
		boolean success = copyRawToFile(srcContext, resId, dstContext, fname);
		if (success == false) {
			return false;
		}
		
		if (dstContext == null) {
			dstContext = srcContext;
		}
		
		final String dstFile = getFilePath(dstContext, fname);
		if (dstFile == null) {
			return false;
		}
		
		return markAsExecutable(dstFile);
	}

	public static boolean copyRawToFile(Context srcContext, int resId, Context dstContext, String fname) {
		if (srcContext == null || fname == null) {
			return false;
		}
		
		if (dstContext == null) {
			dstContext = srcContext;
		}
		
		Resources res = srcContext.getResources();
		if (res == null) {
			return false;
		}
		
		InputStream input = res.openRawResource(resId);
		if (input == null) {
			return false;
		}		
		
		FileOutputStream output = null;
		try {
			output = dstContext.openFileOutput(fname, Context.MODE_WORLD_READABLE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			output = null;
		}
		
		if (output == null) {
			return false;
		}

		return copyToFile(input, output);
	}
	
	public static String getFilePath(Context context, String fname) {
		if (context == null || fname == null) {
			return null;
			
		}
		
		final File baseDir = context.getFilesDir();
		if (baseDir == null) {
			return null;
		}
		
		final File destFile = new File(baseDir, fname);
		
		return destFile.getAbsolutePath();
	}
	
	static boolean markAsExecutable(String fname) {
		if (fname == null) {
			return false;
		}
		
		String[] cmds = {
			String.format("chmod 777 %s", fname),
		};
		
		int ret = CommandShell.execAndWaitFor(cmds);
		
		return (CommandShell.getErrorCode(ret) == CommandShell.ERR_NONE
				&& CommandShell.getExitValue(ret) == 0);
	}

	public static boolean copyToFile(InputStream input, FileOutputStream output) {
		if (input == null || output == null) {
			return false;
		}
		
		BufferedInputStream istream = new BufferedInputStream(input);
		BufferedOutputStream ostream = new BufferedOutputStream(output);
		byte[] buffer = new byte[COPY_BUFFER_SIZE]; 
		
		int count;  
		try {
			while ((count = istream.read(buffer)) > 0) {  
				ostream.write(buffer, 0, count);  
			}
			ostream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
		
		try {
			istream.close();
			ostream.close();
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
  
		return true;
	}
	
}
