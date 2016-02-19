package com.dailystudio.dataobject;

import com.dailystudio.dataobject.query.Expression;
import com.dailystudio.dataobject.query.ExpressionToken;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class TextColumn extends Column {
	
	private final static String TYPE_NAME = "TEXT";
	
	public TextColumn(String colName) {
		this(colName, true);
	}
	
	public TextColumn(String colName, boolean allowNull) {
		this(colName, allowNull, false);
	}
	
	public TextColumn(String colName, boolean allowNull, boolean isPrimary) {
		super(colName, TYPE_NAME, allowNull, isPrimary, VERSION_1);
	}

	public TextColumn(String colName, int version) {
		this(colName, true, version);
	}
	
	public TextColumn(String colName, boolean allowNull, int version) {
		this(colName, allowNull, false, version);
	}
	
	public TextColumn(String colName, boolean allowNull, boolean isPrimary, int version) {
		super(colName, TYPE_NAME, allowNull, isPrimary, version);
	}

	@Override
	protected void setValue(ContentValues container, Object value) {
		if (container == null || value == null) {
			return;
		}
		
		container.put(getName(), (String)value);
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
		
		return container.getAsString(key);
	}

	@Override
	protected boolean matchColumnType(Object value) {
		return (value instanceof String);
	}

	@Override
	protected void attachValueTo(Intent intent, ContentValues container) {
		if (container == null || intent == null) {
			return;
		}
		
		String sVal = container.getAsString(getName());
		if (sVal == null) {
			return;
		}
		
		intent.putExtra(getName(), sVal);
	}

	@Override
	protected void parseValueFrom(Cursor cursor, ContentValues container) {
		if (cursor == null || container == null) {
			return;
		}
		
		try {
			final int columnIndex = cursor.getColumnIndexOrThrow(getName());
			
			if (cursor.isNull(columnIndex) == false) {
				String sVal = cursor.getString(columnIndex);
				
				if (sVal != null) {
					setValue(container, sVal);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String convertValueToString(Object value) {
		if (value instanceof String == false) {
			return null;
		}
		

		/*
		 * DATE:	2011/10/25
		 * AUTHOR:	Nan YE
		 * CONTENT:	handle \' around text value here to 
		 * 			avoid some mismatching outside 
		 */
		String sVal = String.format("\'%s\'", (String)value);
		if (sVal == null) {
			return null;
		}
		
		return sVal;
	}
	
	public ExpressionToken like(String value) {
		return binaryOperator(
				Expression.OPERATOR_LIKE, value);
	}


}
