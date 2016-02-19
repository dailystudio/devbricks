package com.dailystudio.dataobject.utils;

public class SQLiteDateTimeUtils {
	
	private final static String TIME_FUNC = "CAST ( strftime('%s', (%s / 1000), 'unixepoch', 'localtime') AS INTEGER )";

	private final static String HOUR = "%H";
	private final static String MINUTE = "%M";
	private final static String SECOND = "%S";
	private final static String DAY = "%d";
	private final static String WEEK_DAY = "%w";
	private final static String WEEK = "%W";
	private final static String MONTH = "%m";
	private final static String YEAR = "%Y";
	
	static String toDateTimeString(String formatString, String timeString) {
		return String.format(TIME_FUNC,
				formatString,
				timeString);
	}
	
	public static String hourOf(String timeString) {
		return toDateTimeString(HOUR, timeString);
	}
	
	public static String hourOf(long time) {
		return toDateTimeString(HOUR, String.valueOf(time));
	}
	
	public static String minuteOf(String timeString) {
		return toDateTimeString(MINUTE, timeString);
	}
	
	public static String minuteOf(long time) {
		return toDateTimeString(MINUTE, String.valueOf(time));
	}
	
	public static String secondOf(String timeString) {
		return toDateTimeString(SECOND, timeString);
	}
	
	public static String secondOf(long time) {
		return toDateTimeString(SECOND, String.valueOf(time));
	}
	
	public static String dayOf(String timeString) {
		return toDateTimeString(DAY, timeString);
	}
	
	public static String dayOf(long time) {
		return toDateTimeString(DAY, String.valueOf(time));
	}
	
	public static String weekdayOf(String timeString) {
		return toDateTimeString(WEEK_DAY, timeString);
	}
	
	public static String weekdayOf(long time) {
		return toDateTimeString(WEEK_DAY, String.valueOf(time));
	}
	
	public static String weekOf(String timeString) {
		return toDateTimeString(WEEK, timeString);
	}
	
	public static String weekOf(long time) {
		return toDateTimeString(WEEK, String.valueOf(time));
	}
	
	public static String monthOf(String timeString) {
		return toDateTimeString(MONTH, timeString);
	}
	
	public static String monthOf(long time) {
		return toDateTimeString(MONTH, String.valueOf(time));
	}
	
	public static String yearOf(String timeString) {
		return toDateTimeString(YEAR, timeString);
	}
	
	public static String yearOf(long time) {
		return toDateTimeString(YEAR, String.valueOf(time));
	}
	
}
