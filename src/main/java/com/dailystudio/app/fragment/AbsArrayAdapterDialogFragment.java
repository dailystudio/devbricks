package com.dailystudio.app.fragment;

import androidx.loader.content.Loader;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.dailystudio.development.Logger;

import java.util.Comparator;
import java.util.List;

public abstract class AbsArrayAdapterDialogFragment<T> extends AbsAdapterDialogFragment<T, List<T>> {
	
    @Override
    public void onLoadFinished(Loader<List<T>> loader, List<T> data) {
        super.onLoadFinished(loader, data);
        
        sortAdapterIfPossible();
    }

    @Override
    public void onLoaderReset(Loader<List<T>> loader) {
        super.onLoaderReset(loader);
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
