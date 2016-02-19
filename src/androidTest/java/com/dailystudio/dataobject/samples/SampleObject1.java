package com.dailystudio.dataobject.samples;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.Template;

public class SampleObject1 extends DatabaseObject {

	public static final Column COLUMN_ID = new IntegerColumn("_id", false, true);
	public static final Column COLUMN_TIME = new LongColumn("time", false);

	Column[] sColumns = {
		COLUMN_ID,
		COLUMN_TIME,
	};

	public SampleObject1(Context context) {
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
