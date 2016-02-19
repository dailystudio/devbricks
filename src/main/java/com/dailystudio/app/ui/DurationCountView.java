package com.dailystudio.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dailystudio.R;
import com.dailystudio.development.Logger;
import com.dailystudio.app.ui.CountView.OnCountListener;
import com.dailystudio.app.ui.utils.ColorHelper;
import com.dailystudio.app.ui.utils.DateTimePrintUtils;

public class DurationCountView extends FrameLayout {
	
	public static interface OnCountDurationListener {
		
		public void onCountDurationStart(DurationCountView dv, long duration);
		public void onCountDurationFinished(DurationCountView dv, long duration);
		
	}

	private final static int COUNT_DAY_INDEX = 0;
	private final static int COUNT_HOUR_INDEX = 1;
	private final static int COUNT_MIN_INDEX = 2;
	private final static int COUNT_SEC_INDEX = 3;
	
	private final static int COUNT_VIEW_NUM = COUNT_SEC_INDEX + 1;

	private CountView[] mCountViews = new CountView[COUNT_VIEW_NUM];
	private TextView[] mCountLabels = new TextView[COUNT_VIEW_NUM];
	private long[] mCountValues = new long[COUNT_VIEW_NUM];

	protected Context mContext;
	
	private long mDuration;
	
	private OnCountDurationListener mOnCountDurationListener;
	
    public DurationCountView(Context context) {
        this(context, null);
    }

    public DurationCountView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DurationCountView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
		mContext = context;
		
		initMembers();
		
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.DurationCountView, defStyle, 0);
        
        int textColor = a.getColor(R.styleable.DurationCountView_textColor,
        		ColorHelper.getColorResource(mContext, R.color.black));
        setTextColor(textColor);

        float textSize = a.getDimensionPixelSize(R.styleable.DurationCountView_textSize, 0);
        if (textSize != 0) {
        	setTextSize(textSize);
        }

        int textStyle = a.getInt(R.styleable.DurationCountView_textStyle, 0);
        setTextStyle(textStyle);
        
        a.recycle();
    }

	private void initMembers() {
		LayoutInflater.from(mContext).inflate(R.layout.layout_duration_count_view, this);

		mCountViews[COUNT_DAY_INDEX] = (CountView) findViewById(R.id.stats_lifetime_day);
		mCountViews[COUNT_HOUR_INDEX] = (CountView) findViewById(R.id.stats_lifetime_hour);
		mCountViews[COUNT_MIN_INDEX] = (CountView) findViewById(R.id.stats_lifetime_min);
		mCountViews[COUNT_SEC_INDEX] = (CountView) findViewById(R.id.stats_lifetime_sec);
		
		mCountLabels[COUNT_DAY_INDEX] = (TextView) findViewById(R.id.stats_lifetime_day_label);
		mCountLabels[COUNT_HOUR_INDEX] = (TextView) findViewById(R.id.stats_lifetime_hour_label);
		mCountLabels[COUNT_MIN_INDEX] = (TextView) findViewById(R.id.stats_lifetime_min_label);
		mCountLabels[COUNT_SEC_INDEX] = (TextView) findViewById(R.id.stats_lifetime_sec_label);
		
		bindCountListener();
	}
	
	private void bindCountListener() {
		final int N = mCountViews.length;
		
		int i;
		CountView cv;
		
		for (i = 0; i < N; i++) {
			cv = mCountViews[i];
			if (cv == null) {
				continue;
			}
			
			cv.setTag(Integer.valueOf(i));
			cv.setOnCountListener(mOnCountListener);
		}
	}

	public void setTextStyle(int style) {
		Typeface tf = null;
		
		for (CountView cv: mCountViews) {
			if (cv == null) {
				continue;
			}
			
			tf = cv.getTypeface();
			
			cv.setTypeface(tf, style);
		}
		
		for (TextView lv: mCountLabels) {
			if (lv == null) {
				continue;
			}
			
			tf = lv.getTypeface();
			
			lv.setTypeface(tf, style);
		}
	}

	public void setTextColor(int color) {
		for (CountView cv: mCountViews) {
			if (cv == null) {
				continue;
			}
			
			cv.setTextColor(color);
		}
		
		for (TextView lv: mCountLabels) {
			if (lv == null) {
				continue;
			}
			
			lv.setTextColor(color);
		}
	}
	
	public void setTextSize (float size) {
		for (CountView cv: mCountViews) {
			if (cv == null) {
				continue;
			}
			
			cv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		}
		
		for (TextView lv: mCountLabels) {
			if (lv == null) {
				continue;
			}
			
			lv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		}
	}

	public void setDuration(long duration) {
		final long values[] = DateTimePrintUtils.durationPrintValues(duration);
		if (values == null || values.length < 5) {
			return;
		}
		
		mDuration = duration;
		
		final int N = mCountViews.length;
		
		CountView cv = null;
		for (int i = 0; i < N; i++) {
			cv = mCountViews[i];
			if (cv == null) {
				continue;
			}
			
			mCountValues[i] = values[i];
		}
		
		countValue(COUNT_SEC_INDEX);
		if (mOnCountDurationListener != null) {
			mOnCountDurationListener.onCountDurationStart(this, mDuration);
		}
	}
	
	public void setOnCountDurationListener(OnCountDurationListener l) {
		mOnCountDurationListener = l;
	}
	
	private void countValue(int cvIndex) {
		Logger.debug("cvIndex = %d", cvIndex);
		if (cvIndex < 0 || cvIndex >= COUNT_VIEW_NUM) {
			return;
		}
		
		if (mCountViews[cvIndex] != null) {
			Logger.debug("Part[%d] count to [%d]", 
					cvIndex,
					mCountValues[cvIndex]);
			
			mCountViews[cvIndex].countTo(mCountValues[cvIndex]);
		}
	}

	private OnCountListener mOnCountListener = new OnCountListener() {
		
		@Override
		public void onCountStart(CountView cv, long destCount) {
		}
		
		@Override
		public void onCountFinished(CountView cv, long destCount) {
			shiftToNextCount(cv);
		}
		
		@Override
		public void onCountAbort(CountView cv, long destCount) {
			shiftToNextCount(cv);
		}
		
		private void shiftToNextCount(CountView cv) {
			if (cv == null) {
				if (mOnCountDurationListener != null) {
					mOnCountDurationListener.onCountDurationFinished(
							DurationCountView.this, mDuration);
				}

				return;
			}
			
			Object tag = cv.getTag();
			if (tag instanceof Integer == false) {
				if (mOnCountDurationListener != null) {
					mOnCountDurationListener.onCountDurationFinished(
							DurationCountView.this, mDuration);
				}

				return;
			}
			
			final int nextIndex = ((Integer)tag).intValue() - 1;
			if (nextIndex < 0) {
				if (mOnCountDurationListener != null) {
					mOnCountDurationListener.onCountDurationFinished(
							DurationCountView.this, mDuration);
				}

				return;
			}

			countValue(nextIndex);
		}
		
	};
	

}
