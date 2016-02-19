package com.dailystudio.manager;

public interface IGroupObject<G, K> extends ISingletonObject<K> {

	public G getGroup();
	
}
