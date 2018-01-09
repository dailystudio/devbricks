package com.dailystudio.app.utils;

/**
 * Created by nanye on 18/1/5.
 */

public class TextUtils {

    public static String capitalize(String str) {
        if (android.text.TextUtils.isEmpty(str)
                || str.length() <= 1) {
            return str;
        }

        return str.substring(0,1).toUpperCase() + str.substring(1);
    }
}
