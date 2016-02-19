package com.dailystudio.dataobject.samples;

import android.content.Context;

import com.dailystudio.dataobject.BlobColumn;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;

public class SampleObject2 extends DatabaseObject {

	public static final Column COLUMN_ID = new IntegerColumn("_id", false, true);
	public static final Column COLUMN_LON = new DoubleColumn("longitude");
	public static final Column COLUMN_LAT = new DoubleColumn("latitude");
	public static final Column COLUMN_ALT = new DoubleColumn("altitude");
	public static final Column COLUMN_BIN = new BlobColumn("binary");
	
	Column[] sColumns = {
			COLUMN_ID,
			COLUMN_LON,
			COLUMN_LAT,
			COLUMN_ALT,
			COLUMN_BIN,
	};

	public SampleObject2(Context context) {
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
