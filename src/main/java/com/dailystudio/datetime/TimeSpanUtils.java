package com.dailystudio.datetime;

import java.util.HashSet;
import java.util.Set;

import android.util.FloatMath;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;

public class TimeSpanUtils {
	
	public static long calculateOverlapDuration(long start1, long end1, 
			long start2, long end2) {
		if (start1 >= end1 || start2 >= end2) {
			return 0;
		}
		
		final long dstart = Math.max(start1, start2);
		final long dend = Math.min(end1, end2);
/*		Logger.debug("dstart = %s[%d], dend = %s[%d]",
				CalendarUtils.timeToReadableString(dstart),
				dstart,
				CalendarUtils.timeToReadableString(dend),
				dend);
*/
		final long overlap = (dend - dstart);
		
		return (overlap >= 0 ? overlap : 0);
	}
	
	public static long calculateDays(long start, long end) {
		return calculateDays(start, end, null);
	}
	
	public static long calculateDays(long start, long end, int[] filterWeekdays) {
		if (filterWeekdays == null) {
			final long days = (long)FloatMath.ceil((end - start)
					/ (float)CalendarUtils.DAY_IN_MILLIS);
			Logger.debug("(end(%d, %s) - start(%d, %s)) / dayInMillis(%d) = days(%d)", 
					end, CalendarUtils.timeToReadableString(end),
					start, CalendarUtils.timeToReadableString(start),
					CalendarUtils.DAY_IN_MILLIS,
					days);

			return days;
		}
		
		long time = 0;
		
		start = CalendarUtils.getStartOfDay(start);
		end = CalendarUtils.getStartOfDay(end);
		
		Set<Integer> weekdays = new HashSet<Integer>();
		
		for (int fday: filterWeekdays) {
			weekdays.add((fday == 0 ? 7 : fday));
		}
		
		int weekday;
		long count = 0;
		for (time = start; time <= end; time += CalendarUtils.DAY_IN_MILLIS) {
			weekday = CalendarUtils.getWeekDay(time);
			Logger.debug("time = %s, weekday = %d", 
					CalendarUtils.timeToReadableString(time),
					weekday);
			
			if (weekdays.contains(weekday)) {
				count++;
			}
		}
		
		return count;
	}

	public static long[] calculateHourDistribution(long start, long end) {
		return calculateHourDistribution(null, start, end, null);
	}
	
	public static long[] calculateHourDistribution(long[] inputDistrib, long start, long end) {
		return calculateHourDistribution(inputDistrib, start, end, null);
	}
	
	public static long[] calculateHourDistribution(long start, long end, int[] filterWeekdays) {
		return calculateHourDistribution(null, start, end, filterWeekdays);
	}
	
	public static long[] calculateHourDistribution(long[] inputDistrib, 
			long start, long end, int[] filterWeekdays) {
/*		Logger.debug("[%s - %s]",
				CalendarUtils.timeToReadableString(start),
				CalendarUtils.timeToReadableString(end));
*/
		if (start >= end) {
			return inputDistrib;
		}
		
		if (inputDistrib != null && inputDistrib.length != 24) {
			Logger.error("incorrect dimension of inputDistrib[%d], expect: %d", 
					inputDistrib.length,
					24);
			return inputDistrib;
		}
		
		long startHour = start - start % CalendarUtils.HOUR_IN_MILLIS;
		long endHour = end - end % CalendarUtils.HOUR_IN_MILLIS;

/*		if (end - start > CalendarUtils.DAY_IN_MILLIS) {
			endHour += ((end - start) / CalendarUtils.DAY_IN_MILLIS) * CalendarUtils.DAY_IN_MILLIS;
		} else {
			if (CalendarUtils.getHour(endHour) < CalendarUtils.getHour(startHour)) {
				endHour += 24 * CalendarUtils.HOUR_IN_MILLIS;
			}
		}
*/
/*		Logger.debug("hour range: [%s - %s]",
				CalendarUtils.timeToReadableString(startHour),
				CalendarUtils.timeToReadableString(endHour));
*/		
		long[] hoursDistribution = inputDistrib;
		if (hoursDistribution == null) {
			hoursDistribution = new long[24];
		}
		
		Set<Integer> weekdays = null;
		if (filterWeekdays != null) {
			weekdays = new HashSet<Integer>();
			
			for (int fday: filterWeekdays) {
				weekdays.add((fday == 0 ? 7 : fday));
			}
		}
		
		int weekday;
		long time = 0;
		long distrib = 0;
		long dstart = 0;
		long dend = 0;
		int hourIndex = -1;
		for (time = startHour; time <= endHour; time += CalendarUtils.HOUR_IN_MILLIS) {
			weekday = CalendarUtils.getWeekDay(time);
			if (weekdays != null && !weekdays.contains(weekday)) {
				continue;
			}
			
			hourIndex = CalendarUtils.getHour(time);
			
			dstart = Math.max(time, start);
			dend = Math.min(time + CalendarUtils.HOUR_IN_MILLIS, end);
			
			distrib = dend - dstart;
				
/*			Logger.debug("[hour %d]: time = %s, distrib = %s - %s = %s",
					hourIndex,
					CalendarUtils.timeToReadableString(time),
					CalendarUtils.timeToReadableString(dend),
					CalendarUtils.timeToReadableString(dstart),
					CalendarUtils.durationToReadableString(distrib));
*/					
			hoursDistribution[hourIndex] += distrib;
		}
		
		return hoursDistribution;
	}

}
