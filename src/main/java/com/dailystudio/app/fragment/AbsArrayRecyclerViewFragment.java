package com.dailystudio.app.fragment;

import android.support.v7.widget.RecyclerView;

import com.dailystudio.app.ui.AbsArrayRecyclerAdapter;
import com.dailystudio.development.Logger;

import java.util.Comparator;
import java.util.List;

/**
 * Created by nanye on 16/6/9.
 */
public abstract class AbsArrayRecyclerViewFragment<Item, ItemHolder extends RecyclerView.ViewHolder>
        extends AbsRecyclerViewFragment<Item, List<Item>, ItemHolder> {

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

}
