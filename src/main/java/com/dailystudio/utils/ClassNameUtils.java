package com.dailystudio.utils;

public class ClassNameUtils {
    
    public static String getClassName(Class<?> klass) {
    	if (klass == null) {
    		return null;
    	}
    	
    	String className = klass.getName();
    	if (className == null) {
    		return null;
    	}
    	
    	final int splitIndex = className.lastIndexOf('.');
    	
    	return className.substring(splitIndex + 1);
    }
    
    public static String getPackageName(Class<?> klass) {
    	if (klass == null) {
    		return null;
    	}
    	
    	String className = klass.getName();
    	if (className == null) {
    		return null;
    	}
    	
    	final int splitIndex = className.lastIndexOf('.');
    	
    	return className.substring(0, splitIndex);
    }

}
