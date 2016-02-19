package com.dailystudio.dataobject;

import java.util.List;

public class DatabaseObjectCopier {
	
	public void fillObject(DatabaseObject dstObject, DatabaseObject srcObject) {
		if (dstObject == null || srcObject == null) {
			return;
		}
		
		final List<Column> nonEmptyColumns = srcObject.listNonEmptyColumns();
		if (nonEmptyColumns == null) {
			return;
		}
		
		Object value = null;
		for (Column col: nonEmptyColumns) {
			value = srcObject.getValue(col);
			if (value == null) {
				continue;
			}
			
			dstObject.setValue(col.getName(), value);
		}
	}
	
	public DatabaseObject cloneObject(DatabaseObject srcObject, 
			Class<? extends DatabaseObject> dstObjectClass) {
		return cloneObject(srcObject, dstObjectClass, DatabaseObject.VERSION_LATEST);
	}
	
	public DatabaseObject cloneObject(DatabaseObject srcObject, 
			Class<? extends DatabaseObject> dstObjectClass,
			int dstObjectVersion) {
		DatabaseObject dstObject =
			DatabaseObjectFactory.createDatabaseObject(dstObjectClass, dstObjectVersion);
		if (dstObject == null) {
			return null;
		}
		
		fillObject(dstObject, srcObject);
		
		return dstObject;
	}
	
}
