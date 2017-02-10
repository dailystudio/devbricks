package com.dailystudio.app.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;

import com.dailystudio.development.Logger;

public abstract class AbsLoaderFragment<T> extends BaseIntentFragment implements LoaderCallbacks<T> {
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		LoaderManager ldMgr = getLoaderManagerSafe();
		if (ldMgr != null) {
			ldMgr.initLoader(getLoaderId(), createLoaderArguments(), this);
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
		}
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

	public boolean isPermissionGranted(String permission){
		return (ContextCompat.checkSelfPermission(getContext(),
				permission) == PackageManager.PERMISSION_GRANTED);
	}

	abstract protected int getLoaderId();
	abstract protected Bundle createLoaderArguments();

}
