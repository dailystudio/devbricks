package com.dailystudio.app.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.dailystudio.R;
import com.dailystudio.development.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nanye on 17/5/8.
 */

public abstract class SettingsFragment extends BaseIntentFragment {

    public interface LayoutHolder {

        View getView();
        View createView(Context context, Setting setting);
        void invalidate(Context context, Setting setting);

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

        public void notifyDataChanges() {
            mHandler.removeCallbacks(mNotifyDataChangesRunnable);
            mHandler.postDelayed(mNotifyDataChangesRunnable, 300);
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

        protected void notifySettingsChanged() {
            Intent i = new Intent(ACTION_SETTINGS_CHANGED);

            i.putExtra(EXTRA_SETTING_NAME, getName());

            LocalBroadcastManager.getInstance(getContext()).sendBroadcast(i);
        }

        private Runnable mNotifyDataChangesRunnable = new Runnable() {
            @Override
            public void run() {
                if (mHolder != null) {
                    mHolder.invalidate(getContext(), Setting.this);
                }
            }
        };

        private Handler mHandler = new Handler();

    }

    public static class TextSetting extends Setting {

        private CharSequence mDesc;

        public TextSetting(Context context,
                             String name,
                             int iconResId,
                             int labelResId,
                             TextSettingsLayoutHolder holder) {
            this(context, name, iconResId, labelResId, -1, holder);
        }

        public TextSetting(Context context,
                             String name,
                             int iconResId,
                             int labelResId,
                             int descResId,
                             TextSettingsLayoutHolder holder) {
            super(context, name, iconResId, labelResId, holder);

            setDesc(descResId);
        }


        public void setDesc(CharSequence desc) {
            mDesc = desc;
        }

        public void setDesc(int descResId) {
            final Context context = getContext();
            if (context == null) {
                return;
            }

            final Resources res = context.getResources();
            if (res == null) {
                return;
            }

            if (descResId <= 0) {
                setDesc(null);

                return;
            }

            setDesc(res.getString(descResId));
        }

        public CharSequence getDesc() {
            return mDesc;
        }
    }

    public abstract static class EditSetting extends Setting {

        public EditSetting(Context context,
                           String name,
                           int iconResId,
                           int labelResId,
                           EditSettingLayoutHolder holder) {
            super(context, name, iconResId, labelResId, holder);
        }

        public CharSequence getEditHint(Context context) {
            return null;
        }

        public Drawable getEditButtonDrawable(Context context) {
            return null;
        }

        public abstract CharSequence getEditText(Context context);

        public abstract void setEditText(Context context, CharSequence text);
        public abstract void onEditButtonClicked(Context context);

    }

    public class EditSettingLayoutHolder extends BaseSettingLayoutHolder {

        private EditText mEditText;

        @Override
        public View onCreateView(Context context,
                                 LayoutInflater layoutInflater,
                                 Setting setting) {
            View view = layoutInflater.inflate(
                    R.layout.layout_setting_edit, null);

            bingSetting(view, setting);

            return view;
        }

        @Override
        public void invalidate(Context context, Setting setting) {
            if (context == null) {
                return;
            }

            final EditSetting editSetting = (EditSetting) setting;

            if (mEditText != null) {
                mEditText.setText(editSetting.getEditText(context));
            }
        }

        @Override
        protected void bingSetting(View settingView, Setting setting) {
            super.bingSetting(settingView, setting);

            if (settingView == null
                    || setting instanceof EditSetting == false) {
                return;
            }

            final Context context = getContext();
            if (context == null) {
                return;
            }

            final EditSetting editSetting = (EditSetting) setting;

            final ImageView editButton = (ImageView) settingView.findViewById(
                    R.id.setting_edit_image_button);
            if (editButton != null) {
                final Drawable drawable =
                        ((EditSetting) setting).getEditButtonDrawable(context);
                if (drawable == null) {
                    editButton.setVisibility(View.GONE);
                    editButton.setOnClickListener(null);
                } else {
                    editButton.setVisibility(View.VISIBLE);
                    editButton.setImageDrawable(drawable);
                    editButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editSetting.onEditButtonClicked(context);
                        }
                    });
                }
            }

