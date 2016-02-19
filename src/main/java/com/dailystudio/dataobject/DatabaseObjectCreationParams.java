package com.dailystudio.dataobject;

class DatabaseObjectCreationParams {

	
	Class<? extends DatabaseObject> objectClass;
	int version;
	
	DatabaseObjectCreationParams(Class<? extends DatabaseObject> klass) {
		this(klass, Column.VERSION_LATEST);
	}
	
	DatabaseObjectCreationParams(Class<? extends DatabaseObject> klass, int ver) {
		objectClass = klass;
		version = ver;
	}

}
