package com.dailystudio.app.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import androidx.loader.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.dailystudio.app.widget.DeferredHandler;
import com.dailystudio.development.Logger;

public abstract class AbsAdapterDialogFragment<Item, ItemSet> extends AbsLoaderDialogFragment<ItemSet>
	implements OnItemClickListener, AdapterView.OnItemLongClickListener {

	public interface OnListItemSelectedListener {
		
        public void onListItemSelected(Object itemData);
        
    }

	public interface OnListItemHoldListener {

        public boolean onListItemHold(Object itemData);

    }

	private AdapterView<BaseAdapter> mAdapterView;
	private BaseAdapter mAdapter;
	
    private OnListItemSelectedListener mOnListItemSelectedListener;
    private OnListItemHoldListener mOnListItemHoldListener;

	@Override
	protected void setupViewsOnDialog(Dialog dialog) {
		super.setupViewsOnDialog(dialog);

		bindAdapterView();

		restartLoader();
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

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (mOnListItemHoldListener != null) {
			final BaseAdapter adapter = getAdapter();
			if (adapter == null) {
				return false;
			}

			Object data = adapter.getItem(position);

			return mOnListItemHoldListener.onListItemHold(data);
		}

		return false;
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
			mAdapterView.setOnItemLongClickListener(this);
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
        	Logger.warn("host activity does not implements: %s",
        			OnListItemSelectedListener.class.getSimpleName());
        }

        if (activity instanceof OnListItemHoldListener) {
        	mOnListItemHoldListener = (OnListItemHoldListener) activity;
        } else {
        	Logger.warn("host activity does not implements: %s",
					OnListItemHoldListener.class.getSimpleName());
        }
    }


	@Override
	public void restartLoader() {
		if (shouldShowLoadingView()) {
			hideListAndEmpty();
		}

		super.restartLoader();
	}

	private void hideListAndEmpty() {
		View recyclerView = getAdapterView();
		if (recyclerView != null) {
			recyclerView.setVisibility(View.INVISIBLE);
		}

		View emptyView = findEmptyView();
		if (emptyView != null) {
			emptyView.setVisibility(View.INVISIBLE);
		}
	}

	private void showList() {
		View view = getAdapterView();
		if (view == null) {
			return;
		}

		view.setVisibility(View.VISIBLE);
	}

	@Override
 	public void onLoadFinished(Loader<ItemSet> loader, ItemSet data) {
		super.onLoadFinished(loader, data);

		showList();

 		bindData(mAdapter, data);
 	}
 	
 	@Override
 	public void onLoaderReset(Loader<ItemSet> loader) {
		super.onLoaderReset(loader);

		showList();

 		bindData(mAdapter, null);
 	}
 	
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
