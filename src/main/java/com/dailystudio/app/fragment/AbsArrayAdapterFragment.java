package com.dailystudio.app.fragment;

import java.util.Comparator;
import java.util.List;

import com.dailystudio.development.Logger;

import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public abstract class AbsArrayAdapterFragment<T> extends AbsAdapterFragment<T, List<T>> {
	
    @Override
    public void onLoadFinished(Loader<List<T>> loader, List<T> data) {
        super.onLoadFinished(loader, data);
        
        sortAdapterIfPossible();
    }
    
    @Override
    protected void bindData(BaseAdapter adapter, List<T> data) {
        if (adapter instanceof ArrayAdapter == false) {
            return;
        }
        
        @SuppressWarnings("unchecked")
        ArrayAdapter<T> objectAdapter =
            (ArrayAdapter<T>)adapter;
        
        if (clearBeforeBindData()) {
        	objectAdapter.clear();
        }

        if (data == null) {
            return;
        }

        for (T o: data) {
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

    protected Comparator<T> getComparator() {
        return null;
    }
	
    private Runnable mSortAdapterRunnable = new Runnable() {
        
        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            BaseAdapter adapter = getAdapter();
            if (adapter instanceof ArrayAdapter == false) {
                return;
            }
        
            Comparator<T> comparator = getComparator();
            Logger.debug("comparator = %s", comparator);
            if (comparator == null) {
                return;
            }
            
            ((ArrayAdapter<T>)adapter).sort(comparator);
        }

    };
    
}
