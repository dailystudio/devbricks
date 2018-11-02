package com.dailystudio.test;

import java.util.List;

import android.widget.Adapter;
import junit.framework.Assert;

public class Asserts {
	
	public static void assertEquals(List<?> expected, List<?> actual) {
		Assert.assertNotNull(actual);
		
		if (expected == null) {
			Assert.assertEquals(0, actual.size());
			return;
		}

		Assert.assertEquals(expected.size(), actual.size());
		
		final int count = expected.size();
		for (int i = 0; i < count; i++) {
			Assert.assertEquals(expected.get(i), actual.get(i));
		}
	}

	public static void assertEquals(byte[] expected, byte[] actual) {
		Assert.assertNotNull(actual);
		
		if (expected == null) {
			Assert.assertEquals(0, actual.length);
			return;
		}
		
		Assert.assertEquals(expected.length, actual.length);
		
		final int count = expected.length;
		for (int i = 0; i < count; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
	}

	public static void assertEquals(long[] expected, long[] actual) {
		Assert.assertNotNull(actual);
		
		if (expected == null) {
			Assert.assertEquals(0, actual.length);
			return;
		}
		
		Assert.assertEquals(expected.length, actual.length);
		
		final int count = expected.length;
		for (int i = 0; i < count; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
	}

	public static void assertEquals(int[] expected, int[] actual) {
		Assert.assertNotNull(actual);
		
		if (expected == null) {
			Assert.assertEquals(0, actual.length);
			return;
		}
		
		Assert.assertEquals(expected.length, actual.length);
		
		final int count = expected.length;
		for (int i = 0; i < count; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
	}

	public static void assertEquals(Object[] expected, Object[] actual) {
		Assert.assertNotNull(actual);
		
		if (expected == null) {
			Assert.assertEquals(0, actual.length);
			return;
		}
		
		Assert.assertEquals(expected.length, actual.length);
		
		final int count = expected.length;
		for (int i = 0; i < count; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
	}

	public static void assertEquals(Adapter expected, Adapter actual) {
		Assert.assertNotNull(actual);
		
		if (expected == null) {
			Assert.assertEquals(0, actual.getCount());
			return;
		}
		
		Assert.assertEquals(expected.getCount(), actual.getCount());
		
		final int count = expected.getCount();
		for (int i = 0; i < count; i++) {
			Assert.assertEquals(
					expected.getItem(i), actual.getItem(i));
		}
	}

}
