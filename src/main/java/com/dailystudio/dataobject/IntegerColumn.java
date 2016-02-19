package com.dailystudio.dataobject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class IntegerColumn extends Column {
	
	private final static String TYPE_NAME = "INTEGER";
	
	public IntegerColumn(String colName) {
		this(colName, true);
	}
	
	public IntegerColumn(String colName, boolean allowNull) {
		this(colName, allowNull, false);
	}
	
	public IntegerColumn(String colName, boolean allowNull, boolean isPrimary) {
		super(colName, TYPE_NAME, allowNull, isPrimary, VERSION_1);
	}

	public IntegerColumn(String colName, int version) {
		this(colName, true, version);
	}
	
	public IntegerColumn(String colName, boolean allowNull, int version) {
		this(colName, allowNull, false, version);
	}
	
	public IntegerColumn(String colName, boolean allowNull, boolean isPrimary, int version) {
		super(colName, TYPE_NAME, allowNull, isPrimary, version);
	}

	@Override
	protected void setValue(ContentValues container, Object value) {
		if (container == null || value == null) {
			return;
		}
		
		container.put(getName(), (Integer)value);
	}
	
	@Override
	Object getValue(ContentValues container) {
		if (container == null) {
			return null;
		}
		
		final String key = getName();
		
		if (container.containsKey(key) == false) {
			return null;
		}
		
		return container.getAsInteger(key);
	}

	@Override
	protected boolean matchColumnType(Object value) {
		return (value instanceof Integer);
	}

	@Override
	protected void attachValueTo(Intent intent, ContentValues container) {
		if (intent == null || container == null) {
			return;
		}
		
		Integer iVal = container.getAsInteger(getName());
		if (iVal == null) {
			return;
		}
		
		intent.putExtra(getName(), iVal);
	}

	@Override
	protected void parseValueFrom(Cursor cursor, ContentValues container) {
		if (cursor == null || container == null) {
			return;
		}
		
		try {
			final int columnIndex = cursor.getColumnIndexOrThrow(getName());

			if (cursor.isNull(columnIndex) == false) {
				int iVal = cursor.getInt(columnIndex);
			
				setValue(container, iVal);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String convertValueToString(Object value) {
		if (value instanceof Integer == false) {
			return null;
		}
		
		Integer iVal = (Integer)value;
		
		return String.valueOf(iVal.intValue());
	}

}
