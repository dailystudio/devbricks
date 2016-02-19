package com.dailystudio.app.async;

import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;

import android.content.Context;

public abstract class PeroidicalAsyncChecker extends AsyncChecker {

    public PeroidicalAsyncChecker(Context context) {
        super(context);
    }
    
    public void runIfOnTime() {
        final long now = System.currentTimeMillis();
        final long interval = getCheckInterval();

        final long lastTimestamp = getLastCheckTimestamp(mContext);
        Logger.debug("lastTimestamp = %d(%s), current = %d(%s), checkInterval = %d(%s)", 
                lastTimestamp,
                CalendarUtils.timeToReadableString(lastTimestamp),
                now,
                CalendarUtils.timeToReadableString(now),
                interval,
                CalendarUtils.durationToReadableString(interval));
        
        final long elapsed = now - lastTimestamp;
    	/*
    	 * XXX: elapsed < 0 means there is some time problem
    	 * 		during last check, because the laststamp even large
    	 * 		than current time, we should run this at once to
    	 * 		correct this issue.
    	 */
        if (lastTimestamp == -1
                || (elapsed >= interval)
                || (elapsed < 0)) {
            run();
        } else {
            Logger.warnning("time elapsed(%s) less than interval, skip",
                    CalendarUtils.durationToReadableString(elapsed),
                    CalendarUtils.durationToReadableString(interval));
        }
    }

    abstract public long getCheckInterval();
    
}
