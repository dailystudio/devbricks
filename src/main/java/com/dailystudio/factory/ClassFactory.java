package com.dailystudio.factory;

public class ClassFactory<T> extends AbsClassFactory<T, Class<?>> {

	@Override
	protected T newObject(Class<?> params) {
		if (params == null) {
			return null;
		}
		
		return createObjectForClass(params);
	}

}
