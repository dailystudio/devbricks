package com.dailystudio.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.dailystudio.R;
import com.dailystudio.development.Logger;

import java.util.List;

/**
 * Created by nanye on 17/5/8.
 */

public abstract class SettingsFragment extends BaseIntentFragment {

    public static interface LayoutHolder {

        public View createView(Context context, Setting setting);

    }

    public final static String ACTION_SETTINGS_CHANGED =
            "devbricks.intent.ACTION_SETTINGS_CHANGED";
    public final static String EXTRA_SETTING_NAME =
            "devbricks.intent.EXTRA_SETTING_NAME";

    public static class Setting {

        private Context mContext;

        private String mName;

        private Drawable mIcon;
        private CharSequence mLabel;
        private LayoutHolder mHolder;

        public Setting(Context context, String name, int iconResId, int labelResId, LayoutHolder holder) {
            mContext = context.getApplicationContext();
            mHolder = holder;
            mName = name;

            setIcon(iconResId);
            setLabel(labelResId);
        }

        public String getName() {
            return mName;
        }

        public void setIcon(Drawable d) {
            mIcon = d;
        }

        public void setIcon(int iconResId) {
            if (mContext == null) {
                return;
            }

            final Resources res = mContext.getResources();
            if (res == null) {
                return;
            }

            setIcon(res.getDrawable(iconResId));
        }

        public void setLabel(CharSequence label) {
            mLabel = label;
        }

        public void setLabel(int labelResId) {
            if (mContext == null) {
                return;
            }

            final Resources res = mContext.getResources();
            if (res == null) {
                return;
            }

            setLabel(res.getString(labelResId));
        }

        public Drawable getIcon() {
            return mIcon;
        }

        public CharSequence getLabel() {
            return mLabel;
        }

        public Context getContext() {
            return mContext;
        }

        public LayoutHolder getLayoutHolder() {
            return mHolder;
        }

        @Override
        public String toString() {
            return String.format("%s(0x%08x): label = %s, icon = %s, holder = %s",
                    getClass().getSimpleName(),
                    hashCode(),
                    getLabel(),
                    getIcon(),
                    getLayoutHolder());
        }

        void notifySettingsChanged() {
            Intent i = new Intent(ACTION_SETTINGS_CHANGED);

            i.putExtra(EXTRA_SETTING_NAME, getName());

            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(i);
        }

    }

    public static abstract class SwitchSetting extends Setting {

        public SwitchSetting(Context context,
                             String name,
                             int iconResId,
                             int labelResId,
                             SwitchSettingsLayoutHolder holder) {
            super(context, name, iconResId, labelResId, holder);
        }

        public abstract boolean isSwitchOn(Context context);
        public abstract void setSwitchOn(Context context, boolean on);

    }

    public class SwitchSettingsLayoutHolder extends BaseSettingLayoutHolder {

        @Override
        public View createView(Context context, Setting setting) {
            View view = LayoutInflater.from(context).inflate(
                    R.layout.layout_setting_switch, null);

            bingSetting(view, setting);

            return view;
        }

        @Override
        protected void bingSetting(View settingView, Setting setting) {
            super.bingSetting(settingView, setting);

            if (settingView == null
                    || setting instanceof SwitchSetting == false) {
                return;
            }

            final Context context = getContext();
            if (context == null) {
                return;
            }

            final SwitchSetting switchSetting = (SwitchSetting) setting;

            Switch swView = (Switch) settingView.findViewById(
                    R.id.setting_switch);
            if (swView != null) {
                final boolean switchOn =
                        switchSetting.isSwitchOn(context);

                swView.setChecked(switchOn);
                swView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        switchSetting.setSwitchOn(context, isChecked);
                        switchSetting.notifySettingsChanged();
                    }
                });
            }
        }
    }

    public abstract class BaseSettingLayoutHolder implements LayoutHolder {

        protected void bingSetting(View settingView, Setting setting) {
            if (settingView == null
                    || setting == null) {
                return;
            }

            ImageView iconView = (ImageView) settingView.findViewById(
                    R.id.setting_icon);
            if (iconView != null) {
                iconView.setImageDrawable(setting.getIcon());
            }

            TextView labelView = (TextView) settingView.findViewById(
                    R.id.setting_label);
            if (labelView != null) {
                labelView.setText(setting.getLabel());
            }
        }

    }

    private ViewGroup mSettingsContainer;

    private List<Setting> mSettings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);

        setupViews(view);

        return view;
    }

    private void setupViews(View fragmentView) {
        if (fragmentView == null) {
            return;
        }

        mSettingsContainer = (ViewGroup) fragmentView.findViewById(R.id.settings_container);
        Logger.debug("mSettingsContainer = %s", mSettingsContainer);

        Setting[] settings = createSettings(getContext());
        if (settings != null) {
            for (Setting s: settings) {
                addSetting(s);
            }
        }
    }

    protected abstract Setting[] createSettings(Context context);

    public void addSetting(Setting setting) {
        Logger.debug("add setting: %s", setting);
        if (setting == null) {
            return;
        }
        Logger.debug("mSettingsContainer = %s", mSettingsContainer);

        if (mSettingsContainer == null) {
            return;
        }

        LayoutHolder lh = setting.getLayoutHolder();
        if (lh == null) {
            return;
        }

        View view = lh.createView(getContext(), setting);
        if (view == null) {
            return;
        }

        LinearLayout.LayoutParams llp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        Logger.debug("add settings view: %s", view);
        mSettingsContainer.addView(view, llp);

//        mSettings.add(setting);
    }

}
