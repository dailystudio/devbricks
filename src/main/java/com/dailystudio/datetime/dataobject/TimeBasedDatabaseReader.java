package com.dailystudio.datetime.dataobject;

import android.content.Context;

import com.dailystudio.app.dataobject.DatabaseReader;
import com.dailystudio.app.dataobject.QueryBuilder;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;

import java.util.Calendar;
import java.util.List;

public class TimeBasedDatabaseReader<T extends TimeBasedDatabaseObject> extends DatabaseReader<T> {

	public TimeBasedDatabaseReader(Context context,
								   Class<T> objectClass) {
		this(context, null, objectClass);
	}
	
	public TimeBasedDatabaseReader(Context context,
								   String authority,
								   Class<T> objectClass) {
		this(context, authority, objectClass, DatabaseObject.VERSION_LATEST);
	}
	
	public TimeBasedDatabaseReader(Context context,
								   Class<T> objectClass,
								   int version) {
		this(context, null, objectClass, version);
	}

	public TimeBasedDatabaseReader(Context context,
								   String authority, Class<T> klass, int version) {
		super(context, authority, klass, version);
	}
	
	@Override
	protected QueryBuilder onCreateQueryBuilder(Class<T> klass) {
		return new TimeBasedQueryBuilder(klass);
	}
	
	public T queryLastOne() {
		Query query = getQuery();
		if (query == null) {
			return null;
		}
		
		return queryLastOne(query);
	}
	
	@Override
	public T queryLastOne(Query query) {
		OrderingToken orderByToken = TimeBasedDatabaseObject.COLUMN_TIME.orderByDescending();
		if (orderByToken != null) {
			query.setOrderBy(orderByToken);
		}
		
		List<T> objects = queryTopN(query, 1);
		if (objects == null || objects.size() <= 0) {
			return null;
		}
		
		return objects.get(0);
	}

	public List<T> query(long start, long end) {
		return query(start, end, false);
	}
	
	public List<T> query(long start, long end, boolean orderByAscending) {
		if (mQueryBuilder instanceof TimeBasedQueryBuilder == false) {
			return null;
		}
		
		Query query = ((TimeBasedQueryBuilder)mQueryBuilder).getQuery(
				start, end, orderByAscending);
		if (query == null) {
			return null;
		}
		
		return query(query);
	}
	
	public List<T> queryForDay(long time) {
		return queryForDay(time, false);
	}

	public List<T> queryForDay(long time, boolean orderByAscending) {
		if (mQueryBuilder instanceof TimeBasedQueryBuilder == false) {
			return null;
		}
		
		Query query = ((TimeBasedQueryBuilder)mQueryBuilder).getQueryForDay(
				time, orderByAscending);
		if (query == null) {
			return null;
		}
		
		return query(query);
	}
	
	public List<T> queryForCurrentDay() {
		return queryForCurrentDay(false); 
	}

	public List<T> queryForCurrentDay(boolean orderByAscending) {
		final long now = Calendar.getInstance().getTimeInMillis();
		
		return queryForDay(now, orderByAscending); 
	}

	public List<T> queryForWeek(long time) {
		return queryForWeek(time, false);
	}

	public List<T> queryForWeek(long time, boolean orderByAscending) {
		if (mQueryBuilder instanceof TimeBasedQueryBuilder == false) {
			return null;
		}
		
		Query query = ((TimeBasedQueryBuilder)mQueryBuilder).getQueryForWeek(
				time, orderByAscending);
		if (query == null) {
			return null;
		}
		
		return query(query);
	}
	
	public List<T> queryForCurrentWeek() {
		return queryForCurrentWeek(false); 
	}

	public List<T> queryForCurrentWeek(boolean orderByAscending) {
		final long now = Calendar.getInstance().getTimeInMillis();
		
		return queryForWeek(now, orderByAscending); 
	}

	public List<T> queryForMonth(long time) {
		return queryForWeek(time, false);
	}

	public List<T> queryForMonth(long time, boolean orderByAscending) {
		if (mQueryBuilder instanceof TimeBasedQueryBuilder == false) {
			return null;
		}
		
		Query query = ((TimeBasedQueryBuilder)mQueryBuilder).getQueryForMonth(
				time, orderByAscending);
		if (query == null) {
			return null;
		}
		
		return query(query);
	}
	
	public List<T> queryForCurrentMonth() {
		return queryForCurrentWeek(false); 
	}

	public List<T> queryForCurrentMonth(boolean orderByAscending) {
		final long now = Calendar.getInstance().getTimeInMillis();
		
		return queryForMonth(now, orderByAscending); 
	}

	public List<T> queryForYear(long time) {
		return queryForYear(time, false);
	}

	public List<T> queryForYear(long time, boolean orderByAscending) {
		if (mQueryBuilder instanceof TimeBasedQueryBuilder == false) {
			return null;
		}
		
		Query query = ((TimeBasedQueryBuilder)mQueryBuilder).getQueryForYear(
				time, orderByAscending);
		if (query == null) {
			return null;
		}
		
		return query(query);
	}
	
	public List<T> queryForCurrentYear() {
		return queryForCurrentYear(false); 
	}

	public List<T> queryForCurrentYear(boolean orderByAscending) {
		final long now = Calendar.getInstance().getTimeInMillis();
		
		return queryForYear(now, orderByAscending); 
	}
	
}
