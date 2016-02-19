package com.dailystudio.development;

import java.io.File;

import com.dailystudio.BuildConfig;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class Logger {
	
	private static enum LogToken {
		LOG_D,
		LOG_W,
		LOG_I,
		LOG_E,
	}
	
	private static final String SUPPRESS_FILE = "dslog_suppress"; 
	private static final String FORCE_FILE = "dslog_force"; 

	private static final String UNKNOWN_METHOD = "UnknownMethod";
	private static final String UNKNOWN_CLASS = "UnknownClass";
	private static final int TRACE_BASE_INDEX = 3;
	
	private static final String DEBUG_MSG_TEMPL = "%s(): %s";
	
	private static volatile boolean sLogDebugEnabled = BuildConfig.DEBUG;
	
	private static void output(String format, LogToken token, Object... args) {
		final String compose = String.format(DEBUG_MSG_TEMPL, 
				getCallingMethodName(2), format);
		
		if (token == null) {
			token = LogToken.LOG_D;
		}
			
		if (token == LogToken.LOG_D) {
			Log.d(getCallingSimpleClassName(2), String.format(compose, args));
		} else if (token == LogToken.LOG_W) {
			Log.w(getCallingSimpleClassName(2), String.format(compose, args));
		} else if (token == LogToken.LOG_I) {
			Log.i(getCallingSimpleClassName(2), String.format(compose, args));
		} else if (token == LogToken.LOG_E) {
			Log.e(getCallingSimpleClassName(2), String.format(compose, args));
		}
	}
	
	public static void setDebugEnabled(boolean enabled) {
		sLogDebugEnabled = enabled;
	}
	
	public static boolean isDebugEnabled() {
		return sLogDebugEnabled;
	}
	
	public static boolean isDebugSuppressed() {
		return isDebugSuppressed(SUPPRESS_FILE);
	}
	
	public static boolean isPackageDebugSuppressed(String pkg) {
		if (TextUtils.isEmpty(pkg)) {
			return false;
		}
		
		StringBuilder sb = new StringBuilder(SUPPRESS_FILE);
		sb.append('.');
		sb.append(pkg);
		
		return isDebugSuppressed(sb.toString());
	}
	
	private static boolean isDebugSuppressed(String supTagFile) {
		return isTagFileExisted(supTagFile);
	}
	
	public static boolean isDebugForced() {
		return isDebugSuppressed(FORCE_FILE);
	}
	
	public static boolean isPackageDebugForced(String pkg) {
		if (TextUtils.isEmpty(pkg)) {
			return false;
		}
		
		StringBuilder sb = new StringBuilder(FORCE_FILE);
		sb.append('.');
		sb.append(pkg);
		
		return isDebugForced(sb.toString());
	}
	
	private static boolean isDebugForced(String forceTagFile) {
		return isTagFileExisted(forceTagFile);
	}
	
	private static boolean isTagFileExisted(String tagfile) {
		if (TextUtils.isEmpty(tagfile)) {
			return false;
		}
		
		final String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) 
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			final File externalStorage = 
					Environment.getExternalStorageDirectory();
			if (externalStorage != null) {
				final File supfile = new File(externalStorage, tagfile);
				if (supfile.exists()
						&& supfile.isFile()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void info(String format, Object... args) {
		output(format, LogToken.LOG_I, args);
	}

	public static void debug(String format, Object... args) {
		if (sLogDebugEnabled) {
			output(format, LogToken.LOG_D, args);
		}
	}
	
	public static void warnning(String format, Object... args) {
		output(format, LogToken.LOG_W, args);
	}
	
	public static void error(String format, Object... args) {
		output(format, LogToken.LOG_E, args);
	}
	
	public static StackTraceElement getCallingElement(int traceLevel) {
		if (traceLevel < 0) {
			return null;
		}
		
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		if (elements == null) {
			return null;
		}

//		dumpStackTraceElements(elements);
		
		final int length = elements.length;
		if ((TRACE_BASE_INDEX + traceLevel) >= length) {
			return null;
		}
		
		
		return elements[TRACE_BASE_INDEX + traceLevel];
	}
	
/*	private static void dumpStackTraceElements(StackTraceElement[] elements) {
		if (elements == null) {
			return;
		}
		
		final int length = elements.length;
		for (int i = 0; i < length; i++) {
			Log.d(Logger.class.getSimpleName(),
					String.format("dumpStackTraceElements(): [%03d]: element[%s]",
							i, elements[i]));
		}
	}
*/
	public static String getCallingMethodName() {
		return getCallingMethodName(1);
	}
	
	public static String getCallingMethodName(int traceLevel) {
		StackTraceElement element = getCallingElement(traceLevel + 1);
		if (element == null) {
			return UNKNOWN_METHOD;
		}
		
		return element.getMethodName();
	}
	
	public static String getCallingClassName() {
		return getCallingClassName(1);
	}

	public static String getCallingSimpleClassName() {
		return getCallingSimpleClassName(1);
	}
	
	public static String getCallingSimpleClassName(int traceLevel) {
		String className = getCallingClassName(traceLevel + 1);
		if (className == null) {
			return UNKNOWN_CLASS;
		}
		
		Class<?> kls = null;
		try {
			kls = Class.forName(className);
		} catch (ClassNotFoundException e) {
			kls = null;
		}
		
		if (kls == null) {
			return UNKNOWN_CLASS;
		}
		
		return kls.getSimpleName();
	}


	public static String getCallingClassName(int traceLevel) {
		StackTraceElement element = getCallingElement(traceLevel + 1);
		if (element == null) {
			return UNKNOWN_CLASS;
		}
		
		return element.getClassName();
	}
	
}
