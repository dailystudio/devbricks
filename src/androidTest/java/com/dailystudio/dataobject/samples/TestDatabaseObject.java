package com.dailystudio.dataobject.samples;

import android.content.Context;

import com.dailystudio.dataobject.BlobColumn;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.TextColumn;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;

public class TestDatabaseObject extends DatabaseObject {
	
	public static final Column COLUMN_INTEGER_VAL = new IntegerColumn("intVal");
	public static final Column COLUMN_LONG_VAL = new LongColumn("longVal");
	public static final Column COLUMN_DOUBLE_VAL = new DoubleColumn("doubleVal");
	public static final Column COLUMN_TEXT_VAL = new TextColumn("textVal");
	public static final Column COLUMN_BLOB_VAL = new BlobColumn("blobVal");
	
	Column[] sColumns = {
			COLUMN_INTEGER_VAL,
			COLUMN_LONG_VAL,
			COLUMN_DOUBLE_VAL,
			COLUMN_TEXT_VAL,
			COLUMN_BLOB_VAL,
	};
	
	public TestDatabaseObject(Context context) {
		super(context);
		
		initMembers();
	}
	
	private void initMembers() {
		final Template templ = getTemplate();
		
		if (templ != null) {
			templ.addColumns(sColumns);
		}
	}

}
