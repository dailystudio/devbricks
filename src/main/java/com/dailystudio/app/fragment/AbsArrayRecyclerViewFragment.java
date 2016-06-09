package com.dailystudio.app.fragment;

import android.annotation.NonNull;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.dailystudio.development.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nanye on 16/6/9.
 */
public abstract class AbsArrayRecyclerViewFragment<Item, ItemHolder extends RecyclerView.ViewHolder>
        extends AbsRecyclerViewFragment<Item, List<Item>, ItemHolder> {

    public static abstract class AbsArrayRecyclerAdapter<T, ItemHolder extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<ItemHolder> {

        private Context mContext;

        private List<T> mObjects;

        private final Object mLock = new Object();

        protected LayoutInflater mLayoutInflater;

        public AbsArrayRecyclerAdapter(Context context) {
            this(context, new ArrayList<T>());
        }

        public AbsArrayRecyclerAdapter(Context context, @NonNull List<T> objects) {
            mContext = context;
            mObjects = objects;

            mLayoutInflater = LayoutInflater.from(context);
        }

        public void add(T object) {
            synchronized (mLock) {
                mObjects.add(object);
            }

            notifyDataSetChanged();
        }

        public void addAll(T... objects) {
            synchronized (mLock) {
                Collections.addAll(mObjects, objects);
            }

            notifyDataSetChanged();
        }

        public void insert(T object, int index) {
            synchronized (mLock) {
                mObjects.add(index, object);
            }

            notifyDataSetChanged();
        }

        public void remove(T object) {
            synchronized (mLock) {
                mObjects.remove(object);
            }

            notifyDataSetChanged();
        }

        public void clear() {
            synchronized (mLock) {
                mObjects.clear();
            }

            notifyDataSetChanged();
        }

        public T getItem(int position) {
            return mObjects.get(position);
        }

        public void sort(Comparator<? super T> comparator) {
            synchronized (mLock) {
                Collections.sort(mObjects, comparator);
            }

            notifyDataSetChanged();
        }

        public Context getContext() {
            return mContext;
        }

        @Override
        public int getItemCount() {
            return mObjects.size();
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

}
