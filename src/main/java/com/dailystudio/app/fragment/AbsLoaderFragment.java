package com.dailystudio.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

public abstract class AbsLoaderFragment<T> extends BaseIntentFragment implements LoaderCallbacks<T> {
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getLoaderManager().initLoader(getLoaderId(), createLoaderArguments(), this);
	}
	
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		restartLoader();
	}
	
	public void restartLoader() {
		getLoaderManager().restartLoader(getLoaderId(), createLoaderArguments(), this);
	}

	protected int getEmptyViewId() {
		return android.R.id.empty;
	}
	
	abstract protected int getLoaderId();
	abstract protected Bundle createLoaderArguments();

}
