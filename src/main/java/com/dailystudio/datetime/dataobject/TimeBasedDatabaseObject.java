package com.dailystudio.datetime.dataobject;

import android.content.Context;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.TimeColumn;
import com.dailystudio.datetime.CalendarUtils;

public class TimeBasedDatabaseObject extends DatabaseObject {
	
	public static final Column COLUMN_ID = new IntegerColumn("_id", false, true);
	public static final TimeColumn COLUMN_TIME = new TimeColumn("time", false);

	private final static Column[] sColumns = {
		COLUMN_ID,
		COLUMN_TIME,
	};

	public TimeBasedDatabaseObject(Context context) {
		this(context, VERSION_START);
	}
	
	public TimeBasedDatabaseObject(Context context, int version) {
		super(context, version);
		
		final Template templ = getTemplate();
		
		templ.addColumns(sColumns);
	}

	public void setId(int id) {
		setValue(COLUMN_ID, id);
	}
	
	public int getId() {
		return getIntegerValue(COLUMN_ID);
	}
	
	public void setTime(long time) {
		setValue(COLUMN_TIME, time);
	}
	
	public long getTime() {
		return getLongValue(COLUMN_TIME);
	}
	
	@Override
	public String toString() {
		return String.format("%s: [ID: %05d, TIME: %s]",
				getClass().getSimpleName(),
				getId(),
				CalendarUtils.timeToReadableString(getTime()));
	}

}
