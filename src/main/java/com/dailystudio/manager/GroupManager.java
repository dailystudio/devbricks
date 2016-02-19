package com.dailystudio.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupManager<G, K, T extends IGroupObject<G, K>> extends SingletonManager<K, T> {
	
	protected Map<G, List<T>> mGroupMaps;

	@Override
	protected void initMembers() {
		super.initMembers();
		
		mGroupMaps = new HashMap<G, List<T>>();
	}

    @Override
	public void addObject(T object) {
    	if (object == null) {
    		return;
    	}

    	G group = object.getGroup();
    	if (group == null) {
    		return;
    	}
    	
    	K key = object.getSingletonKey();
    	
    	T oldObject = getObject(key);
    	
    	super.addObject(object);
    	
    	synchronized (mGroupMaps) {
	    	List<T> objectsInGroup = mGroupMaps.get(group);
	    	if (objectsInGroup == null) {
	    		objectsInGroup = new ArrayList<T>();
	    		
	    		mGroupMaps.put(group, objectsInGroup);
	    		
	    		onGroupAdded(group);
	    	}
	    	
			if (objectsInGroup != null) {
				if (oldObject != null) {
					objectsInGroup.remove(oldObject);
				}
				
				objectsInGroup.add(object);
			}
		}
	}

	@Override
	public void removeObject(T object) {
    	if (object == null) {
    		return;
    	}
    	
    	G group = object.getGroup();
    	if (group == null) {
    		return;
    	}

    	synchronized (mGroupMaps) {
	    	List<T> objectsInGroup = mGroupMaps.get(group);
	    	if (objectsInGroup != null) {
	    		objectsInGroup.remove(object);
	    		
	    		if (objectsInGroup.size() == 0) {
	    			mGroupMaps.remove(group);
		    		
		    		onGroupRemoved(group);
	    		}
	    	}
		}
    	
    	super.removeObject(object);
	}
	
	@Override
	public T removeObjectByKey(K key) {
		T object = getObject(key);
		if (object == null) {
			return null;
		}
		
    	G group = object.getGroup();
    	if (group == null) {
    		return null;
    	}
    	
    	synchronized (mGroupMaps) {
	    	List<T> objectsInGroup = mGroupMaps.get(group);
	    	if (objectsInGroup != null) {
	    		
	    		objectsInGroup.remove(object);
	    		
	    		if (objectsInGroup.size() == 0) {
	    			mGroupMaps.remove(group);
		    		
		    		onGroupRemoved(group);
	    		}
	    	}
    	}
    	
		return super.removeObjectByKey(key);
	}
	
	@Override
	public void clearObjects() {
		synchronized (mGroupMaps) {
			mGroupMaps.clear();
			
			onGroupsCleared();
		}
		
		super.clearObjects();
	}

	public List<G> listGroups() {
		synchronized (mGroupMaps) {
			return new ArrayList<G>(mGroupMaps.keySet());
		}
	}
	
	public int getGroupCount() {
		synchronized (mGroupMaps) {
			return mGroupMaps.size();
		}
	}
	
	public List<T> getObjectsInGroup(G group) {
		if (group == null) {
			return null;
		}
		
		List<T> objects = null;
		
		synchronized (mGroupMaps) {
			objects = mGroupMaps.get(group);
		}
		
		if (objects == null) {
			return null;
		}

		return new ArrayList<T>(objects);
	}
	
	public int getCountInGroup(G group) {
		List<T> objectsInGroup = getObjectsInGroup(group);

		if (objectsInGroup == null) {
			return 0;
		}
	
		return objectsInGroup.size();
	}
	
	public void removeGroup(G group) {
		if (group == null) {
			return;
		}
		
		List<T> objectsInGroup = getObjectsInGroup(group);
		
		if (objectsInGroup != null) {
			for (T object: objectsInGroup) {
				super.removeObject(object);
			}
		}
			
		synchronized (mGroupMaps) {
			mGroupMaps.remove(group);
			
			onGroupRemoved(group);
		}
		
		return;
	}
	
	public T getFirstObjectInGroup(G group) {
    	List<T> objectsInGroup = getObjectsInGroup(group);
    	if (objectsInGroup == null || objectsInGroup.size() <= 0) {
    		return null;
    	}		
    	
		return objectsInGroup.get(0);
	}
	
	public T getLastObjectInGroup(G group) {
    	List<T> objectsInGroup = getObjectsInGroup(group);
    	if (objectsInGroup == null || objectsInGroup.size() <= 0) {
    		return null;
    	}		
    	
		return objectsInGroup.get(objectsInGroup.size() - 1);
	}
	
	protected void onGroupAdded(G group) {
	}
	
	protected void onGroupRemoved(G group) {
	}
	
	protected void onGroupsCleared() {
	}
	
}
