package com.dailystudio.factory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;


public abstract class AbsClassFactory<T, P> extends Factory<T, P> {


	@SuppressWarnings("unchecked")
	public T createObjectForClass(Class<?> klass) {
		if (klass == null) {
			return null;
		}
		
		Object object = null;
		Constructor<?> constructor = null;
			
		try {
			constructor = klass.getConstructor(Context.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
				
			constructor = null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
				
			constructor = null;
		} 
			
//		Logger.debug("constructor(%s)", constructor);

		try {
			if (constructor == null) {
				object = klass.newInstance();
			} else {
				constructor.setAccessible(true);
				
				final Context context = getContext();
				if (context != null) {
					object = constructor.newInstance(new Object[] { context });
				}
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			
			object = null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			
			object = null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			
			object = null;
		}

		return (T)object;
	}

}
