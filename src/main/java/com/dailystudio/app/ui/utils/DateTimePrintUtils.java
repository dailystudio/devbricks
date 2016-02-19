package com.dailystudio.app.ui.utils;

import android.content.Context;
import android.content.res.Resources;

import com.dailystudio.R;
import com.dailystudio.datetime.CalendarUtils;

import java.text.SimpleDateFormat;

public class DateTimePrintUtils {
	
	private final static String DATE_PRINT_FORMAT = "MMMM dd, yyyy ";
	private final static String TIME_PRINT_FORMAT = "HH:mm:ss";
	
	public static String printDurationString(Context context, long duration) {
		return printDurationString(context, duration, false);
	}
	
	public static long[] durationPrintValues(long duration) {
		long millis = duration / CalendarUtils.SECOND_IN_MILLIS;
		long sec = duration / CalendarUtils.SECOND_IN_MILLIS;
		long min = duration / CalendarUtils.MINUTE_IN_MILLIS;
		long hour = duration / CalendarUtils.HOUR_IN_MILLIS;
		long day = duration / CalendarUtils.DAY_IN_MILLIS;
		
		return new long[] { day, hour % 24, min % 60, sec % 60, millis };
	}

	public static String printDurationString(Context context, 
			long duration, boolean printMillis) {
		if (context == null) {
			return null;
		}
		
		String dayLabel = null;
		String daysLabel = null;
		String hourLabel = null;
		String minLabel = null;
		String secLabel = null;
		
		Resources res = context.getResources();
		if (res != null) {
			dayLabel = res.getString(R.string.time_print_label_day);
			daysLabel = res.getString(R.string.time_print_label_days);
			hourLabel = res.getString(R.string.time_print_label_hour);
			minLabel = res.getString(R.string.time_print_label_min);
			secLabel = res.getString(R.string.time_print_label_sec);
		} else {
			dayLabel = "day";
			daysLabel = "days";
			hourLabel = "h";
			minLabel = "\"";
			secLabel = "\'";
		}
		
		final long values[] = durationPrintValues(duration);
		if (values == null || values.length < 5) {
			return null;
		}
		
		long day = values[0];
		long hour = values[1];
		long min = values[2];
		long sec = values[3];
		long millis = values[4];
		
		StringBuilder builder = new StringBuilder();
		if (day > 0) {
			builder.insert(0, String.format("%d%s %02d%s %02d%s %02d%s",
					day, (day > 1 ? daysLabel : dayLabel),
					hour, hourLabel,
					min, minLabel,
					sec, secLabel));
		} else if (hour > 0) {
			builder.insert(0, String.format("%02d%s %02d%s %02d%s",
					hour, hourLabel,
					min, minLabel,
					sec, secLabel));
		} else if (min > 0) {
			builder.insert(0, String.format("%02d%s %02d%s",
					min, minLabel,
					sec, secLabel));
		} else if (sec > 0) {
			builder.insert(0, String.format("%02d%s",
					sec, secLabel));
		}
		
		if (printMillis) {
			builder.append(String.format(" %03d", millis));
		} else {
			if (sec <= 0 && builder.length() <= 0) {
				builder.append(String.format("<1%s", secLabel));
			}
		}
		
		return builder.toString();
	}
	
	public static String printTimeString(Context context, long time, 
			boolean printDate, boolean printTime) {
		if (context == null) {
			return null;
		}
		
		StringBuilder builder = new StringBuilder();
		
		Resources res = context.getResources();
		if (printDate) {
			String dateFormatTempl = null;
		
			if (res != null) {
				dateFormatTempl = res.getString(R.string.time_print_date_format);
				if (dateFormatTempl != null) {
					dateFormatTempl = dateFormatTempl + " ";
				}
			}

			if (dateFormatTempl == null) {
				dateFormatTempl = DATE_PRINT_FORMAT;
			}
			
			builder.append(dateFormatTempl);	
		}
		
		if (printTime) {
			String timeFormatTempl = null;
			
			if (res != null) {
				timeFormatTempl = res.getString(R.string.time_print_time_format);
			}

			if (timeFormatTempl == null) {
				timeFormatTempl = TIME_PRINT_FORMAT;
			}
			
			builder.append(timeFormatTempl);
		}

		String format = builder.toString();
		if (format == null) {
			return null;
		}
		
		format = format.trim();
		
		SimpleDateFormat formater = new SimpleDateFormat(format);
		
		return formater.format(time);
	}

	public static String printTimeString(Context context, long time) {
		return printTimeString(context, time, true, true);
	}

	public static String printTimeStringWithoutTime(Context context, long time) {
		return printTimeString(context, time, true, false);
	}

	public static String printTimeStringWithoutDate(Context context, long time) {
		return printTimeString(context, time, false, true);
	}

}
