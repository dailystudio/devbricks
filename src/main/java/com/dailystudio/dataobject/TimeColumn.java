package com.dailystudio.dataobject;

import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.utils.SQLiteDateTimeUtils;
import com.dailystudio.datetime.CalendarUtils;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public class TimeColumn extends LongColumn {
	
	public TimeColumn(String colName) {
		this(colName, true);
	}
	
	public TimeColumn(String colName, boolean allowNull) {
		this(colName, allowNull, false);
	}
	
	public TimeColumn(String colName, boolean allowNull, boolean isPrimary) {
		super(colName, allowNull, isPrimary, VERSION_1);
	}

	public TimeColumn(String colName, int version) {
		this(colName, true, version);
	}
	
	public TimeColumn(String colName, boolean allowNull, int version) {
		this(colName, allowNull, false, version);
	}
	
	public TimeColumn(String colName, boolean allowNull, boolean isPrimary, int version) {
		super(colName, allowNull, isPrimary, version);
	}

	@Override
	protected void setValue(ContentValues container, Object value) {
		if (container == null || value == null) {
			return;
		}
		
		container.put(getName(), (Long)value);
	}
	
	@Override
	Object getValue(ContentValues container) {
		if (container == null) {
			return null;
		}
		
		final String key = getName();
		
		if (container.containsKey(key) == false) {
			return null;
		}
		
		return container.getAsLong(key);
	}

	@Override
	protected boolean matchColumnType(Object value) {
		return (value instanceof Long);
	}

	@Override
	protected void attachValueTo(Intent intent, ContentValues container) {
		if (container == null || intent == null) {
			return;
		}
		
		Long lVal = container.getAsLong(getName());
		if (lVal == null) {
			return;
		}
		
		intent.putExtra(getName(), lVal);
	}

	@Override
	protected void parseValueFrom(Cursor cursor, ContentValues container) {
		if (cursor == null || container == null) {
			return;
		}
		
		try {
			final int columnIndex = cursor.getColumnIndexOrThrow(getName());
			
			if (cursor.isNull(columnIndex) == false) {
				long lVal = cursor.getLong(columnIndex);
				
				setValue(container, lVal);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String convertValueToString(Object value) {
		if (value instanceof Long == false) {
			return null;
		}
		
		Long lVal = (Long)value;
		
		return String.valueOf(lVal.longValue());
	}
	
	public ExpressionToken timeInDay() {
		return new ExpressionToken(String.format("( %s %% %d )",
				getName(), CalendarUtils.DAY_IN_MILLIS));
	}

	public OrderingToken groupByHour() {
		return new OrderingToken(SQLiteDateTimeUtils.hourOf(getName()));
	}
	
	public OrderingToken groupByMinute() {
		return new OrderingToken(SQLiteDateTimeUtils.minuteOf(getName()));
	}
	
	public OrderingToken groupBySecond() {
		return new OrderingToken(SQLiteDateTimeUtils.secondOf(getName()));
	}
	
	public OrderingToken groupByDay() {
		return new OrderingToken(SQLiteDateTimeUtils.dayOf(getName()));
	}
	
	public OrderingToken groupByWeekday() {
		return new OrderingToken(SQLiteDateTimeUtils.weekdayOf(getName()));
	}
	
	public OrderingToken groupByWeek() {
		return new OrderingToken(SQLiteDateTimeUtils.weekOf(getName()));
	}
	
	public OrderingToken groupByMonth() {
		return new OrderingToken(SQLiteDateTimeUtils.monthOf(getName()));
	}
	
	public OrderingToken groupByYear() {
		return new OrderingToken(SQLiteDateTimeUtils.yearOf(getName()));
	}
	
	public Column HOUR() {
		return new IntegerColumn(SQLiteDateTimeUtils.hourOf(getName()));
	}
	
	public Column MINUTE() {
		return new IntegerColumn(SQLiteDateTimeUtils.minuteOf(getName()));
	}
	
	public Column SECOND() {
		return new IntegerColumn(SQLiteDateTimeUtils.secondOf(getName()));
	}
	
	public Column DAY() {
		return new IntegerColumn(SQLiteDateTimeUtils.dayOf(getName()));
	}
	
	public Column WEEK() {
		return new IntegerColumn(SQLiteDateTimeUtils.weekOf(getName()));
	}
	
	public Column WEEKDAY() {
		return new IntegerColumn(SQLiteDateTimeUtils.weekdayOf(getName()));
	}
	
	public Column MONTH() {
		return new IntegerColumn(SQLiteDateTimeUtils.monthOf(getName()));
	}
	
	public Column YEAR() {
		return new IntegerColumn(SQLiteDateTimeUtils.yearOf(getName()));
	}
	
}
