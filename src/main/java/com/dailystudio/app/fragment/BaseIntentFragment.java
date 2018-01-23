package com.dailystudio.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class BaseIntentFragment extends Fragment {
	
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
				getChildFragmentManager().beginTransaction();

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
				getChildFragmentManager().beginTransaction();

		if (exitAnim > 0) {
			ft.setCustomAnimations(0, exitAnim);
		}

		ft.hide(fragment);

		ft.commit();
	}

	public void hideFragmentOnCreate(int fragmentId) {
		hideFragmentOnCreate(findFragment(fragmentId));
	}

	public void hideFragmentOnCreate(Fragment fragment) {
		if (fragment == null) {
			return;
		}

		FragmentTransaction ft =
				getChildFragmentManager().beginTransaction();

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
		FragmentManager frgmgr = getChildFragmentManager();
		if (frgmgr == null) {
			return null;
		}

		return frgmgr.findFragmentById(fragmentId);
	}

}
