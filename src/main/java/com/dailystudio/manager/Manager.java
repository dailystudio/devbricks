package com.dailystudio.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.dailystudio.GlobalContextWrapper;

public abstract class Manager<T> extends GlobalContextWrapper implements Comparable<Manager<?>> {
	
    private static Map<Class<?>,Object> sInstanceMap =
        new java.util.HashMap<Class<?>,Object>();
    private static Map<String,Object> sInstanceNameMap =
        new java.util.HashMap<String,Object>();
    
    private static ArrayList<IManagerWatcher> sMgrWatchers = 
    	new ArrayList<IManagerWatcher>();
    
    protected ArrayList<T> mObjects = null;
    protected ArrayList<IObjectWatcher<T>> mObjWatchers = null;
	
    protected Manager() {
    	mObjects = new ArrayList<T>();
    	mObjWatchers = new ArrayList<IObjectWatcher<T>>();
    	
    	initMembers();
    }
    
    protected void initMembers() {
    }
    
    public static synchronized <E> E getInstance(Class<E> clazz) {
    	E instance = clazz.cast(sInstanceMap.get(clazz));
/*    	Logger.debug("clazz(%s), instance(%s)", 
    			clazz, instance);
*/
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
        
        if (instance instanceof Manager<?>) {
        	Manager<?> manager = (Manager<?>)instance;
        	
            sInstanceMap.put(clazz, manager);
            sInstanceNameMap.put(manager.getName(), manager);
            
	        if (sMgrWatchers != null) {
	        	for (IManagerWatcher watcher: sMgrWatchers) {
	        		watcher.onManagerAdded(manager);
	        	}
	        }
        }
        
        return instance;
    }
    
    public static synchronized void clearManagers() {
    	sInstanceMap.clear();
    	sInstanceNameMap.clear();
    }
    
   public static synchronized List<Manager<?>> listManagers() {
    	Collection<Object> objects = sInstanceMap.values();

    	if (objects == null || objects.size() <= 0) {
    		return null;
    	}
    	
    	List<Manager<?>> managers = new ArrayList<Manager<?>>();
    	for (Object object: objects) {
    		if (object instanceof Manager<?>) {
    			managers.add((Manager<?>)object);
    		}
    	}
 	
    	Collections.sort(managers);
    	
    	return managers;
    }
    
   	public static synchronized void addManagerWatcher(IManagerWatcher watcher) {
   		if (watcher == null) {
   			return;
   		}
   
   		if (sMgrWatchers != null) {
   			sMgrWatchers.add(watcher);
   		}
   	}
   
   	public static synchronized void removeManagerWatcher(IManagerWatcher watcher) {
   		if (watcher == null) {
   			return;
   		}
   
   		if (sMgrWatchers != null) {
   			sMgrWatchers.remove(watcher);
   		}
   	}

   	public void addObject(T object) {
    	if (object == null) {
    		return;
    	}
    	
    	synchronized (mObjects) {
        	mObjects.add(object);
		}
    	
    	synchronized (mObjWatchers) {
        	if (mObjWatchers != null) {
        		for (IObjectWatcher<T> watcher: mObjWatchers) {
        			if (watcher.matchWatcher(object)) {
        				watcher.onObjectAdded(object);
        			}
        		}
        	}
		}
    }
    
    public void removeObject(T object) {
    	if (object == null) {
    		return;
    	}
    	
    	synchronized (mObjects) {
        	mObjects.remove(object);
		}

    	synchronized (mObjWatchers) {
        	if (mObjWatchers != null) {
        		for (IObjectWatcher<T> watcher: mObjWatchers) {
        			if (watcher.matchWatcher(object)) {
        				watcher.onObjectRemoved(object);
        			}
        		}
        	}
		}
    }
    
    @SuppressWarnings("unchecked")
	public void clearObjects() {
    	T[] objsArray = null;
    	
    	synchronized (mObjects) {
        	if (mObjects.size() == 0) {
        		return;
        	}
    	
        	objsArray = (T[])mObjects.toArray();
		}
    	
    	synchronized (sMgrWatchers) {
    		for (IObjectWatcher<T> watcher: mObjWatchers) {
    			watcher.onObjectsCleared(objsArray);
    		}
		}
		
    	synchronized (mObjects) {
    		mObjects.clear();
    	}
    }
    
    public ArrayList<T> listObjects () {
    	synchronized (mObjects) {
        	if (mObjects.size() <= 0) {
        		return null;
        	}
        	
        	return new ArrayList<T>(mObjects);
		}
    }

    public int getCount() {
    	synchronized (mObjects) {
	    	if (mObjects == null) {
	    		return 0;
	    	}
	    	
	    	return mObjects.size();
    	}
    }
    
    public synchronized void addObjectWatcher(IObjectWatcher<T> watcher) {
    	if (watcher == null) {
    		return;
    	}
    
    	if (mObjWatchers != null) {
    		synchronized (mObjWatchers) {
    			mObjWatchers.add(watcher);
    		}
		}
    }
    
    public synchronized void removeObjectWatcher(IObjectWatcher<T> watcher) {
    	if (watcher == null) {
    		return;
    	}
    
    	if (mObjWatchers != null) {
    		synchronized (mObjWatchers) {
        		mObjWatchers.remove(watcher);
			}
    	}
    }

    public synchronized List<IObjectWatcher<T>> listWatchers() {
        return new ArrayList<IObjectWatcher<T>>(mObjWatchers);
    }
    
    public int compareTo(Manager<?> another) {
    	if (another == null) {
    		return 1;
    	}
    	
    	String mName = getName();
    	String oName = another.getName();
    	if (mName != null && oName != null) {
    		return mName.compareTo(oName);
    	} else if (oName == null) {
    		return 1;
    	} else if (mName == null) {
    		return -1;
    	}
    	
    	return 0;
    };
    
    public static synchronized Manager<?> queryInstanceByName(String name) {
    	if (name == null || sInstanceNameMap == null) {
    		return null;
    	}
    	
    	Object instance = sInstanceNameMap.get(name);
    	if (instance == null) {
    		return null;
    	}
    	
    	return (Manager<?>)instance; 
    }
    
    public ArrayList<String> getClassHierarchy() {
    	Class<?> kls = null;
    	
    	ArrayList<String> klsList = new ArrayList<String>();
    	
    	kls = this.getClass();
    	while (kls != null) {
			klsList.add(kls.getSimpleName());
			kls = kls.getSuperclass();
    	};
    	
    	return klsList;
    }
    
    public String getName() {
    	return this.getClass().getSimpleName();
    }
    
}
 