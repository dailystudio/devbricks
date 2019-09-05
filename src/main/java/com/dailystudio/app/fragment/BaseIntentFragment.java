package com.dailystudio.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    public void showChildFragment(int fragmentId) {
        showChildFragment(fragmentId, 0);
    }

    public void showChildFragment(Fragment fragment) {
        showChildFragment(fragment, 0);
    }

    public void showChildFragment(int fragmentId, int enterAnim) {
        showChildFragment(findChildFragment(fragmentId), enterAnim);
    }

    public void showChildFragment(Fragment fragment, int enterAnim) {
        showFragment(fragment, enterAnim, true);
    }

    public void showFragment(int fragmentId) {
		showFragment(fragmentId, 0);
	}

    public void showFragment(Fragment fragment) {
        showFragment(fragment, 0);
    }

	public void showFragment(int fragmentId, int enterAnim) {
		showFragment(findFragment(fragmentId), enterAnim);
	}

	public void showFragment(Fragment fragment, int enterAnim) {
		showFragment(fragment, enterAnim, false);
	}

	private void showFragment(Fragment fragment, int enterAnim, boolean isChild) {
		if (fragment == null || fragment.isVisible()) {
			return;
		}

		FragmentManager fm = (isChild ?
                getChildFragmentManager() : getFragmentManager());
		FragmentTransaction ft =
                fm.beginTransaction();

		if (enterAnim > 0) {
			ft.setCustomAnimations(enterAnim, 0);
		}

		ft.show(fragment);

		ft.commitAllowingStateLoss();
	}

    public void hideChildFragment(int fragmentId) {
        hideChildFragment(fragmentId, 0);
    }

    public void hideChildFragment(Fragment fragment) {
        hideChildFragment(fragment, 0);
    }

    public void hideChildFragment(int fragmentId, int enterAnim) {
        hideChildFragment(findChildFragment(fragmentId), enterAnim);
    }

    public void hideChildFragment(Fragment fragment, int exitAnim) {
        hideFragment(fragment, exitAnim, true);
    }

    public void hideFragment(int fragmentId) {
		hideFragment(fragmentId, 0);
	}

    public void hideFragment(Fragment fragment) {
        hideFragment(fragment, 0);
    }

    public void hideFragment(int fragmentId, int enterAnim) {
        hideFragment(findFragment(fragmentId), enterAnim);
    }

    public void hideFragment(Fragment fragment, int exitAnim) {
        hideFragment(fragment, exitAnim, false);
    }

	private void hideFragment(Fragment fragment, int exitAnim, boolean isChild) {
		if (fragment == null || !fragment.isVisible()) {
			return;
		}

        FragmentManager fm = (isChild ?
                getChildFragmentManager() : getFragmentManager());
		FragmentTransaction ft =
                fm.beginTransaction();

		if (exitAnim > 0) {
			ft.setCustomAnimations(0, exitAnim);
		}

		ft.hide(fragment);

		ft.commit();
	}

	public void hideChildFragmentOnCreate(int fragmentId) {
		hideFragmentOnCreate(findChildFragment(fragmentId));
	}

	public void hideChildFragmentOnCreate(Fragment fragment) {
		hideFragmentOnCreate(fragment, true);
	}

	public void hideFragmentOnCreate(int fragmentId) {
		hideFragmentOnCreate(findFragment(fragmentId));
	}

	public void hideFragmentOnCreate(Fragment fragment) {
		hideFragmentOnCreate(fragment, false);
	}

	public void hideFragmentOnCreate(Fragment fragment, boolean isChild) {
		if (fragment == null) {
			return;
		}

		FragmentManager fm = (isChild ?
				getChildFragmentManager() : getFragmentManager());

		FragmentTransaction ft =
				fm.beginTransaction();

		ft.hide(fragment);

		ft.commit();
	}

	public boolean isFragmentVisible(int fragmentId) {
		return isFragmentVisible(findFragment(fragmentId));
	}

	public boolean isChildFragmentVisible(int fragmentId) {
		return isFragmentVisible(findChildFragment(fragmentId));
	}

	public boolean isFragmentVisible(Fragment fragment) {
		if (fragment == null) {
			return false;
		}

		return fragment.isVisible();
	}

	public Fragment findChildFragment(int fragmentId) {
		FragmentManager frgmgr = getChildFragmentManager();
		if (frgmgr == null) {
			return null;
		}

		return frgmgr.findFragmentById(fragmentId);
	}

	public Fragment findFragment(int fragmentId) {
		FragmentManager frgmgr = getFragmentManager();
		if (frgmgr == null) {
			return null;
		}

		return frgmgr.findFragmentById(fragmentId);
	}

}
