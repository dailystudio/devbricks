package com.dailystudio.dataobject.samples;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;

public class ProjectionObject extends DatabaseObject {
	
	public static Column COLUMN_ID_COUNT = new IntegerColumn("count( _id )"); 

	public ProjectionObject(Context context) {
		super(context);
		
		initMembers();
	}
	
	private void initMembers() {
		final Template templ = getTemplate();
		
		templ.addColumn(COLUMN_ID_COUNT);
	}
	
}
