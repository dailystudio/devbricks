package com.dailystudio.app.dataobject;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;

public class CountObject extends DatabaseObject {
	
	public static final Column COLUMN_ID = new IntegerColumn("_id", false, true);
	public static final Column COLUMN_COUNT = new IntegerColumn("count(*)", false);

	private final static Column[] sColumns = {
		COLUMN_ID,
		COLUMN_COUNT,
	};
	
	public CountObject(Context context) {
		super(context);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sColumns);
	}
	
	public void setCount(int count) {
		setValue(COLUMN_COUNT, count);
	}
	
	public int getCount() {
		return getIntegerValue(COLUMN_COUNT);
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x): count(%d)",
				getClass().getSimpleName(),
				hashCode(),
				getCount());
	}

}
