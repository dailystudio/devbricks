package com.dailystudio.app.ui;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import androidx.annotation.WorkerThread;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by nanye on 17/1/22.
 */

public abstract class AbsAsyncLayout<T> extends FrameLayout {

    private class FloatWindowAsyncTask extends AsyncTask<Void, Void, T> {

        private Context mContext;

        private FloatWindowAsyncTask(Context context) {
            mContext = context.getApplicationContext();
        }

        @Override
        protected T doInBackground(Void... params) {
            if (params == null || params.length <= 0) {
                return null;
            }

            return execTaskInBackground(getContext());
        }

        protected Context getContext() {
            return mContext;
        }

        @Override
        protected void onPostExecute(T t) {
            super.onPostExecute(t);

            onTaskFinished(t);
        }

    }

    private FloatWindowAsyncTask mAsyncTask;

    private View mContentView;

    public AbsAsyncLayout(Context context) {
        this(context, null);
    }

    public AbsAsyncLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsAsyncLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final LayoutInflater inflater = LayoutInflater.from(context);
        mContentView = onCreateContentView(inflater, context);
        if (mContentView != null) {
            addView(mContentView);
        }
    }

    protected View getContentView() {
        return mContentView;
    }

    public boolean isPermissionGranted(String permission){
        return (ContextCompat.checkSelfPermission(getContext(),
                permission) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        restartAsyncTask();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        stopAsyncTask();
    }

    public void restartAsyncTask() {
        if (mAsyncTask != null) {
            AsyncTask.Status status = mAsyncTask.getStatus();
            if (status != AsyncTask.Status.FINISHED) {
                mAsyncTask.cancel(true);
            }
        }

        mAsyncTask = onCreateAsyncTask(getContext());
        if (mAsyncTask == null) {
            return;
        }

        mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                (Void)null);
    }

    protected void stopAsyncTask() {
        if (mAsyncTask != null) {
            AsyncTask.Status status = mAsyncTask.getStatus();
            if (status != AsyncTask.Status.FINISHED) {
                mAsyncTask.cancel(true);
            }
        }

        mAsyncTask = null;
    }

    private FloatWindowAsyncTask onCreateAsyncTask(Context context) {
        return new FloatWindowAsyncTask(context);
    }

    protected abstract View onCreateContentView(LayoutInflater inflater,
                                                Context context);

    @WorkerThread
    protected abstract T execTaskInBackground(Context context);

    protected abstract void onTaskFinished(T result);

}
