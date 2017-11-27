package com.dailystudio.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import com.dailystudio.app.ui.ExpandableGroup;
import com.dailystudio.app.ui.ExpandableListData;
import com.dailystudio.app.widget.DeferredHandler;
import com.dailystudio.development.Logger;

public abstract class AbsExpandableListAdapterFragment<Group extends ExpandableGroup<MapKey>, Item, MapKey>
		extends AbsLoaderFragment<ExpandableListData<Group, Item, MapKey>>
	implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {
	
	public interface OnListItemSelectedListener {
		
        public void onListItemSelected(Object itemData);
        public void onListGroupSelected(Object groupData);

    }

	private ExpandableListView mAdapterView;
	private BaseExpandableListAdapter mAdapter;
	
    private OnListItemSelectedListener mOnListItemSelectedListener;
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		bindAdapterView();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		if (mOnListItemSelectedListener != null) {
			final BaseExpandableListAdapter adapter = getAdapter();
			if (adapter == null) {
				return false;
			}

			Object data = adapter.getChild(groupPosition, childPosition);

			mOnListItemSelectedListener.onListItemSelected(data);

			return true;
		}

		return false;
	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
		if (mOnListItemSelectedListener != null) {
			final BaseExpandableListAdapter adapter = getAdapter();
			if (adapter == null) {
				return false;
			}

			Object group = adapter.getGroup(groupPosition);

			mOnListItemSelectedListener.onListGroupSelected(group);

			return true;
		}

		return false;
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

		ExpandableListView oldAdapterView = mAdapterView;

		if (oldAdapterView != null) {
			oldAdapterView.clearDisappearingChildren();
			oldAdapterView.clearAnimation();
			oldAdapterView.setAdapter((BaseExpandableListAdapter)null);
			oldAdapterView.setOnChildClickListener(null);
			oldAdapterView.setOnGroupClickListener(null);
			oldAdapterView.setVisibility(View.GONE);
			oldAdapterView.setEmptyView(null);
		}
		
		mAdapter = onCreateAdapter();
		
		mAdapterView = (ExpandableListView) fragmentView.findViewById(
		        getAdapterViewId());
		if (mAdapterView != null) {
			mAdapterView.setAdapter(mAdapter);
			mAdapterView.setOnChildClickListener(this);
			mAdapterView.setOnGroupClickListener(this);
			mAdapterView.setVisibility(View.VISIBLE);
			mAdapterView.scheduleLayoutAnimation();
			
			final View emptyView = fragmentView.findViewById(getEmptyViewId());
			if (emptyView != null) {
				mAdapterView.setEmptyView(emptyView);
			}
		}
	}
	
	public BaseExpandableListAdapter getAdapter() {
		return mAdapter;
	}
	
	public ExpandableListView getAdapterView() {
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
    }

	@Override
	public void restartLoader() {
		hideList();
		super.restartLoader();
	}

	private void hideList() {
		View view = getAdapterView();
		if (view == null) {
			return;
		}

		view.setVisibility(View.INVISIBLE);
	}

	private void showList() {
		View view = getAdapterView();
		if (view == null) {
			return;
		}

		view.setVisibility(View.VISIBLE);
	}

 	@Override
 	public void onLoadFinished(Loader<ExpandableListData<Group, Item, MapKey>> loader, ExpandableListData<Group, Item, MapKey> data) {
 		super.onLoadFinished(loader, data);

 		showList();

		bindData(mAdapter, data);
 	}
 	
 	@Override
 	public void onLoaderReset(Loader<ExpandableListData<Group, Item, MapKey>> loader) {
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
 	
	abstract protected void bindData(BaseExpandableListAdapter adapter, ExpandableListData<Group, Item, MapKey> data);

	abstract protected BaseExpandableListAdapter onCreateAdapter();
	
	private Handler mHandler = new Handler();
	private DeferredHandler mDeferredHandler = new DeferredHandler();
	
	private Runnable notifyAdapterChangedRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (mAdapter instanceof BaseExpandableListAdapter == false) {
				return;
			}
			
			((BaseExpandableListAdapter)mAdapter).notifyDataSetChanged();
		}
		
	};

}
