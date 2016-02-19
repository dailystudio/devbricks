package com.dailystudio.app.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.dailystudio.app.fragment.BaseIntentFragment;

import java.util.List;

/**
 * Created by nan on 2015/2/12.
 */
public class ActionBarFragmentActivity extends android.support.v7.app.ActionBarActivity {

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        final Intent intent = getIntent();

        bindIntent(intent);

        bindIntentOnFragments(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        bindIntent(intent);

        bindIntentOnFragments(intent);
    }

    protected void bindIntentOnFragments(Intent intent) {
        FragmentManager frgmgr =
                getSupportFragmentManager();
        if (frgmgr == null) {
            return;
        }

        List<Fragment> fragments = frgmgr.getFragments();
        if (fragments == null) {
            return;
        }

        for (Fragment f: fragments) {
            if (f instanceof BaseIntentFragment) {
                ((BaseIntentFragment)f).bindIntent(intent);
            }
        }
    }

    protected void bindIntent(Intent intent) {
    }

    public void showFragment(int fragmentId) {
        showFragment(fragmentId, 0);
    }

    public void showFragment(int fragmentId, int enterAnim) {
        showFragment(findFragment(fragmentId), enterAnim);
    }

    public void showFragment(Fragment fragment) {
        showFragment(fragment, 0);
    }

    public void showFragment(Fragment fragment, int enterAnim) {
        if (fragment == null || fragment.isVisible()) {
            return;
        }

        FragmentTransaction ft =
                getSupportFragmentManager().beginTransaction();

        if (enterAnim > 0) {
            ft.setCustomAnimations(enterAnim, 0);
        }

        ft.show(fragment);

        ft.commit();
    }

    public void hideFragment(int fragmentId) {
        hideFragment(fragmentId, 0);
    }

    public void hideFragment(int fragmentId, int enterAnim) {
        hideFragment(findFragment(fragmentId), enterAnim);
    }

    public void hideFragment(Fragment fragment) {
        hideFragment(fragment, 0);
    }

    public void hideFragment(Fragment fragment, int exitAnim) {
        if (fragment == null || !fragment.isVisible()) {
            return;
        }

        FragmentTransaction ft =
                getSupportFragmentManager().beginTransaction();

        if (exitAnim > 0) {
            ft.setCustomAnimations(0, exitAnim);
        }

        ft.hide(fragment);

        ft.commit();
    }

    public void hideFragmentOnCreate(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        FragmentTransaction ft =
                getSupportFragmentManager().beginTransaction();

        ft.hide(fragment);

        ft.commit();
    }

    public boolean isFragmentVisible(int fragmentId) {
        return isFragmentVisible(findFragment(fragmentId));
    }

    public boolean isFragmentVisible(Fragment fragment) {
        if (fragment == null) {
            return false;
        }

        return fragment.isVisible();
    }

    public Fragment findFragment(int fragmentId) {
        FragmentManager frgmgr = getSupportFragmentManager();
        if (frgmgr == null) {
            return null;
        }

        return frgmgr.findFragmentById(fragmentId);
    }

}
