package com.dailystudio.app.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.dailystudio.app.utils.ArrayUtils;
import com.dailystudio.development.Logger;

public abstract class AbsPermissionsFragment extends BaseIntentFragment {

    private final static int REQUEST_PERMISSIONS = 10;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] requiredPermissions = getRequiredPermissions();
        Logger.debug("requiredPermissions: %s",
                ArrayUtils.stringArrayToString(requiredPermissions,","));

        if (!hasPermissions(requireContext(), requiredPermissions)) {
            Logger.debug("request required permissions");
            requestPermissions(requiredPermissions, REQUEST_PERMISSIONS);
        } else {
            onPermissionsGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Take the user to the success fragment when permission is granted
                Logger.warn("Permission request granted");
                onPermissionsGranted();
            } else {
                Logger.warn("Permission request denied");
            }
        }
    }

    public abstract String[] getRequiredPermissions();
    public abstract void onPermissionsGranted();

    public static boolean hasPermissions(Context context,
                                         String[] permissions) {
        if (permissions == null || permissions.length <= 0) {
            return true;
        }

        int granted;
        for (String perm: permissions) {
            granted = ContextCompat.checkSelfPermission(context,
                    perm);

            if (granted != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

}
