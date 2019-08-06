package com.dailystudio.test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.dailystudio.app.activity.ActionBarFragmentActivity;
import com.dailystudio.app.prefs.AbsPrefs;
import com.dailystudio.app.utils.ArrayUtils;
import com.dailystudio.development.Logger;

/**
 * Created by nanye on 16/8/4.
 */
public class PermCheckActivity extends ActionBarFragmentActivity {

    public static final String ACTION_PERM_CHECK_RESULT =
            "dailystudio.intent.ACTION_PERM_CHECK_RESULT";

    public static final String EXTRA_PERM_CHECK_RESULT =
            "dailystudio.intent.EXTRA_PERM_CHECK_RESULT";

    private final static int PERM_QUEST_EXTERNAL_STORAGE = 1;

    private final static String PREF_NAME = "perm-check";
    private final static String KEY_PERM_CHECKED = "checked";

    private static class PermCheckPrefs extends AbsPrefs {

        @Override
        protected String getPrefName() {
            return PREF_NAME;
        }

        private void setPermChecked(Context context, boolean checked) {
            setBooleanPrefValue(context, KEY_PERM_CHECKED, checked);
        }

        private boolean isPermChecked(Context context) {
            return getBooleanPrefValue(context, KEY_PERM_CHECKED);
        }

    }

    private static PermCheckPrefs sPref = new PermCheckPrefs();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkRequiredPermission();
    }

    private void checkRequiredPermission() {
        final boolean readExtGranted = isPermissionGranted(
                Manifest.permission.READ_EXTERNAL_STORAGE);
        final boolean writeExtGranted = isPermissionGranted(
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Logger.debug("readExt = %s, writeExt = %s",
                readExtGranted,
                writeExtGranted);

        if (readExtGranted == false || writeExtGranted == false) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    },
                    PERM_QUEST_EXTERNAL_STORAGE);
        } else {
            notifyResult(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Logger.debug("requestCode(%d), permissions[%s], grantResults[%s]",
                requestCode,
                ArrayUtils.arrayToString(permissions),
                ArrayUtils.intArrayToString(grantResults));

        final boolean readExtGranted = isPermissionGranted(
                Manifest.permission.READ_EXTERNAL_STORAGE);
        final boolean writeExtGranted = isPermissionGranted(
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        notifyResult((readExtGranted && writeExtGranted));
    }

    private void notifyResult(boolean allowed) {
        Intent result = new Intent(ACTION_PERM_CHECK_RESULT);

        result.putExtra(EXTRA_PERM_CHECK_RESULT, allowed);

        sendBroadcast(result);

        sPref.setPermChecked(this, true);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    public static boolean isPermChecked(Context context) {
        return sPref.isPermChecked(context);
    }

    public static void setkPermChecked(Context context, boolean checked) {
        sPref.setPermChecked(context, checked);
    }

    private Handler mHandler = new Handler();
}
