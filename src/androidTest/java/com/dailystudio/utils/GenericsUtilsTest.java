package com.dailystudio.utils;

import com.dailystudio.test.ActivityTestCase;

public class GenericsUtilsTest extends ActivityTestCase {
	
	private class TestBaseClassA<T> {
		
	}
	
	private class TestClassA extends TestBaseClassA<Integer> {
		
	}
	
	private class TestBaseClassB<T, P> {
		
	}
	
	private class TestClassB extends TestBaseClassB<Long, Double> {
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetSuperClassGenricType() {
		Class<?> typeClass = null;

		TestClassA iObjA = new TestClassA();
		assertNotNull(iObjA);
		
		typeClass = GenericsUtils.getSuperClassGenricType(iObjA.getClass());
		assertEquals(Integer.class, typeClass);

		TestClassB iObjB = new TestClassB();
		assertNotNull(iObjA);
		
		typeClass = GenericsUtils.getSuperClassGenricType(iObjB.getClass());
		assertEquals(Long.class, typeClass);
		
		typeClass = GenericsUtils.getSuperClassGenricType(iObjB.getClass(), 0);
		assertEquals(Long.class, typeClass);
		
		typeClass = GenericsUtils.getSuperClassGenricType(iObjB.getClass(), 1);
		assertEquals(Double.class, typeClass);
	}
	
}
