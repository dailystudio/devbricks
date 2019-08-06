package com.dailystudio.app.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.R;
import com.dailystudio.development.Logger;

/**
 * Created by Mikael on 02/02/16.
 */

public abstract class AbsAboutFragment extends AppCompatDialogFragment {

    public AbsAboutFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getContext();
        if (context == null) {
            return null;
        }

        final Resources res = context.getResources();
        if (res == null) {
            return null;
        }

        View dialogView = LayoutInflater.from(context).inflate(
                R.layout.fragment_about, null);

        if (dialogView == null) {
            return null;
        }

        ImageView appThumbView = dialogView.findViewById(R.id.about_app_thumb);
        if (appThumbView != null) {
            final int thumbResId = getAppThumbResource();
            if (thumbResId <= 0) {
                appThumbView.setVisibility(View.GONE);
            } else {
                appThumbView.setVisibility(View.VISIBLE);
                appThumbView.setImageResource(thumbResId);
            }
        }

        TextView appVerView = dialogView.findViewById(R.id.about_app_ver);
        if (appVerView != null) {
            String verName = getString(android.R.string.unknownName);

            PackageManager pkgMgr = context.getPackageManager();
            if (pkgMgr != null) {
                PackageInfo pkgInfo;

                try {
                    pkgInfo = pkgMgr.getPackageInfo(context.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    Logger.warnning("could not get package info of current app: %s",
                            e.toString());

                    pkgInfo = null;
                }

                if (pkgInfo != null) {
                    verName = pkgInfo.versionName;
                }

                appVerView.setText(verName);
            }
        }

        TextView appNameView = dialogView.findViewById(R.id.about_app_name);
        if (appNameView != null) {
            appNameView.setText(getAppName());
        }

        TextView appDescView = dialogView.findViewById(R.id.about_app_desc);
        if (appDescView != null) {
            if (hasHtmlDescription()) {
                appDescView.setAutoLinkMask(0);
                appDescView.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                appDescView.setAutoLinkMask(Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);
            }
            appDescView.setText(getAppDescription());
        }

        ImageView appIconView = (ImageView) dialogView.findViewById(R.id.about_app_icon);
        if (appIconView != null) {
            appIconView.setImageResource(getAppIconResource());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        }
                );

        return builder.create();
    }

    protected boolean hasHtmlDescription() {
        return false;
    }

    protected int getAppThumbResource() {
        return -1;
    }

    public abstract CharSequence getAppName();
    public abstract CharSequence getAppDescription();
    public abstract int getAppIconResource();

}
