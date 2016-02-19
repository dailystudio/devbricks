package com.dailystudio.dataobject.samples;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;

public class CopierTestObject1 extends DatabaseObject {
	
	public static final Column COLUMN_INT_VAL1 = new IntegerColumn("intVal1");
	public static final Column COLUMN_INT_VAL2 = new IntegerColumn("intVal2");
	public static final Column COLUMN_LONG_VAL1 = new LongColumn("longVal1");
	public static final Column COLUMN_LONG_VAL2 = new LongColumn("longVal2");
	public static final Column COLUMN_DOUBLE_VAL1 = new DoubleColumn("doubleVal1");
	public static final Column COLUMN_DOUBLE_VAL2 = new DoubleColumn("doubleVal2");
	public static final Column COLUMN_TEXT_VAL1 = new TextColumn("textVal1");
	public static final Column COLUMN_TEXT_VAL2 = new TextColumn("textVal2");
	public static final Column COLUMN_INT_DIFF = new IntegerColumn("intDiff", VERSION_START + 1);
	public static final Column COLUMN_LONG_DIFF = new LongColumn("longDiff", VERSION_START + 1);
	public static final Column COLUMN_DOUBLE_DIFF = new DoubleColumn("doubleDiif", VERSION_START + 1);
	public static final Column COLUMN_TEXT_DIFF = new TextColumn("textDiff", VERSION_START + 1);

	Column[] sColumnsVer1 = {
			COLUMN_INT_VAL1,
			COLUMN_INT_VAL2,
			COLUMN_LONG_VAL1,
			COLUMN_LONG_VAL2,
			COLUMN_DOUBLE_VAL1,
			COLUMN_DOUBLE_VAL2,
			COLUMN_TEXT_VAL1,
			COLUMN_TEXT_VAL2,
	};

	Column[] sColumnsVer2 = {
			COLUMN_INT_VAL1,
			COLUMN_INT_VAL2,
			COLUMN_LONG_VAL1,
			COLUMN_LONG_VAL2,
			COLUMN_DOUBLE_VAL1,
			COLUMN_DOUBLE_VAL2,
			COLUMN_TEXT_VAL1,
			COLUMN_TEXT_VAL2,
			COLUMN_INT_DIFF,
			COLUMN_LONG_DIFF,
			COLUMN_DOUBLE_DIFF,
			COLUMN_TEXT_DIFF,
	};

	public CopierTestObject1(Context context) {
		this(context, VERSION_START + 1);
	}
	
	public CopierTestObject1(Context context, int version) {
		super(context, version);
		
		initMembers();
	}
	
	private void initMembers() {
		final Template templ = getTemplate();
		
		if (mVersion == VERSION_START) {
			templ.addColumns(sColumnsVer1);
		} else if (mVersion == VERSION_START + 1) {
			templ.addColumns(sColumnsVer2);
		}
	}

}
