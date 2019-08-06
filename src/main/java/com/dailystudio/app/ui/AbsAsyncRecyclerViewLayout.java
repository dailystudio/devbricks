package com.dailystudio.app.ui;

import android.content.Context;
import android.os.Handler;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.dailystudio.app.widget.DeferredHandler;

/**
 * Created by nanye on 17/1/22.
 */

public abstract class AbsAsyncRecyclerViewLayout<Item, ItemSet, ItemHolder extends RecyclerView.ViewHolder>
        extends AbsAsyncLayout<ItemSet> {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ItemHolder> mAdapter;
    private RecyclerView.ItemDecoration mItemDecoration;
    private RecyclerView.LayoutManager mLayoutManager;

    private View mEmptyView;

    public AbsAsyncRecyclerViewLayout(Context context) {
        this(context, null);
    }

    public AbsAsyncRecyclerViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsAsyncRecyclerViewLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        bindAdapterView();

        super.onAttachedToWindow();
    }

    protected int getAdapterViewId() {
        return android.R.id.list;
    }

    protected int getEmptyViewId() {
        return android.R.id.empty;
    }

    @SuppressWarnings("unchecked")
    protected void bindAdapterView() {
        final View contentView = getContentView();
        if (contentView == null) {
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

        mRecyclerView = (RecyclerView) contentView.findViewById(
                getAdapterViewId());
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.scheduleLayoutAnimation();

            if (mItemDecoration != null) {
                mRecyclerView.addItemDecoration(mItemDecoration);
            }

            mEmptyView = contentView.findViewById(getEmptyViewId());
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
    protected void onTaskFinished(ItemSet result) {
        bindData(mAdapter, result);
    }

    protected void notifyAdapterChanged() {
        removeCallbacks(notifyAdapterChangedRunnable);
        post(notifyAdapterChangedRunnable);
    }

    protected void notifyAdapterChangedDelay(long delay) {
        removeCallbacks(notifyAdapterChangedRunnable);
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
