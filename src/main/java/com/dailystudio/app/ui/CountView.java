package com.dailystudio.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.dailystudio.R;
import com.dailystudio.datetime.CalendarUtils;
import com.dailystudio.development.Logger;

public class CountView extends TextView {
	
	public static interface OnCountListener {
		
		public void onCountStart(CountView cv, long destCount);
		public void onCountAbort(CountView cv, long destCount);
		public void onCountFinished(CountView cv, long destCount);
		
	}
	

	private final int DEFAULT_MINI_DIGITS = 1;
	
	private final long DELTA_INC_DELAY = 20;
	private final long DURATION_ESTIMATION_UNIT = (1500 / 100) ;
	private final long MAX_COUNT_DURATION = (5 * CalendarUtils.SECOND_IN_MILLIS) ;
	
	private class CountRunnable implements Runnable {

		private long mStartTime = 0;
		private float mDeltaVal = 0;
		private long mStartCount = 0;
	
		private CountRunnable(long destCount, long startCount, long duration) {
			mStartCount = startCount;
			
			mDeltaVal = (float)(destCount - mStartCount) / duration;
			
			mStartTime = AnimationUtils.currentAnimationTimeMillis();
			
			Logger.debug("mDeltaVal(%f) = (destCount(%d) - startCount(%d)) / duration(%d)",
					mDeltaVal,
					destCount,
					startCount,
					duration);
		}
		
		@Override
		public void run() {
			long time = AnimationUtils.currentAnimationTimeMillis();

			final long elapsedTime = time - mStartTime;

			mCurrCount = mStartCount + Math.round(elapsedTime * mDeltaVal);
/*			Logger.debug("[elapsedTime: %d]: mCurrCount(%d / %d)",
					elapsedTime,
					mCurrCount, 
					mDestCount);
*/			
			if (mCurrCount < mDestCount) {
				updateCountValue();
				
				postDelayed(this, DELTA_INC_DELAY);
			} else {
				finishCount();
			}
		}

	};

	protected Context mContext;
	
	private int mMiniDigits = DEFAULT_MINI_DIGITS;
	private String mCountValueTempl;
	
	private long mDestCount = 0;
	private long mCurrCount = 0;
	
	private CountRunnable mCountRunnable;
	
	private OnCountListener mOnCountListener;
    
	public CountView(Context context) {
        this(context, null);
    }

    public CountView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
		mContext = context;
		
		initMembers();
		
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CountView, defStyle, 0);
        
        int minDigits = a.getInt(R.styleable.CountView_minDigits, 1);
        setMinimumDigits(minDigits);
        
        a.recycle();
    }

	private void initMembers() {
	}

	public void setMinimumDigits(int digits) {
		if (digits < DEFAULT_MINI_DIGITS) {
			return;
		}
		
		mMiniDigits = digits;
		mCountValueTempl = String.format("%%0%dd", mMiniDigits);
		
//		Logger.debug("mCountValueTempl = %s", mCountValueTempl);
		
		updateCountValue();
	}
	
	public int getMinimumDigits() {
		return mMiniDigits;
	}

	public void countTo(long count) {
		count(0, count);
	}

	public void count(long start, long to) {
		count(start, to, 0);
	}

	public void count(long start, long to, long duration) {
		if (mCurrCount != mDestCount) {
			abortCount();
		}

		mDestCount = to;

		if (duration <= 0) {
			duration = DURATION_ESTIMATION_UNIT * (to - start);
		}

		if (duration > MAX_COUNT_DURATION) {
			duration = MAX_COUNT_DURATION;
		}
		
		if (duration <= 0) {
			finishCount();
			
			return;
		}
		
		mCountRunnable =
			new CountRunnable(mDestCount, start, duration);
		
		if (mOnCountListener != null) {
			mOnCountListener.onCountStart(this, mDestCount);
		}
		
		post(mCountRunnable);
	}

	public void abortCount() {
		finishCount(true);
	}

	private void finishCount() {
		finishCount(false);
	}

	private void finishCount(boolean aborted) {
		if (mCountRunnable != null) {
			removeCallbacks(mCountRunnable);
			
			mCountRunnable = null;
		}
		
		mCurrCount = mDestCount;
		
		updateCountValue();
		
		if (mOnCountListener != null) {
			if (aborted) {
				mOnCountListener.onCountAbort(this, mDestCount);
			} else {
				mOnCountListener.onCountFinished(this, mDestCount);
			}
		}
	}
	
	private void updateCountValue() {
		if (mCountValueTempl == null) {
			return;
		}
		
		setText(String.format(mCountValueTempl, mCurrCount));
	}
	
	public void setOnCountListener(OnCountListener l) {
		mOnCountListener = l;
	}
	
}
