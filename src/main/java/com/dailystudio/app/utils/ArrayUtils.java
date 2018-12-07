package com.dailystudio.app.utils;

import android.text.TextUtils;

import com.dailystudio.development.Logger;

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
		
		List<Integer> newArray = new ArrayList<>();
		
		final int N = array.length;
		
		for (int i = 0; i < N; i++) {
			newArray.add(array[i]);
		}
		
		return newArray.toArray(new Integer[0]);
	}

	public static Double[] toDoubleArray(double[] array) {
		if (array == null) {
			return null;
		}

		List<Double> newArray = new ArrayList<>();

		final int N = array.length;

		for (int i = 0; i < N; i++) {
			newArray.add(array[i]);
		}

		return newArray.toArray(new Double[0]);
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

	/**
	 * XXX: Code from com.google.common.primitives.Bytes
	 *
	 * Returns the index of the first appearance of the value {@code target} in {@code array}.
	 *
	 * @param array an array of {@code byte} values, possibly empty
	 * @param target a primitive {@code byte} value
	 * @return the least index {@code i} for which {@code array[i] == target}, or {@code -1} if no
	 *     such index exists.
	 */
	public static int indexOf(byte[] array, byte target) {
		return indexOf(array, target, 0, array.length);
	}

	public static int indexOf(byte[] array, byte target, int start, int end) {
		for (int i = start; i < end; i++) {
			if (array[i] == target) {
				return i;
			}
		}
		return -1;
	}

	public static int bytesToInteger(byte[] a) {
		return bytesToInteger(a, 0);
	}

	public static String bytesToString(byte[] a) {
		return bytesToString(a, true);
	}

	public static String bytesToString(byte[] a, boolean trim) {
		if (a == null) {
			return null;
		}

		int index = ArrayUtils.indexOf(a, (byte)0);

		return new String(a, 0, index).trim();
	}

	public static int bytesToInteger(byte[] a, int startIndex) {
		if (a == null ||
				startIndex + 3 >= a.length) {
			return 0;
		}

		return (a[startIndex] & 0xff)
				| ((a[startIndex + 1] << 8) & 0xff00)
				| ((a[startIndex + 2] << 24) >>> 8)
				| (a[startIndex + 3] << 24); // byte[] to int
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
			sb.append(String.format("%02X", a[i]&0xff));
			if (withSplitter && i != (N - 1)) {
				sb.append(':');
			}
		}
		
		return sb.toString();
	}

	public static byte[] hexStringToByteArray(String hexString) {
		if (TextUtils.isEmpty(hexString)) {
			return null;
		}

		final int N = hexString.length();

		if (N % 2 != 0) {
			Logger.warn("length of hex string is invalid. len = %d", N);

			return null;
		}

		StringBuilder builder = new StringBuilder("0");
		byte[] bytes = new byte[N / 2];
		for (int i = 0; i < N; i += 2) {
			bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
					+ Character.digit(hexString.charAt(i + 1), 16));
/*
			Logger.debug("current byte[%c%c]: byte = %02X ",
					hexString.charAt(i), hexString.charAt(i + 1),
					bytes[i / 2]);
*/
		}

		return bytes;
	}

	public static <T> List<T> arrayToList(T[] arrays) {
		if (arrays == null
				|| arrays.length <= 0) {
			return null;
		}

		List<T> list = new ArrayList<>();

		for (T item: arrays) {
			list.add(item);
		}

		return list;
	}

}