            mEditText = (EditText) settingView.findViewById(
                    R.id.setting_edit);
            if (mEditText != null) {
                mEditText.setText(editSetting.getEditText(context));
                mEditText.setHint(editSetting.getEditHint(context));
                mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus == false) {
                            Editable editable = mEditText.getText();
                            if (editable != null) {
                                performEditTextChange(editSetting,
                                        editable);
                            }
                        }
                    }
                });
            }
        }

        protected void performEditTextChange(EditSetting editSetting, CharSequence s) {
            if (editSetting == null) {
                return;
            }

            mHandler.removeMessages(MSG_TEXT_CHANGED);

            TextChangeData tcd = new TextChangeData();
            tcd.editSetting = editSetting;
            tcd.text = s;

            Message msg = Message.obtain(mHandler, MSG_TEXT_CHANGED, tcd);

            mHandler.sendMessageDelayed(msg, DELAY);
        }

        private final static int MSG_TEXT_CHANGED = 0x1;
        private final static long DELAY = 500;

        private class TextChangeData {
            private EditSetting editSetting;
            private CharSequence text;
        }

        private Handler mHandler = new Handler() {

            @Override
            public void dispatchMessage(Message msg) {
                if (msg.what == MSG_TEXT_CHANGED
                        && msg.obj instanceof TextChangeData) {
                    TextChangeData tcd = (TextChangeData)msg.obj;

                    if (tcd.editSetting != null) {
                        tcd.editSetting.setEditText(getContext(), tcd.text);
                        tcd.editSetting.notifySettingsChanged();
                    }

                    return;
                }

                super.dispatchMessage(msg);
            }

        };

    }

    public static abstract class SwitchSetting extends TextSetting {

        public SwitchSetting(Context context,
                             String name,
                             int iconResId,
                             int labelResId,
                             SwitchSettingsLayoutHolder holder) {
            super(context, name, iconResId, labelResId, holder);
        }


        public SwitchSetting(Context context,
                             String name,
                             int iconResId,
                             int labelResId,
                             int descResId,
                             SwitchSettingsLayoutHolder holder) {
            super(context, name, iconResId, labelResId, descResId, holder);
        }

        public abstract boolean isSwitchOn(Context context);
        public abstract void setSwitchOn(Context context, boolean on);

    }

    public static abstract class SeekBarSetting extends Setting {

        public SeekBarSetting(Context context,
                              String name,
                              int iconResId,
                              int labelResId,
                              SeekBarSettingsLayoutHolder holder) {
            super(context, name, iconResId, labelResId, holder);
        }

        public abstract int getProgress(Context context);

        public abstract void setProgress(Context context, int progress);

        public abstract int getMinValue(Context context);
        public abstract int getMaxValue(Context context);
        public abstract int getStep(Context context);
    }

    public interface RadioSettingItem {

        public CharSequence getLabel();
        public String getId();

    }

    public abstract static class RadioSetting<T extends  RadioSettingItem> extends Setting {

        private List<T> mRadioItems = new ArrayList<>();
        private Object mLock = new Object();

        public RadioSetting(Context context,
                            String name,
                            int iconResId,
                            int labelResId,
                            RadioSettingsLayoutHolder holder,
                            T[] items) {
            super(context, name, iconResId, labelResId, holder);

            addItems(items);
        }

        public void addItem(T item) {
            synchronized (mLock) {
                mRadioItems.add(item);
            }

            notifyDataChanges();
        }

        public void addItems(T[] items) {
            if (items == null
                    || items.length <= 0) {
                return;
            }

            synchronized (mLock) {
                for (T item : items) {
                    mRadioItems.add(item);
                }
            }

            notifyDataChanges();
        }

        public void clear() {
            synchronized (mLock) {
                mRadioItems.clear();
            }

            notifyDataChanges();
        }

        public T getItem(int position) {
            return mRadioItems.get(position);
        }

        public int getItemCount() {
            return mRadioItems.size();
        }

        public T findItemById(String itemId) {
            if (TextUtils.isEmpty(itemId)) {
                return null;
            }

            for (T item: mRadioItems) {
                if (itemId.equals(item.getId())) {
                    return item;
                }
            }

            return null;
        }

        protected abstract String getSelectedId();
        protected abstract void setSelected(String selectedId);

    }

    public class RadioSettingsLayoutHolder<T extends RadioSettingItem> extends BaseSettingLayoutHolder {

        private RadioGroup mRadioGroup;

        @Override
        public View onCreateView(Context context,
                                 LayoutInflater layoutInflater,
                                 Setting setting) {
            View view = layoutInflater.inflate(
                    R.layout.layout_setting_radio, null);

            bingSetting(view, setting);

            return view;
        }

        @Override
        public void invalidate(Context context, Setting setting) {
            if (mRadioGroup == null) {
                return;
            }

            mRadioGroup.removeAllViews();

            if (setting instanceof RadioSetting) {
                bindRadios(getContext(), mRadioGroup,
                        (RadioSetting)setting);
            }
        }

        @Override
        protected void bingSetting(View settingView, Setting setting) {
            super.bingSetting(settingView, setting);

            if (settingView == null
                    || setting instanceof RadioSetting == false) {
                return;
            }

            final Context context = getContext();
            if (context == null) {
                return;
            }

            final RadioSetting radioSetting = (RadioSetting) setting;

            mRadioGroup = (RadioGroup) settingView.findViewById(
                    R.id.selection_group);
            if (mRadioGroup != null) {

            }
        }

        private void bindRadios(Context context,
                                RadioGroup radioGroup,
                                final RadioSetting<T> radioSetting) {
            if (context == null
                    || radioGroup == null
                    || radioSetting == null) {
                return;
            }

            final int N = radioSetting.getItemCount();
            if (N <= 0) {
                return;
            }

            final String selectedId = radioSetting.getSelectedId();

            RadioSettingItem item;
            RadioButton rb;
            RadioButton[] rbs = new RadioButton[N];
            for (int i = 0; i < N; i++) {
                item = radioSetting.getItem(i);

                rb = new RadioButton(context);

                rb.setText(item.getLabel());
                rb.setTextAppearance(context, R.style.SettingsText);
                rb.setTag(item);
                rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked && compoundButton != null) {
                            Object o = compoundButton.getTag();
                            if (o instanceof RadioSettingItem) {
                                RadioSettingItem i = (RadioSettingItem)o;
                                radioSetting.setSelected(i.getId());
                            }

                            radioSetting.notifySettingsChanged();
                        }
                    }
                });

                mRadioGroup.addView(rb);
                rbs[i] = rb;
            }

            for (int i = 0; i < N; i++) {
                item = radioSetting.getItem(i);
                rb = rbs[i];

                if (!TextUtils.isEmpty(selectedId)
                        && selectedId.equals(item.getId())) {
                    rb.setChecked(true);
                } else {
                    rb.setChecked(false);
                }
            }
        }

    }


    public class SwitchSettingsLayoutHolder extends TextSettingsLayoutHolder {

        @Override
        public View onCreateView(Context context,
                                 LayoutInflater layoutInflater,
                                 Setting setting) {
            View view = layoutInflater.inflate(
                    R.layout.layout_setting_switch, null);

            bingSetting(view, setting);

            return view;
        }

        @Override
        public void invalidate(Context context, Setting setting) {

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

    public class TextSettingsLayoutHolder extends BaseSettingLayoutHolder {

        @Override
        public View onCreateView(Context context,
                                 LayoutInflater layoutInflater,
                                 Setting setting) {
            View view = layoutInflater.inflate(
                    R.layout.layout_setting_text, null);

            bingSetting(view, setting);

            return view;
        }

        @Override
        public void invalidate(Context context, Setting setting) {

        }

        @Override
        protected void bingSetting(View settingView, Setting setting) {
            super.bingSetting(settingView, setting);

            if (settingView == null
                    || setting instanceof TextSetting == false) {
                return;
            }

            final Context context = getContext();
            if (context == null) {
                return;
            }

            final TextSetting textSetting = (TextSetting) setting;

            TextView descView = settingView.findViewById(R.id.setting_desc);
            if (descView != null) {
                final CharSequence desc = textSetting.getDesc();

                descView.setText(desc);
                if (TextUtils.isEmpty(desc)) {
                    descView.setVisibility(View.GONE);
                } else {
                    descView.setVisibility(View.VISIBLE);
                }
            }

            View rootView = settingView.findViewById(
                    R.id.setting_root);
            if (rootView != null) {
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Logger.debug("clicked on text settings");
                        textSetting.notifySettingsChanged();
                    }
                });
            }
        }
    }

    public class SeekBarSettingsLayoutHolder extends BaseSettingLayoutHolder {

        @Override
        public View onCreateView(Context context,
                                 LayoutInflater layoutInflater,
                                 Setting setting) {
            View view = layoutInflater.inflate(
                    R.layout.layout_setting_seek_bar, null);

            bingSetting(view, setting);

            return view;
        }

        @Override
        public void invalidate(Context context, Setting setting) {
            View view = getView();
            if (view == null) {
                return;
            }

            if (setting instanceof SeekBarSetting == false) {
                return;
            }

            SeekBarSetting seekBarSetting = (SeekBarSetting)setting;

            SeekBar seekBarView = (SeekBar) view.findViewById(
                    R.id.setting_seek_bar);
            syncProgressWithSetting(getContext(), seekBarView, seekBarSetting);
        }

        private void syncProgressWithSetting(Context context,
                                             SeekBar seekBar,
                                             SeekBarSetting seekBarSetting) {
            if (context == null
                    || seekBar == null
                    || seekBarSetting == null) {
                return;
            }

            final int progress = seekBarSetting.getProgress(context);
            final int step = seekBarSetting.getStep(context);

            final int min = seekBarSetting.getMinValue(context);
            final int max = seekBarSetting.getMaxValue(context);
            final int prg = min + (progress * step);

/*
            Logger.debug("prg = %d, [min: %d, max: %d, step: %d",
                    prg, min, max, step);
*/

            seekBar.setProgress((prg - min) / step);
            seekBar.setMax((max - min) / step);
        }

        @Override
        protected void bingSetting(View settingView, Setting setting) {
            super.bingSetting(settingView, setting);

            if (settingView == null
                    || setting instanceof SeekBarSetting == false) {
                return;
            }

            final Context context = getContext();
            if (context == null) {
                return;
            }

            final SeekBarSetting seekBarSetting = (SeekBarSetting) setting;

            final TextView seekValView = (TextView) settingView.findViewById(
                    R.id.setting_seek_value);
            if (seekValView != null) {
                seekValView.setText(String.valueOf(
                        seekBarSetting.getProgress(context)));
            }

            SeekBar seekBarView = (SeekBar) settingView.findViewById(
                    R.id.setting_seek_bar);
            if (seekBarView != null) {
                syncProgressWithSetting(getContext(), seekBarView, seekBarSetting);
                seekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int prg = seekBarSetting.getMinValue(context)
                                + (progress * seekBarSetting.getStep(context));

                        if (seekValView != null) {
                            seekValView.setText(String.valueOf(prg));
                        }

                        seekBarSetting.setProgress(context, prg);
                        seekBarSetting.notifySettingsChanged();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                });
            }
        }
    }

    public abstract class BaseSettingLayoutHolder implements LayoutHolder {

        private View mView;

        @Override
        final public View createView(Context context, Setting setting) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);

            mView = onCreateView(context, layoutInflater, setting);

            bingSetting(mView, setting);

            return mView;
        }

        @Override
        public View getView() {
            return mView;
        }

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

        protected abstract View onCreateView(Context context,
                                             LayoutInflater layoutInflater,
                                             Setting setting);
    }

    private ViewGroup mSettingsContainer;

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

        reloadSettings();
    }

    protected void reloadSettings() {
        mSettingsContainer.removeAllViews();

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
    }

    public static void registerSettingChangesReceiver(Context context, BroadcastReceiver receiver) {
        if (context == null || receiver == null) {
            return;
        }

        IntentFilter filter = new IntentFilter(ACTION_SETTINGS_CHANGED);

        try {
            LocalBroadcastManager.getInstance(context)
                    .registerReceiver(receiver, filter);
        } catch (Exception e) {
            Logger.warn("could not register receiver [%s] on %s: %s",
                    receiver, ACTION_SETTINGS_CHANGED, e.toString());
        }
    }

    public static void unregisterSettingChangesReceiver(Context context, BroadcastReceiver receiver) {
        if (context == null || receiver == null) {
            return;
        }

        try {
            LocalBroadcastManager.getInstance(context)
                    .unregisterReceiver(receiver);
        } catch (Exception e) {
            Logger.warn("could not unregister receiver [%s] from %s: %s",
                    receiver, ACTION_SETTINGS_CHANGED, e.toString());
        }
    }

}
