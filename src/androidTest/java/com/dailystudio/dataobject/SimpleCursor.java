package com.dailystudio.dataobject;

import java.util.ArrayList;
import java.util.List;

import android.test.mock.MockCursor;

public class SimpleCursor<T> extends MockCursor {
	
	protected List<T> mValues;
	protected List<String> mColumns;
	
	public SimpleCursor() {
		mValues = new ArrayList<T>();
		mColumns = new ArrayList<String>();
	}
	
	@Override
	public boolean moveToFirst() {
		return true;
	}
	
	@Override
	public boolean moveToLast() {
		return true;
	}
	
	@Override
	public boolean moveToNext() {
		return true;
	}
	
	@Override
	public boolean moveToPrevious() {
		return true;
	}
	
	@Override
	public int getCount() {
		return 1;
	};
	
	@Override
	public int getColumnCount() {
		return 1;
	}
	
	@Override
	public boolean isNull(int columnIndex) {
		return (getColumnName(columnIndex) == null);
	}
	
	public void putColumnValue(String colName, T value) {
		if (colName == null || value == null) {
			return;
		}
		
		if (mValues == null || mColumns == null) {
			return;
		}
		
		mValues.add(value);
		mColumns.add(colName);
	}
	
	@Override
	public int getColumnIndex(String columnName) {
		if (columnName == null) {
			return -1;
		}
		
		if (mColumns == null) {
			return -1;
		}
		
		final int N = mColumns.size();
		if (N <= 0) {
			return -1;
		}
		
		int i;
		for (i = 0; i < N; i++) {
			if (mColumns.get(i).equals(columnName)) {
				break;
			}
		}
		
		return ((i >= N) ? -1 : i);
	}
	
	@Override
	public int getColumnIndexOrThrow(String columnName) {
		int index = getColumnIndex(columnName);
		
		if (index < 0) {
			throw new IllegalArgumentException(
					String.format("no such column(%s)", columnName));
		}
		
		return index;
	}
	
	@Override
	public int getPosition() {
		return 0;
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex < 0) {
			return null;
		}
		
		if (mColumns == null) {
			return null;
		}
		
		final int N = mColumns.size();
		if (N <= 0) {
			return null;
		}
		
		if (columnIndex >= N) {
			return null;
		}
		
		return mColumns.get(columnIndex);
	}

}
