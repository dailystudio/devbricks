package com.dailystudio.utils;

import com.dailystudio.test.ActivityTestCase;

public class ClassNameUtilsTest extends ActivityTestCase {
	
	private class TestClass {
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetPackageName() {
		String expected = "com.dailystudio.utils";
		assertEquals(expected, ClassNameUtils.getPackageName(this.getClass()));
		assertEquals(expected, ClassNameUtils.getPackageName(TestClass.class));
	}
	
	
	public void testGetClassNameName() {
		String expected = null;
		
		expected = "ClassNameUtilsTest";
		assertEquals(expected, ClassNameUtils.getClassName(this.getClass()));
		expected = "ClassNameUtilsTest$TestClass";
		assertEquals(expected, ClassNameUtils.getClassName(TestClass.class));
	}
}
