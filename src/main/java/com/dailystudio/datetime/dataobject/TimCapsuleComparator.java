package com.dailystudio.datetime.dataobject;

import java.util.Comparator;

/**
 * Created by nanye on 16/6/15.
 */
public class TimCapsuleComparator implements Comparator<TimeCapsule> {

    @Override
    public int compare(TimeCapsule lhs, TimeCapsule rhs) {
        final long time1 = lhs.getTime();
        final long time2 = rhs.getTime();

        final int ret = (int)(time1 - time2);

        return ret;
    }

}
