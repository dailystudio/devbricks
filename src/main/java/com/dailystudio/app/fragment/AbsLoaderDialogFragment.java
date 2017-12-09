package com.dailystudio.app.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;

import com.dailystudio.R;
import com.dailystudio.development.Logger;

public abstract class AbsLoaderDialogFragment<T> extends BaseIntentDialogFragment implements LoaderCallbacks<T> {

	private boolean mShowLoadingView = true;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getLoaderManager().initLoader(getLoaderId(), createLoaderArguments(), this);

		if (shouldShowLoadingView()) {
			showLoadingView();
		}
	}

	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		restartLoader();
	}
	
	public void restartLoader() {
		getLoaderManager().restartLoader(getLoaderId(), createLoaderArguments(), this);

		if (shouldShowLoadingView()) {
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

	private View findLoadingView() {
        Dialog dialog = getDialog();
		if (dialog == null) {
			return null;
		}

		return dialog.findViewById(getLoadingViewId());
	}

	protected View findEmptyView() {
		Dialog dialog = getDialog();
		if (dialog == null) {
			return null;
		}

		return dialog.findViewById(getEmptyViewId());
	}

	public boolean shouldShowLoadingView() {
		return mShowLoadingView;
	}

	public void setShowLoadingView(boolean show) {
		mShowLoadingView = show;
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
        Logger.debug("hide loading view: %s", loadingView);
		if (loadingView == null) {
			return;
		}


		loadingView.setVisibility(View.GONE);
	}

	protected int getLoadingViewId() {
		return R.id.loading;
	}

	protected int getEmptyViewId() {
		return android.R.id.empty;
	}

	abstract protected int getLoaderId();
	abstract protected Bundle createLoaderArguments();

}
