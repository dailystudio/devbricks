package com.dailystudio.dataobject;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.development.Logger;
import com.dailystudio.utils.ClassNameUtils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class DatabaseObject {

	public static final int VERSION_LATEST = Column.VERSION_LATEST;
	public static final int VERSION_START = Column.VERSION_1;
	
	private final static String TABLE_SQL_BASE = "CREATE TABLE IF NOT EXISTS ";

	private ContentValues mValues;
	
	protected Template mTemplate;
	
	protected Context mContext;
	
	protected final int mVersion;
	
	public DatabaseObject(Context context) {
		this(context, VERSION_START);
	}
	
	public DatabaseObject(Context context, int version) {
		mContext = context; 

		initMembers();

		mVersion = version;
	}
	
	private void initMembers() {
		mValues = new ContentValues();
		mTemplate = new Template();
	}
	
	protected void setTemplate(Template templ) {
		if (templ == null) {
			return;
		}
		
		mTemplate = templ;
	}
	
	public Template getTemplate() {
		return mTemplate;
	}
	
	public final ContentValues getValues() {
		return mValues;
	}
	
	public final List<Column> listNonEmptyColumns() {
		if (mTemplate == null) {
			return null;
		}
		
		if (mValues == null) {
			return null;
		}
		
		List<Column> columns = mTemplate.listColumns();
		if (columns == null || columns.size() <= 0) {
			return null;
		}
		
		List<Column> nonEmptyColumns = new ArrayList<Column>();
		
		int colVer = VERSION_LATEST;
		for (Column col: columns) {
			colVer = col.getVerion();
			if (colVer > mVersion) {
				continue;
			}
			
			if (mValues.containsKey(col.getName())) {
				nonEmptyColumns.add(col);
			}
		}
		
		return nonEmptyColumns;
	}
	
	public void setValue(String colName, Object value) {
		if (colName == null || value == null) {
			return;
		}
		
		if (mTemplate == null) {
			return;
		}
		
		setValue(mTemplate.getColumn(colName), value);
	}
	
	public void setValue(Column column, Object value) {
		if (column == null || value == null) {
			return;
		}
		
		if (mTemplate == null) {
			return;
		}
		
		if (mTemplate.containsColumn(column) == false) {
			Logger.warnning("no such column(name: %s)", column.getName());
			
			return;
		}
		
		if (column.matchColumnType(value) == false) {
			Logger.warnning("unmatched column(type: %s)", column.getType());
			
			return;
		}
		
		final int colVer = column.getVerion();
		if (colVer > mVersion) {
			Logger.warnning("column(name: %s, ver: %d) is NOT match object version %d", 
					column.getName(), colVer,
					mVersion);
			return;
		}

		if (mValues == null) {
			return;
		}
		
		column.setValue(mValues, value);
	}
	
	public Object getValue(String colName) {
		if (colName == null) {
			return null;
		}
		
		if (mTemplate == null) {
			return null;
		}
		
		return getValue(mTemplate.getColumn(colName));
	}
	
	public Object getValue(Column column) {
		if (column == null) {
			return null;
		}
		
		if (mTemplate == null) {
			return null;
		}
		
		if (mTemplate.containsColumn(column) == false) {
			Logger.warnning("no such column(name: %s)", column.getName());
			Logger.debug("columns: [%s]", mTemplate.listColumns());
			
			return null;
		}
		
		final int colVer = column.getVerion();
		if (colVer > mVersion) {
			Logger.warnning("column(name: %s, ver: %d) is NOT match object version %d", 
					column.getName(), colVer,
					mVersion);
			return null;
		}

		if (mValues == null) {
			return null;
		}
		
		return column.getValue(mValues);
	}
	
	public int getIntegerValue(String colName) {
		if (colName == null) {
			return 0;
		}
		
		if (mTemplate == null) {
			return 0;
		}
		
		return getIntegerValue(mTemplate.getColumn(colName));
	}
	
	public int getIntegerValue(Column column) {
		if (column == null) {
			return 0;
		}
		
		Object val = getValue(column);
		if (val == null || val instanceof Integer == false) {
			return 0;
		}
		
		Integer intVal = (Integer)val;
		
		return intVal.intValue();
	}

	public long getLongValue(String colName) {
		if (colName == null) {
			return 0l;
		}
		
		if (mTemplate == null) {
			return 0l;
		}
		
		return getLongValue(mTemplate.getColumn(colName));
	}
	
	public long getLongValue(Column column) {
		if (column == null) {
			return 0l;
		}
		
		Object val = getValue(column);
		if (val == null || val instanceof Long == false) {
			return 0l;
		}
		
		Long longVal = (Long)val;
		
		return longVal.longValue();
	}
	
	public double getDoubleValue(String colName) {
		if (colName == null) {
			return .0f;
		}
		
		if (mTemplate == null) {
			return .0f;
		}
		
		return getDoubleValue(mTemplate.getColumn(colName));
	}
	
	public double getDoubleValue(Column column) {
		if (column == null) {
			return .0;
		}
		
		Object val = getValue(column);
		if (val == null || val instanceof Double == false) {
			return .0;
		}
		
		Double doubleVal = (Double)val;
		
		return doubleVal.doubleValue();
	}
	
	public String getTextValue(String colName) {
		if (colName == null) {
			return null;
		}
		
		if (mTemplate == null) {
			return null;
		}
		
		return getTextValue(mTemplate.getColumn(colName));
	}
	
	public String getTextValue(Column column) {
		if (column == null) {
			return null;
		}
		
		Object val = getValue(column);
		if (val == null || val instanceof String == false) {
			return null;
		}
		
		String strVal = (String)val;
		
		return strVal;
	}
	
	public byte[] getBlobValue(String colName) {
		if (colName == null) {
			return null;
		}
		
		if (mTemplate == null) {
			return null;
		}
		
		return getBlobValue(mTemplate.getColumn(colName));
	}
	
	public byte[] getBlobValue(Column column) {
		if (column == null) {
			return null;
		}
		
		Object val = getValue(column);
//		Logger.debug("val = %s", val);
		if (val == null || val instanceof byte[] == false) {
			return null;
		}
		
		return (byte[])val;
	}
	
	public boolean isEmpty() {
		if (mValues == null || mValues.size() <= 0) {
			return true;
		}
		
		return false;
	}
	
	public Intent convertToIntent() {
		if (mValues == null) {
			return null;
		}
		
		if (mTemplate == null) {
			return null;
		}
		
		Intent i = new Intent();
		
		List<Column> columns = mTemplate.listColumns();
		if (columns == null) {
			return null;
		}
		
		for (Column column: columns) {
			if (column.getVerion() > mVersion) {
				continue;
			}
			
			column.attachValueTo(i, mValues);
		}
		
		return i;
	}
	
	public void fillValuesFromCursor(Cursor c) {
		if (c == null)  {
			return;
		}
		
		if (mValues == null) {
			return;
		}
		
		if (mTemplate == null) {
			return;
		}
		
		List<Column> columns = mTemplate.listColumns();
		if (columns == null) {
			return;
		}
		
		for (Column column: columns) {
			if (column.getVerion() > mVersion) {
				continue;
			}

			column.parseValueFrom(c, mValues);
		}
	}

	public String toSQLSelectionString() {
		if (isEmpty()) {
			return "";
		}
		
		if (mTemplate == null) {
			return "";
		}
		
		final List<Column> columns = mTemplate.listColumns();
		if (columns == null || columns.size() <= 0) {
			return "";
		}
		
		final List<String> parts = new ArrayList<String>();

		String valStr = null;
		Object value = null;
		for (Column column: columns) {
			value = getValue(column);
			if (value == null) {
				continue;
			}
			
			valStr = column.convertValueToString(value);
			if (valStr == null) {
				continue;
			}

			/*
			 * DATE:	2011/10/25
			 * AUTHOR:	Nan YE
			 * CONTENT:	remove \' around value, this will be handle
			 * 			by Column.convertValueToString(). '\ around
			 * 			numeric value will cause mismatching during
			 * 			execute "select" with some runtime defined 
			 * 			column (e.g. (longa - longb) > '0') 
			 */
			parts.add(String.format("%s = %s", 
					column.getName(), 
					valStr));
		}
		
		final int partsCount = parts.size();

		if (partsCount <= 0) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < partsCount; i++) {
			builder.append(parts.get(i));
			if (i != partsCount - 1) {
				builder.append(" AND ");
			}
		}
		
		return builder.toString();
	}

	public String handleNullProjection() {
		return null;
	}
	
	public int getVersion() {
		return mVersion;
	}
	
	public String[] toSQLProjection() {
		if (mTemplate == null) {
			return null;
		}
		
		final List<Column> columns = mTemplate.listColumns();
		if (columns == null) {
			return null;
		}
		
		final int size = columns.size();
		if (size <= 0) {
			return null;
		}
		
		String[] projection = new String[size];
		Column column = null;
		for (int i = 0; i < size; i++) {
			column = columns.get(i);
			if (column.getVerion() > mVersion) {
				continue;
			}			
			
			projection[i] = column.getName();
		}
		
		return projection;
	}

	public String toSQLTableCreationString() {
		final String table = classToTable(getClass());
		if (table == null) {
			return null;
		}
		
		final Template tmpl = getTemplate();
		if (tmpl == null) {
			return null;
		}
		
		StringBuilder sqlBuilder = new StringBuilder(TABLE_SQL_BASE);
		
		sqlBuilder.append(table).append(" ( ");
		
		final List<Column> columns = tmpl.listColumns();
		if (columns == null) {
			return null;
		}
		
		final List<Column> filterColumns = new ArrayList<Column>();
		
		for (Column col: columns) {
			if (col.getVerion() > mVersion) {
				continue;
			}
			
			filterColumns.add(col);
		}
		
		final int size = filterColumns.size();
		if (size <= 0) {
			return null;
		}
		
		Column column = null;
		for (int i = 0; i < size; i++) {
			column = filterColumns.get(i);
			
			sqlBuilder.append(column.toString());
			if (i != size - 1) {
				sqlBuilder.append(", ");
			} else {
				sqlBuilder.append(" );");
			}
		}
		
		return sqlBuilder.toString();
	}
	
	public static String classToTable(Class<? extends DatabaseObject> klass) {
		String className = ClassNameUtils.getClassName(klass);
		if (className == null) {
			return null;
		}
			
		return className.replace('.', '_').replace('$', '_');
	}

	public static String classToDatabase(Class<? extends DatabaseObject> klass) {
		String pkgName = ClassNameUtils.getPackageName(klass);
		if (pkgName == null) {
			return null;
		}
			
		String className = ClassNameUtils.getClassName(klass);
		if (className == null) {
			return null;
		}
			
		return String.format("%s.%s.db", 
				pkgName, 
				className.replace('.', '_').replace('$', '_'));
	}

}
