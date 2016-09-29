package com.dailystudio.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by nanye on 16/9/29.
 */

public class BaseIntentDialogFragment extends DialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindIntent(getActivity().getIntent());
    }

    public void onNewIntent(Intent intent) {
        bindIntent(intent);
    }

    public void bindIntent(Intent intent) {
    }

}
