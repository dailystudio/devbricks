package com.dailystudio.dataobject.samples;

import android.content.Context;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DoubleColumn;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TextColumn;

public class QueryObject extends DatabaseObject {

	public QueryObject(Context context) {
		super(context);
		
		initMembers();
	}
	
	private void initMembers() {
		final Template templ = getTemplate();
		
		templ.addColumn(new IntegerColumn("_id", false, true));
		templ.addColumn(new IntegerColumn("intValue"));
		templ.addColumn(new DoubleColumn("doubleValue"));
		templ.addColumn(new TextColumn("textValue"));
	}

}

