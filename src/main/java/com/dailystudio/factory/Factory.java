package com.dailystudio.factory;

import java.util.Map;

import com.dailystudio.GlobalContextWrapper;

public abstract class Factory<T, P> extends GlobalContextWrapper {
	
    private static Map<Class<?>,Object> sInstanceMap =
        new java.util.HashMap<Class<?>,Object>();
    
    protected Factory() {
    	initMembers();
    }
    
    protected void initMembers() {
    }

    public static synchronized <E> E getInstance(Class<E> clazz) {
        E instance = clazz.cast(sInstanceMap.get(clazz));
        if (instance != null) {
            return instance;
        }
        
        try {
            instance = clazz.cast(clazz.newInstance());
        } catch (InstantiationException exc) {
            throw new IllegalArgumentException(exc);
        } catch (IllegalAccessException exc) {
            throw new IllegalArgumentException(exc);
        } catch (ClassCastException exc) {
            throw new IllegalArgumentException(exc);
        }
        
        sInstanceMap.put(clazz, instance);
        
        return instance;
    }
    
    public T createObject(P params) {
    	return newObject(params);
    }
    
    abstract protected T newObject(P params);
 
    public synchronized void reset() {
    	
    }

}