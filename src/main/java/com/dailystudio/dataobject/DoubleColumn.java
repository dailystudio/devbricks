package com.dailystudio.dataobject;

import java.text.DecimalFormat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class DoubleColumn extends Column {
	
	private final static String TYPE_NAME = "DOUBLE";
	
	public DoubleColumn(String colName) {
		this(colName, true);
	}
	
	public DoubleColumn(String colName, boolean allowNull) {
		this(colName, allowNull, false);
	}
	
	public DoubleColumn(String colName, boolean allowNull, boolean isPrimary) {
		super(colName, TYPE_NAME, allowNull, isPrimary, VERSION_1);
	}

	public DoubleColumn(String colName, int version) {
		this(colName, true, version);
	}
	
	public DoubleColumn(String colName, boolean allowNull, int version) {
		this(colName, allowNull, false, version);
	}
	
	public DoubleColumn(String colName, boolean allowNull, boolean isPrimary, int version) {
		super(colName, TYPE_NAME, allowNull, isPrimary, version);
	}

	@Override
	protected void setValue(ContentValues container, Object value) {
		if (container == null || value == null) {
			return;
		}
		
		container.put(getName(), (Double)value);
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
		
		
		return container.getAsDouble(key);
	}

	@Override
	protected boolean matchColumnType(Object value) {
		return (value instanceof Double);
	}

	@Override
	protected void attachValueTo(Intent intent, ContentValues container) {
		if (container == null || intent == null) {
			return;
		}
		
		Double dVal = container.getAsDouble(getName());
		if (dVal == null) {
			return;
		}
		
		intent.putExtra(getName(), dVal);
	}

	@Override
	protected void parseValueFrom(Cursor cursor, ContentValues container) {
		if (cursor == null || container == null) {
			return;
		}
		
		try {
			final int columnIndex = cursor.getColumnIndexOrThrow(getName());
			
			if (cursor.isNull(columnIndex) == false) {
				double dVal = cursor.getDouble(columnIndex);
			
				setValue(container, dVal);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String convertValueToString(Object value) {
		if (value instanceof Double == false) {
			return null;
		}
		
		Double dVal = (Double)value;
		
		return new DecimalFormat("0.####################").format(dVal.doubleValue());
	}

}
