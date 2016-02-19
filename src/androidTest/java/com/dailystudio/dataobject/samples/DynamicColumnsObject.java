package com.dailystudio.dataobject.samples;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;

public class DynamicColumnsObject extends DatabaseObject {
	

	public static final Column COLUMN_INT_VER1 = new IntegerColumn("int_ver1", VERSION_START);
	public static final Column COLUMN_INT_VER1_UPDATED = new IntegerColumn("int_ver1", false, VERSION_START + 1);
	public static final Column COLUMN_INT_VER2 = new IntegerColumn("int_ver2", VERSION_START + 1);
	public static final Column COLUMN_LONG_VER1 = new LongColumn("long_ver1_only", VERSION_START);
	public static final Column COLUMN_DOUBLE_VER2 = new DoubleColumn("double_ver2", VERSION_START + 1);
	public static final Column COLUMN_TEXT_VER2 = new TextColumn("text_ver2", VERSION_START + 1);
	public static final Column COLUMN_TEXT_VER3 = new TextColumn("text_ver3", VERSION_START + 2);
	
	Column[] sColumnsVer1 = {
			COLUMN_INT_VER1,
			COLUMN_LONG_VER1,
	};

	Column[] sColumnsVer2 = {
			COLUMN_INT_VER1_UPDATED,
			COLUMN_INT_VER2,
			COLUMN_DOUBLE_VER2,
			COLUMN_TEXT_VER2,
	};

	Column[] sColumnsVer3 = {
			COLUMN_INT_VER1_UPDATED,
			COLUMN_INT_VER2,
			COLUMN_DOUBLE_VER2,
			COLUMN_TEXT_VER2,
			COLUMN_TEXT_VER3,
	};

	public DynamicColumnsObject(Context context) {
		this(context, VERSION_START + 2);
	}
	
	public DynamicColumnsObject(Context context, int version) {
		super(context, version);
		
		initMembers();
	}
	
	private void initMembers() {
		final Template templ = getTemplate();
		
		if (mVersion == VERSION_START) {
			templ.addColumns(sColumnsVer1);
		} else if (mVersion == VERSION_START + 1) {
			templ.addColumns(sColumnsVer2);
		} else {
			templ.addColumns(sColumnsVer3);
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s(ver: %d): %s", 
				getClass().getSimpleName(),
				getVersion(),
				toSQLSelectionString());
	}
	
}
