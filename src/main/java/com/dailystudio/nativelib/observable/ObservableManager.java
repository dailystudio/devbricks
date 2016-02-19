package com.dailystudio.nativelib.observable;

import java.util.List;

import com.dailystudio.manager.Manager;
import com.dailystudio.manager.SingletonManager;

public class ObservableManager extends SingletonManager<Class<? extends NativeObservable>, NativeObservable> {

	public static synchronized ObservableManager getInstance() {
		return Manager.getInstance(ObservableManager.class);
	}
	
	@Override
	public void addObject(NativeObservable object) {
		super.addObject(object);
		
		if (object != null) {
			object.onCreate();
		}
	}
	
	@Override
	public void removeObject(NativeObservable object) {
		super.removeObject(object);

		if (object != null) {
			object.onDestroy();
		}
	}
	
	@Override
	public NativeObservable removeObjectByKey(Class<? extends NativeObservable> key) {
		NativeObservable observable = super.removeObjectByKey(key);
		if (observable != null) {
			observable.onDestroy();
		}
		
		return observable;
	}	
	
	@Override
	public void clearObjects() {
		List<NativeObservable> observables = listObjects();
		super.clearObjects();

		if (observables != null) {
			for (NativeObservable o: observables) {
				o.onDestroy();
			}
		}
	}
	
	public static void registerObservable(NativeObservable observable) {
		if (observable == null) {
			return;
		}
		
		ObservableManager obmgr = ObservableManager.getInstance();
		if (obmgr == null) {
			return;
		}
		
		obmgr.addObject(observable);
	}
	
	public static void unregisterObservable(Class<? extends NativeObservable> klass) {
		if (klass == null) {
			return;
		}
		
		ObservableManager obmgr = ObservableManager.getInstance();
		if (obmgr == null) {
			return;
		}
		
		obmgr.removeObjectByKey(klass);
	}
	
	public static void clearObservables() {
		ObservableManager obmgr = ObservableManager.getInstance();
		if (obmgr == null) {
			return;
		}
		
		obmgr.clearObjects();
	}

	public static NativeObservable getObservable(Class<? extends NativeObservable> klass) {
		ObservableManager obmgr = ObservableManager.getInstance();
		if (obmgr == null) {
			return null;
		}
		
		NativeObservable observable = obmgr.getObject(klass);
		if (observable == null) {
			observable = ObservableFactory.createObservable(klass);
			if (observable != null) {
				registerObservable(observable);
			}
		}
		
		return observable;
	}

	public static List<NativeObservable> listObservables() {
		ObservableManager obmgr = ObservableManager.getInstance();
		if (obmgr == null) {
			return null;
		}
		
		return obmgr.listObjects();
	}

	public static int countObservables() {
		ObservableManager obmgr = ObservableManager.getInstance();
		if (obmgr == null) {
			return 0;
		}
		
		return obmgr.getCount();
	}

}
