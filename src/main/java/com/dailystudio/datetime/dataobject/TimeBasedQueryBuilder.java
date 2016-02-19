package com.dailystudio.datetime.dataobject;

import com.dailystudio.app.dataobject.QueryBuilder;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.LongColumn;
import com.dailystudio.dataobject.TimeColumn;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;

import java.util.Calendar;

public class TimeBasedQueryBuilder extends QueryBuilder {
	
	public TimeBasedQueryBuilder(Class<? extends DatabaseObject> klass) {
		super(klass);
	}
	
	public Query getQueryForIntersect(TimeColumn colStart, LongColumn colDuration,
			long start, long end) {
		return getQueryForIntersect(colStart, colDuration, start, end, false);
	}
	
	public Query getQueryForIntersect(ExpressionToken startExp, ExpressionToken durationExp,
			long start, long end) {
		return getQueryForIntersect(startExp, durationExp, start, end, false);
	}

/*	public Query getQueryForIntersect(TimeColumn colStart, LongColumn colDuration,
			long start, long end, boolean orderByAscending) {
		if (colStart == null|| colDuration == null) {
			return null;
		}
		
		Logger.debug("columns: start(%s), duration(%s)", 
				colStart, colDuration);
		Logger.debug("peroid: start(%s), end(%s), orderByAscending(%s)", 
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end),
				orderByAscending);
		
		Query query = getQuery();
		if (query != null) {
			if (start < end) {
				ExpressionToken sel1 = colStart.lte(start)
						.and(colStart.plus(colDuration).gt(new ExpressionToken(start)));
				ExpressionToken sel2 = colStart.gt(start)
						.and(colStart.plus(colDuration).lte(new ExpressionToken(end)));
				
				ExpressionToken selection = sel1.or(sel2);
				if (selection != null) {
					query.setSelection(selection);
				}
			}
			
			OrderingToken orderBy = (orderByAscending ?
					TimeBasedDatabaseObject.COLUMN_TIME.orderByAscending() :
						TimeBasedDatabaseObject.COLUMN_TIME.orderByDescending());
			if (orderBy != null) {
				query.setOrderBy(orderBy);
			}
		}
		
		return query;
	}
*/
	
	public Query getQueryForIntersect(TimeColumn colStart, LongColumn colDuration,
			long start, long end, boolean orderByAscending) {
		if (colStart == null|| colDuration == null) {
			return null;
		}
		
		return getQueryForIntersect(
				new ExpressionToken(colStart),
				new ExpressionToken(colDuration),
				start, end, orderByAscending);
	}

	public Query getQueryForIntersect(ExpressionToken startExp, ExpressionToken durationExp,
			long start, long end, boolean orderByAscending) {
		if (startExp == null|| durationExp == null) {
			return null;
		}
		
		String startExpStr = startExp.toString();
		
		Logger.debug("expression: startExp(%s), durationExp(%s)",
				startExp,
				durationExp,
				orderByAscending);
		Logger.debug("peroid: start(%s), end(%s), orderByAscending(%s)",
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end),
				orderByAscending);
		
		Query query = getQuery();
		if (query != null) {
			if (start < end) {
				ExpressionToken sExp = new ExpressionToken(start);
				ExpressionToken eExp = new ExpressionToken(end);
				ExpressionToken sel1 = new ExpressionToken(startExpStr).lte(sExp)
						.and(new ExpressionToken(startExpStr).plus(durationExp).gt(sExp));
				ExpressionToken sel2 = new ExpressionToken(startExpStr).gt(sExp)
						.and(new ExpressionToken(startExpStr).plus(durationExp).lte(eExp));
				
				ExpressionToken selection = sel1.or(sel2);
				if (selection != null) {
					query.setSelection(selection);
				}
			}
			
			OrderingToken orderBy = (orderByAscending ?
					TimeBasedDatabaseObject.COLUMN_TIME.orderByAscending() :
						TimeBasedDatabaseObject.COLUMN_TIME.orderByDescending());
			if (orderBy != null) {
				query.setOrderBy(orderBy);
			}
		}
		
		return query;
	}

	public Query getQuery(long start, long end) {
		return getQuery(start, end, false);
	}
	
	public Query getQuery(long start, long end, boolean orderByAscending) {
		Logger.debug("peroid: start(%s), end(%s), orderByAscending(%s)",
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end),
				orderByAscending);
		
		Query query = getQuery();
		if (query != null) {
			if (start < end) {
				ExpressionToken selection = TimeBasedDatabaseObject.COLUMN_TIME.gte(start)
					.and(TimeBasedDatabaseObject.COLUMN_TIME.lte(end));
				if (selection != null) {
					query.setSelection(selection);
				}
			}
			
			OrderingToken orderBy = (orderByAscending ?
					TimeBasedDatabaseObject.COLUMN_TIME.orderByAscending() :
						TimeBasedDatabaseObject.COLUMN_TIME.orderByDescending());
			if (orderBy != null) {
				query.setOrderBy(orderBy);
			}
		}
		
		return query;
	}
	
	public Query getQueryForDay(long time) {
		return getQueryForDay(time, false);
	}

	public Query getQueryForDay(long time, boolean orderByAscending) {
		long start = CalendarUtils.getStartOfDay(time);
		long end = CalendarUtils.getEndOfDay(time);
		
		return getQuery(start, end, orderByAscending);
	}
	
	public Query getQueryForCurrentDay() {
		return getQueryForCurrentDay(false);
	}
	
	public Query getQueryForCurrentDay(boolean orderByAscending) {
		final long now = Calendar.getInstance().getTimeInMillis();
		
		return getQueryForDay(now, orderByAscending);
	}

	public Query getQueryForWeek(long time) {
		return getQueryForWeek(time, false);
	}

	public Query getQueryForWeek(long time, boolean orderByAscending) {
		long start = CalendarUtils.getStartOfWeek(time);
		long end = CalendarUtils.getEndOfWeek(time);
		if (end <= start) {
			start = CalendarUtils.getStartOfWeek(end);
		}

		return getQuery(start, end, orderByAscending);
	}
	
	public Query getQueryForCurrentWeek() {
		return getQueryForCurrentWeek(false);
	}
	
	public Query getQueryForCurrentWeek(boolean orderByAscending) {
		final long now = Calendar.getInstance().getTimeInMillis();
		
		return getQueryForWeek(now, orderByAscending);
	}

	public Query getQueryForMonth(long time) {
		return getQueryForMonth(time, false);
	}

	public Query getQueryForMonth(long time, boolean orderByAscending) {
		long start = CalendarUtils.getStartOfMonth(time);
		long end = CalendarUtils.getEndOfMonth(time);

		return getQuery(start, end, orderByAscending);
	}
	
	public Query getQueryForCurrentMonth() {
		return getQueryForCurrentMonth(false);
	}
	
	public Query getQueryForCurrentMonth(boolean orderByAscending) {
		final long now = Calendar.getInstance().getTimeInMillis();
		
		return getQueryForMonth(now, orderByAscending);
	}

	public Query getQueryForYear(long time) {
		return getQueryForYear(time, false);
	}

	public Query getQueryForYear(long time, boolean orderByAscending) {
		long start = CalendarUtils.getStartOfYear(time);
		long end = CalendarUtils.getEndOfYear(time);

		return getQuery(start, end, orderByAscending);
	}
	
	public Query getQueryForCurrentYear() {
		return getQueryForCurrentYear(false);
	}
	
	public Query getQueryForCurrentYear(boolean orderByAscending) {
		final long now = Calendar.getInstance().getTimeInMillis();
		
		return getQueryForYear(now, orderByAscending);
	}
	
}
