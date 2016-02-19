package com.dailystudio.factory;

import com.dailystudio.development.Logger;

public abstract class SingletoneFactory<T, P> extends Factory<T, P> {
	
	abstract protected T findObject(P params);
	abstract protected void cacheObject(T object);
	
	@Override
	public T createObject(P params) {
		T object = findObject(params);
		Logger.debug("params(%s), object(%s)",
				params, object);
		
		if (object != null) {
			return object;
		}
		
		object = super.createObject(params);
		if (object != null) {
			cacheObject(object);
		}
		
		return object;
	}

}
