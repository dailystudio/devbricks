package com.dailystudio.manager;

import java.util.HashMap;
import java.util.Map;

public class SingletonManager<K, T extends ISingletonObject<K>> extends Manager<T> {

	protected Map<K, T> mObjectMaps;
	
    @Override
	protected void initMembers() {
		super.initMembers();
		
		mObjectMaps = new HashMap<K, T>();
	}

    @Override
	public void addObject(T object) {
    	if (object == null) {
    		return;
    	}

    	K key = object.getSingletonKey();
    	if (key == null) {
    		return;
    	}
    	
    	T oldObject = null;
    	
    	synchronized (mObjectMaps) {
    		oldObject = mObjectMaps.get(key);
		}
    	
    	if (oldObject != null) {
    		super.removeObject(oldObject);
    	}
    	
    	super.addObject(object);
    	
    	synchronized (mObjectMaps) {
    		mObjectMaps.put(key, object);
    	}
	};
	
	@Override
	public void removeObject(T object) {
    	if (object == null) {
    		return;
    	}
    	
    	K key = object.getSingletonKey();
    	if (key == null) {
    		return;
    	}
    	
    	synchronized (mObjectMaps) {
        	T mappedObject = mObjectMaps.get(key);
        	if (mappedObject == object) {
            	mObjectMaps.remove(key);
        	}
		}

    	super.removeObject(object);
	}
	
	public T removeObjectByKey(K key) {
    	if (key == null) {
    		return null;
    	}
    	
    	T object = null;
    	synchronized (mObjWatchers) {
        	object = mObjectMaps.get(key);
		}
    	
    	if (object == null) {
    		return null;
    	}
    	
    	super.removeObject(object);
    	
    	synchronized (mObjWatchers) {
    		mObjectMaps.remove(key);
    	}
    	
    	return object;
	}

	@Override
	public void clearObjects() {
    	synchronized (mObjWatchers) {
    		mObjectMaps.clear();
    	}
    	
		super.clearObjects();
	}

	public T getObject(K key) {
		if (key == null) {
			return null;
		}
	
    	synchronized (mObjWatchers) {
    		return mObjectMaps.get(key);
    	}
	}
	
}
