package com.dailystudio.dataobject.samples;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;

public class VersionControlledObject extends DatabaseObject {
	
	public static final Column COLUMN_INT_VER1 = new IntegerColumn("int_ver1", VERSION_START);
	public static final Column COLUMN_INT_VER2 = new IntegerColumn("int_ver2", VERSION_START + 1);
	public static final Column COLUMN_LONG_VER1 = new LongColumn("long_ver1", VERSION_START);
	public static final Column COLUMN_DOUBLE_VER2 = new DoubleColumn("double_ver2", VERSION_START + 1);
	public static final Column COLUMN_TEXT_VER2 = new TextColumn("text_ver2", VERSION_START + 1);

	Column[] sColumns = {
			COLUMN_INT_VER1,
			COLUMN_INT_VER2,
			COLUMN_LONG_VER1,
			COLUMN_DOUBLE_VER2,
			COLUMN_TEXT_VER2,
	};
	
	public VersionControlledObject(Context context) {
		this(context, 2);
	}
	
	public VersionControlledObject(Context context, int version) {
		super(context, version);
		
		initMembers();
	}
	
	private void initMembers() {
		final Template templ = getTemplate();
		
		if (templ != null) {
			templ.addColumns(sColumns);
		}
	}
	
}
