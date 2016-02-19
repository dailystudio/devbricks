package com.dailystudio.dataobject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class LongColumn extends Column {
	
	private final static String TYPE_NAME = "LONG";
	
	public LongColumn(String colName) {
		this(colName, true);
	}
	
	public LongColumn(String colName, boolean allowNull) {
		this(colName, allowNull, false);
	}
	
	public LongColumn(String colName, boolean allowNull, boolean isPrimary) {
		super(colName, TYPE_NAME, allowNull, isPrimary, VERSION_1);
	}

	public LongColumn(String colName, int version) {
		this(colName, true, version);
	}
	
	public LongColumn(String colName, boolean allowNull, int version) {
		this(colName, allowNull, false, version);
	}
	
	public LongColumn(String colName, boolean allowNull, boolean isPrimary, int version) {
		super(colName, TYPE_NAME, allowNull, isPrimary, version);
	}

	@Override
	protected void setValue(ContentValues container, Object value) {
		if (container == null || value == null) {
			return;
		}
		
		container.put(getName(), (Long)value);
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
		
		return container.getAsLong(key);
	}

	@Override
	protected boolean matchColumnType(Object value) {
		return (value instanceof Long);
	}

	@Override
	protected void attachValueTo(Intent intent, ContentValues container) {
		if (container == null || intent == null) {
			return;
		}
		
		Long lVal = container.getAsLong(getName());
		if (lVal == null) {
			return;
		}
		
		intent.putExtra(getName(), lVal);
	}

	@Override
	protected void parseValueFrom(Cursor cursor, ContentValues container) {
		if (cursor == null || container == null) {
			return;
		}
		
		try {
			final int columnIndex = cursor.getColumnIndexOrThrow(getName());
			
			if (cursor.isNull(columnIndex) == false) {
				long lVal = cursor.getLong(columnIndex);
				
				setValue(container, lVal);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String convertValueToString(Object value) {
		if (value instanceof Long == false) {
			return null;
		}
		
		Long lVal = (Long)value;
		
		return String.valueOf(lVal.longValue());
	}

}
