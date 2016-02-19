package com.dailystudio.app.utils;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

	public static final String DEFAULT_ARRAY_DELIMITER = ",";
	
	public static String stringArrayToString(String[] strings, String delimiter) {
		if (strings == null || delimiter == null) {
			return null;
		}

		final int N = strings.length;
		if (N <= 0) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		
		int i;
		for (i = 0; i < N; i++) {
			builder.append(strings[i]);
			if (i != (N - 1)) {
				builder.append(delimiter);
			}
		}
		
		return builder.toString();
	}
	
	public static String[] toStringArray(String string, String delimiter) {
		if (string == null || delimiter == null) {
			return null;
		}
		
		return string.split(delimiter);
	}
	
	public static Integer[] toIntegerArray(int[] array) {
		if (array == null) {
			return null;
		}
		
		List<Integer> newArray = new ArrayList<Integer>();
		
		final int N = array.length;
		
		for (int i = 0; i < N; i++) {
			newArray.add(array[i]);
		}
		
		return newArray.toArray(new Integer[0]);
	}
	
	public static String intArrayToString(int[] array) {
		return intArrayToString(array, DEFAULT_ARRAY_DELIMITER);
	}
	
	public static String intArrayToString(int[] array, String delimiter) {
		if (array == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		final int N = array.length;
		
		for (int i = 0; i < N; i++) {
			sb.append(String.valueOf(array[i]));
			if (i != (N - 1)) {
				sb.append(delimiter);
			}
		}
		
		return sb.toString();
	}
	
	public static String longArrayToString(long[] array) {
		return longArrayToString(array, DEFAULT_ARRAY_DELIMITER);
	}
	
	public static String longArrayToString(long[] array, String delimiter) {
		if (array == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		final int N = array.length;
		
		for (int i = 0; i < N; i++) {
			sb.append(String.valueOf(array[i]));
			if (i != (N - 1)) {
				sb.append(delimiter);
			}
		}
		
		return sb.toString();
	}
	
	public static String floatArrayToString(float[] array) {
		return floatArrayToString(array, DEFAULT_ARRAY_DELIMITER);
	}
	
	public static String floatArrayToString(float[] array, String delimiter) {
		if (array == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		final int N = array.length;
		
		for (int i = 0; i < N; i++) {
			sb.append(String.valueOf(array[i]));
			if (i != (N - 1)) {
				sb.append(delimiter);
			}
		}
		
		return sb.toString();
	}
	
	public static String arrayToString(Object[] array) {
		return arrayToString(array, DEFAULT_ARRAY_DELIMITER);
	}
	
	public static String arrayToString(Object[] array, String delimiter) {
		if (array == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		final int N = array.length;
		
		for (int i = 0; i < N; i++) {
			sb.append(array[i]);
			if (i != (N - 1)) {
				sb.append(delimiter);
			}
		}
		
		return sb.toString();
	}
	
	public static String byteArrayToHex(byte[] a) {
		return byteArrayToHex(a, false);
	}

	public static String byteArrayToHex(byte[] a, boolean withSplitter) {
		if (a == null) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		
		final int N = a.length;
		
		for(int i = 0; i < N; i++) {
			sb.append(String.format("%02x", a[i]&0xff));
			if (withSplitter && i != (N - 1)) {
				sb.append(':');
			}
		}
		
		return sb.toString();
	}

}
