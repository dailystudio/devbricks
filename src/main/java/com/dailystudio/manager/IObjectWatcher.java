package com.dailystudio.manager;

public interface IObjectWatcher<T> {
	
	public void onObjectAdded(T object);
	public void onObjectRemoved(T object);
	
	public void onObjectsCleared(T[] object);
	
	public boolean matchWatcher(T object);

}
