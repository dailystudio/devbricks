package com.dailystudio.test;

import java.util.List;

import android.test.AndroidTestCase;
import android.widget.Adapter;

public class Asserts {
	
	public static void assertEquals(List<?> expected, List<?> actual) {
		AndroidTestCase.assertNotNull(actual);
		
		if (expected == null) {
			AndroidTestCase.assertEquals(0, actual.size());
			return;
		}
		
		AndroidTestCase.assertEquals(expected.size(), actual.size());
		
		final int count = expected.size();
		for (int i = 0; i < count; i++) {
			AndroidTestCase.assertEquals(expected.get(i), actual.get(i));
		}
	}

	public static void assertEquals(byte[] expected, byte[] actual) {
		AndroidTestCase.assertNotNull(actual);
		
		if (expected == null) {
			AndroidTestCase.assertEquals(0, actual.length);
			return;
		}
		
		AndroidTestCase.assertEquals(expected.length, actual.length);
		
		final int count = expected.length;
		for (int i = 0; i < count; i++) {
			AndroidTestCase.assertEquals(expected[i], actual[i]);
		}
	}

	public static void assertEquals(long[] expected, long[] actual) {
		AndroidTestCase.assertNotNull(actual);
		
		if (expected == null) {
			AndroidTestCase.assertEquals(0, actual.length);
			return;
		}
		
		AndroidTestCase.assertEquals(expected.length, actual.length);
		
		final int count = expected.length;
		for (int i = 0; i < count; i++) {
			AndroidTestCase.assertEquals(expected[i], actual[i]);
		}
	}

	public static void assertEquals(int[] expected, int[] actual) {
		AndroidTestCase.assertNotNull(actual);
		
		if (expected == null) {
			AndroidTestCase.assertEquals(0, actual.length);
			return;
		}
		
		AndroidTestCase.assertEquals(expected.length, actual.length);
		
		final int count = expected.length;
		for (int i = 0; i < count; i++) {
			AndroidTestCase.assertEquals(expected[i], actual[i]);
		}
	}

	public static void assertEquals(Object[] expected, Object[] actual) {
		AndroidTestCase.assertNotNull(actual);
		
		if (expected == null) {
			AndroidTestCase.assertEquals(0, actual.length);
			return;
		}
		
		AndroidTestCase.assertEquals(expected.length, actual.length);
		
		final int count = expected.length;
		for (int i = 0; i < count; i++) {
			AndroidTestCase.assertEquals(expected[i], actual[i]);
		}
	}

	public static void assertEquals(Adapter expected, Adapter actual) {
		AndroidTestCase.assertNotNull(actual);
		
		if (expected == null) {
			AndroidTestCase.assertEquals(0, actual.getCount());
			return;
		}
		
		AndroidTestCase.assertEquals(expected.getCount(), actual.getCount());
		
		final int count = expected.getCount();
		for (int i = 0; i < count; i++) {
			AndroidTestCase.assertEquals(
					expected.getItem(i), actual.getItem(i));
		}
	}

}
