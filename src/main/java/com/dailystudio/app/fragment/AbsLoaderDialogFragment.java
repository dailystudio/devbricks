package com.dailystudio.app.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

import com.dailystudio.development.Logger;

public abstract class AbsLoaderDialogFragment<T> extends BaseIntentDialogFragment implements LoaderCallbacks<T> {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Dialog dialog = getDialog();
		Logger.debug("dialog = %s", dialog);

		if (dialog != null) {
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					setupViewsOnDialog(dialog);
				}

			});
		}

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

	protected void setupViewsOnDialog(DialogInterface dialog) {
	}
	
	abstract protected int getLoaderId();
	abstract protected Bundle createLoaderArguments();

}
