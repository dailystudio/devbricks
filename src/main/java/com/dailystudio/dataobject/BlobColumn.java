package com.dailystudio.dataobject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class BlobColumn extends Column {
	
	private final static String TYPE_NAME = "BLOB";
	
	public BlobColumn(String colName) {
		this(colName, true);
	}
	
	public BlobColumn(String colName, boolean allowNull) {
		this(colName, allowNull, false);
	}
	
	public BlobColumn(String colName, boolean allowNull, boolean isPrimary) {
		super(colName, TYPE_NAME, allowNull, isPrimary, VERSION_1);
	}

	public BlobColumn(String colName, int version) {
		this(colName, true, version);
	}
	
	public BlobColumn(String colName, boolean allowNull, int version) {
		this(colName, allowNull, false, version);
	}
	
	public BlobColumn(String colName, boolean allowNull, boolean isPrimary, int version) {
		super(colName, TYPE_NAME, allowNull, isPrimary, version);
	}

	@Override
	protected void setValue(ContentValues container, Object value) {
		if (container == null || value == null) {
			return;
		}
		
		container.put(getName(), (byte[])value);
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
		
		return container.getAsByteArray(key);
	}

	@Override
	protected boolean matchColumnType(Object value) {
		return (value instanceof byte[]);
	}

	@Override
	protected void attachValueTo(Intent intent, ContentValues container) {
		if (container == null || intent == null) {
			return;
		}
		
		byte[] baVal = container.getAsByteArray(getName());
		if (baVal == null) {
			return;
		}
		
		intent.putExtra(getName(), baVal);
	}

	@Override
	protected void parseValueFrom(Cursor cursor, ContentValues container) {
		if (cursor == null || container == null) {
			return;
		}
		
		try {
			final int columnIndex = cursor.getColumnIndexOrThrow(getName());
			
			if (cursor.isNull(columnIndex) == false) {
				byte[] baVal = cursor.getBlob(columnIndex);
				
				if (baVal != null) {
					setValue(container, baVal);
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String convertValueToString(Object value) {
		if (value instanceof byte[] == false) {
			return null;
		}
		
		byte[] bytes = (byte[])value;
    	if (bytes == null || bytes.length <= 0) {
    		return null;
    	}
    	
		String sVal = String.format("X\'%s\'", byteArrayToHexString(bytes));
		if (sVal == null) {
			return null;
		}
		
		return sVal;
	}
	
    public static String byteArrayToHexString(byte bytes[]) {
    	if (bytes == null || bytes.length <= 0) {
    		return null;
    	}
    	
        StringBuffer s = new StringBuffer();
        
        for (int j = 0; j < bytes.length; j++) {
            s.append(Integer.toHexString((int)((bytes[j]>>4)&0x0f)).toUpperCase());
            s.append(Integer.toHexString((int)(bytes[j]&0x0f)).toUpperCase());
        }
        
        return new String(s);
    }
}
