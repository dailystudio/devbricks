package com.dailystudio.app.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.dailystudio.R;
import com.dailystudio.development.Logger;

public abstract class InplaceSettingsFragment extends BaseIntentFragment {

    private View mCloseBtn;
    private ImageView mTopImageView;
    private View mDarkBackgroundView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inplace_settings, null);

        setupViews(view);

        return view;
    }

    private void setupViews(View fragmentView) {
        if (fragmentView == null) {
            return;
        }

        mTopImageView = fragmentView.findViewById(R.id.settings_top);
        if (mTopImageView != null) {
            final Drawable drawable = getSettingsTopImageDrawable();

            if (drawable != null) {
                mTopImageView.setImageDrawable(drawable);
                mTopImageView.setVisibility(View.VISIBLE);
            } else {
                mTopImageView.setImageDrawable(null);
                mTopImageView.setVisibility(View.GONE);
            }
        }

        mCloseBtn = fragmentView.findViewById(R.id.settings_close);
        if (mCloseBtn != null) {
            Logger.debug("mCloseBtn: %s", mCloseBtn);

            mCloseBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    hide();
                }

            });
        }

        final String className = getSettingFragmentClassName();
        if (!TextUtils.isEmpty(className)) {
            Logger.debug("fragment class name: %s", className);
            Fragment fragment = Fragment.instantiate(getContext(),
                    className);

            if (fragment != null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(R.id.setting_fragment_holder, fragment);
                ft.commit();
            }
        }
    }

    public int getSettingsInAnimId() {
        return R.anim.inplace_settings_fragment_in;
    }

    public int getSettingsOutAnimId() {
        return R.anim.inplace_settings_fragment_out;
    }

    protected Drawable getSettingsTopImageDrawable() {
        return null;
    }

    abstract protected String getSettingFragmentClassName();

    public void show() {
        show(null);
    }

    public void showWithDarkBackground(View darkBackground) {
        show(darkBackground);
    }

    protected void show(View darkBackground) {
        if (isVisible()) {
            return;
        }

        if (darkBackground != null) {
            mDarkBackgroundView = darkBackground;
            darkBackground.setVisibility(View.VISIBLE);
        }

        FragmentTransaction ft =
                getFragmentManager().beginTransaction();

        final int enterAnim = getSettingsInAnimId();
        if (enterAnim > 0) {
            ft.setCustomAnimations(enterAnim, 0);
        }

        ft.show(this);

        ft.commitAllowingStateLoss();
    }

    public void hide() {
        hideFragment(this,
                getSettingsOutAnimId());

        if (mDarkBackgroundView != null) {
            mDarkBackgroundView.setVisibility(View.GONE);
            mDarkBackgroundView = null;
        }
    }

}