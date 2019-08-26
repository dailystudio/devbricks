package com.dailystudio.app.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.dailystudio.R;
import com.dailystudio.development.Logger;

public abstract class AbsPermissionsFragment extends BaseIntentFragment {

    private final static int REQUEST_PERMISSIONS = 10;

    private View mRootView;
    private View mPromptView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!hasPermissions(requireContext(), getRequiredPermissions())) {
            requestPermissions();
        } else {
            onPermissionsGranted(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_permissions, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRootView = view.findViewById(R.id.fragment_view_root);
        if (mRootView != null) {
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPermissions();
                }
            });
        }

        mPromptView = view.findViewById(android.R.id.empty);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Take the user to the success fragment when permission is granted
                Logger.warn("Permission request granted");
                onPermissionsGranted(true);
            } else {
                if (mPromptView != null) {
                    mPromptView.setVisibility(View.VISIBLE);
                }

                Logger.warn("Permission request denied");
                onPermissionsDenied();
            }
        }
    }

    private void requestPermissions() {
        if (mPromptView != null) {
            mPromptView.setVisibility(View.GONE);
        }

        Logger.debug("request required permissions");
        requestPermissions(getRequiredPermissions(), REQUEST_PERMISSIONS);
    }

    public abstract String[] getRequiredPermissions();
    public abstract void onPermissionsGranted(boolean newlyGranted);
    public abstract void onPermissionsDenied();

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
