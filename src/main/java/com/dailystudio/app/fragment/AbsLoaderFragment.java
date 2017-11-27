package com.dailystudio.app.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.view.View;

import com.dailystudio.R;
import com.dailystudio.development.Logger;

public abstract class AbsLoaderFragment<T> extends BaseIntentFragment implements LoaderCallbacks<T> {
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LoaderManager ldMgr = getLoaderManagerSafe();
		if (ldMgr != null) {
			ldMgr.initLoader(getLoaderId(), createLoaderArguments(), this);
			showLoadingView();
		}
	}
	
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		restartLoader();
	}
	
	public void restartLoader() {
		LoaderManager ldMgr = getLoaderManagerSafe();
		if (ldMgr != null) {
			ldMgr.restartLoader(getLoaderId(), createLoaderArguments(), this);
			showLoadingView();
		}
	}

	@Override
	public void onLoadFinished(Loader<T> loader, T data) {
		hideLoadingView();
	}

	@Override
	public void onLoaderReset(Loader<T> loader) {
		hideLoadingView();
	}

	public void deferredRestartLoader(long milliseconds) {
		if (milliseconds <= 0) {
			restartLoader();

			return;
		}

		mHandler.removeCallbacks(mDeferredRestartLoaderRunnable);
		mHandler.postDelayed(mDeferredRestartLoaderRunnable, milliseconds);
	}

	private LoaderManager getLoaderManagerSafe() {
		LoaderManager loaderManager;

		try {
			loaderManager = getLoaderManager();

		} catch (IllegalStateException e) {
			Logger.warn("could not get loader manager: %s",
					e.toString());

			loaderManager = null;
		}

		return loaderManager;
	}

	protected int getEmptyViewId() {
		return android.R.id.empty;
	}

	private View findLoadingView() {
		View rootView = getView();
		if (rootView == null) {
			return null;
		}

		return rootView.findViewById(getLoadingViewId());
	}

	private View findEmptyView() {
		View rootView = getView();
		if (rootView == null) {
			return null;
		}

		return rootView.findViewById(getEmptyViewId());
	}

	protected void showLoadingView() {
		View emptyView = findEmptyView();
		if (emptyView != null) {
			emptyView.setVisibility(View.GONE);
		}

		View loadingView = findLoadingView();
		if (loadingView == null) {
			return;
		}

		loadingView.setVisibility(View.VISIBLE);
	}

	protected void hideLoadingView() {
		View loadingView = findLoadingView();
		if (loadingView == null) {
			return;
		}

		loadingView.setVisibility(View.GONE);
	}

	protected int getLoadingViewId() {
		return R.id.loading;
	}

	public boolean isPermissionGranted(String permission){
		return (ContextCompat.checkSelfPermission(getContext(),
				permission) == PackageManager.PERMISSION_GRANTED);
	}

	abstract protected int getLoaderId();
	abstract protected Bundle createLoaderArguments();

	private Handler mHandler = new Handler();

	private Runnable mDeferredRestartLoaderRunnable = new Runnable() {

		@Override
		public void run() {
			restartLoader();
		}

	};

}
