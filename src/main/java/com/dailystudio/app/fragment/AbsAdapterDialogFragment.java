package com.dailystudio.app.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.dailystudio.app.widget.DeferredHandler;
import com.dailystudio.development.Logger;

public abstract class AbsAdapterDialogFragment<Item, ItemSet> extends AbsLoaderDialogFragment<ItemSet>
	implements OnItemClickListener {
	
	public interface OnListItemSelectedListener {
		
        public void onListItemSelected(Object itemData);
        
    }

	private AdapterView<BaseAdapter> mAdapterView;
	private BaseAdapter mAdapter;
	
    private OnListItemSelectedListener mOnListItemSelectedListener;
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Dialog dialog = getDialog();
		Logger.debug("dialog = %s", dialog);

		if (dialog != null) {
			dialog.setOnShowListener(new DialogInterface.OnShowListener() {
				@Override
				public void onShow(DialogInterface dialog) {
					bindAdapterView();

					restartLoader();
				}
			});
		}
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		if (mOnListItemSelectedListener != null) {
			final BaseAdapter adapter = getAdapter();
			if (adapter == null) {
				return;
			}
			
			Object data = adapter.getItem(position);
			
			mOnListItemSelectedListener.onListItemSelected(data);
		}
	}

	protected int getAdapterViewId() {
		return android.R.id.list;
	}

	@SuppressWarnings("unchecked")
    protected void bindAdapterView() {
		final Dialog dialog = getDialog();
		Logger.debug("dialog = %s", dialog);
		if (dialog == null) {
			return;
		}

		AdapterView<?> oldAdapterView = mAdapterView;

		if (oldAdapterView != null) {
			oldAdapterView.clearDisappearingChildren();
			oldAdapterView.clearAnimation();
			oldAdapterView.setAdapter(null);
			oldAdapterView.setOnItemClickListener(null);
			oldAdapterView.setVisibility(View.GONE);
			oldAdapterView.setEmptyView(null);
		}
		
		mAdapter = onCreateAdapter();
		
		mAdapterView = (AdapterView<BaseAdapter>) dialog.findViewById(
				getAdapterViewId());
		Logger.debug("adpater view = %s", mAdapterView);
		if (mAdapterView != null) {
			mAdapterView.setAdapter(mAdapter);
			mAdapterView.setOnItemClickListener(this);
			mAdapterView.setVisibility(View.VISIBLE);
			mAdapterView.scheduleLayoutAnimation();
			
			final View emptyView = dialog.findViewById(getEmptyViewId());
			if (emptyView != null) {
				mAdapterView.setEmptyView(emptyView);
			}
		}
	}
	
	public BaseAdapter getAdapter() {
		return mAdapter;
	}
	
	public AdapterView<BaseAdapter> getAdapterView() {
		return mAdapterView;
	}
	
 	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        if (activity instanceof OnListItemSelectedListener) {
        	mOnListItemSelectedListener = (OnListItemSelectedListener) activity;
        } else {
        	Logger.warnning("host activity does not implements: %s", 
        			OnListItemSelectedListener.class.getSimpleName());
        }
    }

 	@Override
 	public void onLoadFinished(Loader<ItemSet> loader, ItemSet data) {
 		bindData(mAdapter, data);
 	};
 	
 	@Override
 	public void onLoaderReset(Loader<ItemSet> loader) {
 		bindData(mAdapter, null);
 	};
 	
 	protected void removeCallbacks(Runnable r) {
 		mHandler.removeCallbacks(r);
 	}
 	
 	protected void post(Runnable r) {
 		mHandler.post(r);
 	}
 	
 	protected void postDelayed(Runnable r, long delayMillis) {
 		mHandler.postDelayed(r, delayMillis);
 	}
 	
	protected void notifyAdapterChanged() {
		post(notifyAdapterChangedRunnable);
	}
 	
	protected void notifyAdapterChangedOnIdle() {
		mDeferredHandler.postIdle(notifyAdapterChangedRunnable);
	}
 	
	abstract protected void bindData(BaseAdapter adapter, ItemSet data);

	abstract protected BaseAdapter onCreateAdapter();
	
	private Handler mHandler = new Handler();
	private DeferredHandler mDeferredHandler = new DeferredHandler();
	
	private Runnable notifyAdapterChangedRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (mAdapter instanceof BaseAdapter == false) {
				return;
			}
			
			((BaseAdapter)mAdapter).notifyDataSetChanged();
		}
		
	};

}
