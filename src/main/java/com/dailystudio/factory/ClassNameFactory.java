package com.dailystudio.factory;

import android.content.Context;


public class ClassNameFactory<T> extends AbsClassFactory<T, String> {

	@Override
	protected T newObject(String params) {
		if (params == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		String klassName = parseClassName(params);
		if (klassName == null) {
			return null;
		}
		
		Class<?> klass = null;
		try {
			klass = Class.forName(klassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			
			klass = null;
		}
		
//		Logger.debug("klass(%s)", klass);
		if (klass == null) {
			return null;
		}
		
		return createObjectForClass(klass);
	}

	String parseClassName(String params) {
		if (params == null) {
			return null;
		}
		
		final Context context = getContext();
		if (context == null) {
			return null;
		}
		
		String klassName = null;
		if (params.startsWith(".")) {
			klassName = String.format("%s.%s",
					context.getPackageName(),
					params.substring(1));
		} else {
			klassName = params;
		}
		
		return klassName;
	}

}
