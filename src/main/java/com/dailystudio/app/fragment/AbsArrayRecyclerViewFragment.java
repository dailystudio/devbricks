package com.dailystudio.app.fragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dailystudio.app.ui.AbsArrayRecyclerAdapter;
import com.dailystudio.development.Logger;

import java.util.Comparator;
import java.util.List;

/**
 * Created by nanye on 16/6/9.
 */
public abstract class AbsArrayRecyclerViewFragment<Item, ItemHolder extends RecyclerView.ViewHolder>
        extends AbsRecyclerViewFragment<Item, List<Item>, ItemHolder> {

    public interface OnRecyclerViewItemClickListener {

        public void onItemClick(View view, Object item);

    }

    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnRecyclerViewItemClickListener) {
            mOnRecyclerViewItemClickListener = (OnRecyclerViewItemClickListener) activity;
        } else {
            Logger.warn("host activity does not implements: %s",
                    OnRecyclerViewItemClickListener.class.getSimpleName());
        }
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
            AbsArrayRecyclerViewFragment.this.onItemClick(view, item);
        }

    };

}
