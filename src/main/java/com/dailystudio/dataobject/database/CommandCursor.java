package com.dailystudio.dataobject.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.AbstractCursor;

class CommandCursor extends AbstractCursor {

	private String mCommand;
	
	private ContentValues mValues;
	private List<String> mColumns;

	public CommandCursor(String cmd) {
		mCommand = cmd;
	
		mValues = new ContentValues();
		mColumns = new ArrayList<String>();
	}
	
	public String getCommand() {
		return mCommand;
	}
	
	public void addColumn(String colName) {
		if (colName == null) {
			return;
		}
		
		if (mColumns == null) {
			return;
		}
		
		if (mColumns.contains(colName) == false) {
			mColumns.add(colName);
		}
	}
	
	public void addColumns(String[] colNames) {
		if (colNames == null) {
			return;
		}
		
		for (String colName: colNames) {
			addColumn(colName);
		}
	}
	
	public void removeColumn(String colName) {
		if (colName == null) {
			return;
		}
		
		if (mColumns == null) {
			return;
		}
		
		mColumns.remove(colName);
		
		if (mValues == null) {
			return;
		}
		
		mValues.remove(colName);
	}
	
	public void clearValue(int column) {
		final String colName = getColumnName(column);
		if (colName == null) {
			return;
		}
		
		if (mValues == null) {
			return;
		}
		
		mValues.remove(colName);
	}
	
	public void putValue(String colName, Double value) {
		if (mColumns == null || mValues == null) {
			return;
		}
		
		if (mColumns.contains(colName) == false) {
			mColumns.add(colName);
		}
		
		mValues.put(colName, value);
	}
	
	public void putValue(String colName, Float value) {
		if (mColumns == null || mValues == null) {
			return;
		}
		
		if (mColumns.contains(colName) == false) {
			mColumns.add(colName);
		}
		
		mValues.put(colName, value);
	}
	
	public void putValue(String colName, Integer value) {
		if (mColumns == null || mValues == null) {
			return;
		}
		
		if (mColumns.contains(colName) == false) {
			mColumns.add(colName);
		}
		
		mValues.put(colName, value);
	}
	
	public void putValue(String colName, Long value) {
		if (mColumns == null || mValues == null) {
			return;
		}
		
		if (mColumns.contains(colName) == false) {
			mColumns.add(colName);
		}
		
		mValues.put(colName, value);
	}
	
	public void putValue(String colName, Short value) {
		if (mColumns == null || mValues == null) {
			return;
		}
		
		if (mColumns.contains(colName) == false) {
			mColumns.add(colName);
		}
		
		mValues.put(colName, value);
	}
	
	public void putValue(String colName, String value) {
		if (mColumns == null || mValues == null) {
			return;
		}
		
		if (mColumns.contains(colName) == false) {
			mColumns.add(colName);
		}
		
		mValues.put(colName, value);
	}
	
	@Override
	public String[] getColumnNames() {
		if (mColumns == null) {
			return null;
		}
		
		return mColumns.toArray(new String[0]);
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public double getDouble(int column) {
		final String colName = getColumnName(column);
		if (colName == null) {
			return 0;
		}
		
		if (mValues.containsKey(colName) == false) {
			return 0;
		}
		
		return mValues.getAsDouble(colName);
	}

	@Override
	public float getFloat(int column) {
		final String colName = getColumnName(column);
		if (colName == null) {
			return 0;
		}
		
		if (mValues.containsKey(colName) == false) {
			return 0;
		}
		
		return mValues.getAsFloat(colName);
	}

	@Override
	public int getInt(int column) {
		final String colName = getColumnName(column);
		if (colName == null) {
			return 0;
		}
		
		if (mValues.containsKey(colName) == false) {
			return 0;
		}
		
		return mValues.getAsInteger(colName);
	}

	@Override
	public long getLong(int column) {
		final String colName = getColumnName(column);
		if (colName == null) {
			return 0;
		}
		
		return mValues.getAsLong(colName);
	}

	@Override
	public short getShort(int column) {
		final String colName = getColumnName(column);
		if (colName == null) {
			return 0;
		}
		
		if (mValues.containsKey(colName) == false) {
			return 0;
		}
		
		return mValues.getAsShort(colName);
	}

	@Override
	public String getString(int column) {
		final String colName = getColumnName(column);
		if (colName == null) {
			return null;
		}
		
		if (mValues.containsKey(colName) == false) {
			return null;
		}
		
		return mValues.getAsString(colName);
	}

	@Override
	public boolean isNull(int column) {
		final String colName = getColumnName(column);
		if (colName == null) {
			return true;
		}
		
		if (mValues == null) {
			return true;
		}
		
		return (!mValues.containsKey(colName));
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex < 0 
				|| columnIndex >= getColumnCount()) {
			return null;
		}
		
		return super.getColumnName(columnIndex);
	}

}
