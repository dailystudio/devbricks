package com.dailystudio.app.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.dailystudio.development.Logger;

/**
 * Created by nanye on 16/9/29.
 */

public class BaseIntentDialogFragment extends DialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        Logger.debug("dialog = %s", dialog);

        if (dialog != null) {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    final Dialog dialogInstance = getDialog();
                    setupViewsOnDialog(dialogInstance);
                }

            });
        }

        bindIntent(getActivity().getIntent());
    }

    public void onNewIntent(Intent intent) {
        bindIntent(intent);
    }

    protected void setupViewsOnDialog(Dialog dialog) {
    }

    public void bindIntent(Intent intent) {
    }

}
