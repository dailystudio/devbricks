package com.dailystudio.dataobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Template {
	
	private List<Column> mColumns = null;
	private HashMap<String, Column> mColumnsMap = null;
	
	public Template() {
		initMembers();
	}
	
	private void initMembers() {
		mColumns = new ArrayList<Column>();
		mColumnsMap = new HashMap<String, Column>();
	}
	
	public synchronized void addColumns(Column[] columns) {
		if (columns != null) {
			for (Column col: columns) {
				if (col == null || col.isValid() == false) {
					continue;
				}
				
				if (mColumns != null) {
					mColumns.add(col);
				}
				
				if (mColumnsMap != null) {
					mColumnsMap.put(col.getName(), col);
				}
			}
		}
	}

	public synchronized void addColumn(Column column) {
		if (column == null || column.isValid() == false) {
			return;
		}
		
		if (mColumns != null) {
			mColumns.add(column);
		}
		
		if (mColumnsMap != null) {
			mColumnsMap.put(column.getName(), column);
		}
	}
	
	public synchronized void removeColumn(Column column) {
		if (column == null) {
			return;
		}
		
		if (mColumns != null) {
			mColumns.remove(column);
		}
		
		if (mColumnsMap != null && column.getName() != null) {
			mColumnsMap.remove(column.getName());
		}
	}
	
	public synchronized List<Column> listColumns() {
		return new ArrayList<Column>(mColumns);
	}
	
	public synchronized Column getColumn(String colName) {
		if (colName == null) {
			return null;
		}
		
		if (mColumnsMap == null) {
			return null;
		}

		return mColumnsMap.get(colName);
	}

	public boolean containsColumn(Column column) {
		if (column == null) {
			return false;
		}
		
		if (mColumnsMap == null) {
			return false;
		}
		
		return mColumnsMap.containsValue(column);
	}
	
	
	public boolean containsColumn(String colName) {
		if (colName == null) {
			return false;
		}
		
		if (mColumnsMap == null) {
			return false;
		}
		
		final Column column = mColumnsMap.get(colName);
		
		return (column != null);
	}
	
	public boolean isEmpty() {
		return (mColumns == null || mColumns.size() <= 0);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Template == false) {
			return false;
		}
		
		Template templ = (Template)o;

		if (isEmpty() && templ.isEmpty()) {
			return true;
		}
		
		if (mColumns.size() != templ.mColumns.size()) {
			return false;
		}
		
		final int count = mColumns.size();
		for (int i = 0; i < count; i++) {
			if (mColumns.get(i).equals(templ.mColumns.get(i)) == false) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("columns(%s)", mColumns);
	}

}
