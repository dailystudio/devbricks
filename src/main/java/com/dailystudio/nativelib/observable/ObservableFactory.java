package com.dailystudio.nativelib.observable;

import com.dailystudio.factory.ClassFactory;
import com.dailystudio.factory.Factory;

public class ObservableFactory extends ClassFactory<NativeObservable> {
	
	public synchronized static final ObservableFactory getInstance() {
		return Factory.getInstance(ObservableFactory.class);
	}

	public synchronized static final NativeObservable createObservable(Class<? extends NativeObservable> params) {
		final ObservableFactory factory = ObservableFactory.getInstance();
		if (factory == null) {
			return null;
		}
	
		NativeObservable observable = factory.createObject(params);
		
		return observable;
	}

}
