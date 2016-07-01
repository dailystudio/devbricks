package com.dailystudio.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dailystudio.app.widget.DeferredHandler;

public abstract class AbsRecyclerViewFragment<Item, ItemSet, ItemHolder extends RecyclerView.ViewHolder>
		extends AbsLoaderFragment<ItemSet> {

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter<ItemHolder> mAdapter;
	private RecyclerView.ItemDecoration mItemDecoration;
	private RecyclerView.LayoutManager mLayoutManager;

	private View mEmptyView;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		bindAdapterView();
	}

	protected int getAdapterViewId() {
		return android.R.id.list;
	}
	
	@SuppressWarnings("unchecked")
    protected void bindAdapterView() {
		final View fragmentView = getView();
		if (fragmentView == null) {
			return;
		}

		RecyclerView oldRecyclerView = mRecyclerView;

		if (oldRecyclerView != null) {
			oldRecyclerView.clearDisappearingChildren();
			oldRecyclerView.clearAnimation();
			oldRecyclerView.setAdapter(null);
			oldRecyclerView.setVisibility(View.GONE);
//			oldRecyclerView.setEmptyView(null);
			oldRecyclerView.setLayoutManager(null);
			oldRecyclerView.removeItemDecoration(mItemDecoration);
		}
		
		mAdapter = onCreateAdapter();
		mItemDecoration = onCreateItemDecoration();

		mLayoutManager = onCreateLayoutManager();
		
		mRecyclerView = (RecyclerView) fragmentView.findViewById(
		        getAdapterViewId());
		if (mRecyclerView != null) {
			mRecyclerView.setAdapter(mAdapter);
			mRecyclerView.setLayoutManager(mLayoutManager);
			mRecyclerView.setVisibility(View.VISIBLE);
			mRecyclerView.scheduleLayoutAnimation();

			if (mItemDecoration != null) {
				mRecyclerView.addItemDecoration(mItemDecoration);
			}

			mEmptyView = fragmentView.findViewById(getEmptyViewId());
			if (mEmptyView != null && mAdapter != null) {
				mAdapter.registerAdapterDataObserver(mAdapterDataObserver);
			}
		}
	}
	
	public RecyclerView.Adapter<ItemHolder> getAdapter() {
		return mAdapter;
	}

	public void switchLayout() {
		if (mRecyclerView != null) {
			mRecyclerView.removeItemDecoration(mItemDecoration);
		}

		mItemDecoration = onCreateItemDecoration();
		mLayoutManager = onCreateLayoutManager();

		if (mRecyclerView != null) {
			mRecyclerView.setLayoutManager(mLayoutManager);

			if (mItemDecoration != null) {
				mRecyclerView.addItemDecoration(mItemDecoration);
			}
		}
	}

	public RecyclerView getRecyclerView() {
		return mRecyclerView;
	}

	public RecyclerView.ItemDecoration getItemDecoration() {
		return mItemDecoration;
	}

 	@Override
 	public void onLoadFinished(Loader<ItemSet> loader, ItemSet data) {
 		bindData(mAdapter, data);
 	}
 	
 	@Override
 	public void onLoaderReset(Loader<ItemSet> loader) {
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
		mHandler.removeCallbacks(notifyAdapterChangedRunnable);
		post(notifyAdapterChangedRunnable);
	}

	protected void notifyAdapterChangedDelay(long delay) {
		mHandler.removeCallbacks(notifyAdapterChangedRunnable);
		postDelayed(notifyAdapterChangedRunnable, delay);
	}

	protected void notifyAdapterChangedOnIdle() {
		mDeferredHandler.cancelRunnable(notifyAdapterChangedRunnable);
		mDeferredHandler.postIdle(notifyAdapterChangedRunnable);
	}
 	
	abstract protected void bindData(RecyclerView.Adapter adapter, ItemSet data);

	abstract protected RecyclerView.Adapter onCreateAdapter();
	abstract protected RecyclerView.LayoutManager onCreateLayoutManager();
	abstract protected RecyclerView.ItemDecoration onCreateItemDecoration();

	private Handler mHandler = new Handler();
	private DeferredHandler mDeferredHandler = new DeferredHandler();
	
	private Runnable notifyAdapterChangedRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (mAdapter == null) {
				return;
			}

			mAdapter.notifyDataSetChanged();
		}
		
	};

	private RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {

		@Override
		public void onChanged() {
			super.onChanged();

			if (mEmptyView == null || mRecyclerView == null) {
				return;
			}

			RecyclerView.Adapter adapter = getAdapter();
			if (adapter == null) {
				return;
			}

			final int N = adapter.getItemCount();
			if (N <= 0) {
				mEmptyView.setVisibility(View.VISIBLE);
				mRecyclerView.setVisibility(View.GONE);
			} else {
				mEmptyView.setVisibility(View.GONE);
				mRecyclerView.setVisibility(View.VISIBLE);
			}
		}

	};

}
