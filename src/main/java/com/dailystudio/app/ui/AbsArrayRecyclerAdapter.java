package com.dailystudio.app.ui;

import android.annotation.NonNull;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nanye on 16/6/9.
 */
public abstract class AbsArrayRecyclerAdapter<Item, ItemHolder extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<ItemHolder> {

    private Context mContext;

    private List<Item> mObjects;

    private final Object mLock = new Object();

    protected LayoutInflater mLayoutInflater;

    public AbsArrayRecyclerAdapter(Context context) {
        this(context, new ArrayList<Item>());
    }

    public AbsArrayRecyclerAdapter(Context context, @NonNull List<Item> objects) {
        mContext = context;
        mObjects = objects;

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        if (holder instanceof AbsArrayItemViewHolder == false) {
            return;
        }

        Item item = getItem(position);

        ((AbsArrayItemViewHolder)holder).bindItem(getContext(), item);
    }

    public void add(Item object) {
        synchronized (mLock) {
            mObjects.add(object);
        }

        notifyDataSetChanged();
    }

    public void addAll(Item... objects) {
        synchronized (mLock) {
            Collections.addAll(mObjects, objects);
        }

        notifyDataSetChanged();
    }

    public void insert(Item object, int index) {
        synchronized (mLock) {
            mObjects.add(index, object);
        }

        notifyDataSetChanged();
    }

    public void remove(Item object) {
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

    public Item getItem(int position) {
        return mObjects.get(position);
    }

    public void sort(Comparator<? super Item> comparator) {
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
