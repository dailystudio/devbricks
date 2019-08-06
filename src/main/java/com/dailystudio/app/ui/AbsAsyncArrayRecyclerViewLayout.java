package com.dailystudio.app.ui;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.dailystudio.development.Logger;

import java.util.Comparator;
import java.util.List;

/**
 * Created by nanye on 17/1/22.
 */

public abstract class AbsAsyncArrayRecyclerViewLayout<Item, ItemHolder extends RecyclerView.ViewHolder>
        extends AbsAsyncRecyclerViewLayout<Item, List<Item>, ItemHolder> {

    public interface OnRecyclerViewItemClickListener {

        public void onItemClick(View view, Object item);

    }

    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    public AbsAsyncArrayRecyclerViewLayout(Context context) {
        this(context, null);
    }

    public AbsAsyncArrayRecyclerViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsAsyncArrayRecyclerViewLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener l) {
        mOnRecyclerViewItemClickListener = l;
    }

    @Override
    protected void bindAdapterView() {
        RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter instanceof AbsArrayRecyclerAdapter) {
            ((AbsArrayRecyclerAdapter)oldAdapter).setOnItemClickListener(null);
        }

        super.bindAdapterView();

        RecyclerView.Adapter adapter = getAdapter();
        if (adapter instanceof AbsArrayRecyclerAdapter) {
            ((AbsArrayRecyclerAdapter)adapter).setOnItemClickListener(mOnItemClickListener);
        }
    }

    @Override
    protected void bindData(RecyclerView.Adapter adapter, List<Item> data) {
        if (adapter instanceof AbsArrayRecyclerAdapter == false) {
            return;
        }

        AbsArrayRecyclerAdapter objectAdapter =
                (AbsArrayRecyclerAdapter)adapter;

        if (clearBeforeBindData()) {
            objectAdapter.clear();
        }

        if (data == null) {
            return;
        }

        for (Item o: data) {
            objectAdapter.add(o);
        }
    }

    protected boolean clearBeforeBindData() {
        return true;
    }

    protected void sortAdapterIfPossible() {
        removeCallbacks(mSortAdapterRunnable);

        post(mSortAdapterRunnable);
    }

    protected Comparator<Item> getComparator() {
        return null;
    }

    private Runnable mSortAdapterRunnable = new Runnable() {

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            RecyclerView.Adapter adapter = getAdapter();
            if (adapter instanceof AbsArrayRecyclerAdapter == false) {
                return;
            }


            Comparator<Item> comparator = getComparator();
            Logger.debug("comparator = %s", comparator);
            if (comparator == null) {
                return;
            }

            ((AbsArrayRecyclerAdapter<Item, ItemHolder>)adapter).sort(comparator);
        }

    };

    protected void onItemClick(View view, Object item) {
        if (mOnRecyclerViewItemClickListener != null) {
            mOnRecyclerViewItemClickListener.onItemClick(view, item);
        }
    }

    private AbsArrayRecyclerAdapter.OnItemClickListener mOnItemClickListener = new AbsArrayRecyclerAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View view, Object item) {
            AbsAsyncArrayRecyclerViewLayout.this.onItemClick(view, item);
        }

    };
}
