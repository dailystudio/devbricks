
package com.dailystudio.app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.dailystudio.R;
import com.dailystudio.app.ui.utils.ColorHelper;
import com.dailystudio.development.Logger;

public class GifImageView extends View {

    private int mMovieResourceId;
    private Movie mMovie;

    private long mTimeStart;
    private int mTimeOffset = 0;
    private int mLastOffset = 0;

    private float mDrawingFactor;

    private boolean mPaused = false;

    private boolean mInfinite = true;

    private boolean mFillCanvas = false;

    private int mTintColor;
    private boolean mHasTintColor = false;
    private Paint mTintPaint;

    public GifImageView(Context context) {
        this(context, null);
    }

    public GifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setViewAttributes(context, attrs, defStyle);
    }

    @SuppressLint("NewApi")
    private void setViewAttributes(Context context, AttributeSet attrs,
            int defStyle) {

        /**
         * Starting from HONEYCOMB have to turn off HW acceleration to draw
         * Movie on Canvas.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        final TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.GifMovieView, defStyle,
                0);

        if (array.hasValue(R.styleable.GifMovieView_tintColor)) {
            mTintColor = array.getColor(R.styleable.GifMovieView_tintColor,
                    ColorHelper.getColorResource(getContext(), R.color.android_blue));
            mHasTintColor = true;
        }

        mMovieResourceId = array.getResourceId(R.styleable.GifMovieView_gif,
                -1);
        mPaused = array.getBoolean(R.styleable.GifMovieView_paused, false);
        mFillCanvas = array.getBoolean(R.styleable.GifMovieView_fill, false);

        array.recycle();

        if (mMovieResourceId != -1) {
            mMovie = Movie.decodeStream(getResources().openRawResource(
                    mMovieResourceId));
        }

        if (mHasTintColor) {
            mTintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            ColorFilter filter = new PorterDuffColorFilter(
                    mTintColor,
                    PorterDuff.Mode.SRC_ATOP);

            mTintPaint.setColorFilter(filter);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMovie == null) {
            setMeasuredDimension(getSuggestedMinimumWidth(),
                    getSuggestedMinimumHeight());

            return;
        }

        final int width = mMovie.width();
        final int height = mMovie.height();

        float scaleW = 1f;
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED) {
            final int maximumWidth = MeasureSpec.getSize(widthMeasureSpec);
            if (width > maximumWidth || mFillCanvas) {
                scaleW = (float) width / (float) maximumWidth;
            }
        }

        float scaleH = 1f;
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED) {
            final int maximumHeight = MeasureSpec.getSize(heightMeasureSpec);
            if (height > maximumHeight || mFillCanvas) {
                scaleH = (float) height / (float) maximumHeight;
            }
        }

        mDrawingFactor = 1f / Math.max(scaleW, scaleH);

        int measuredWidth = (int) (width * mDrawingFactor);
        int measuredHeight = (int) (height * mDrawingFactor);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie == null) {
            return;
        }

        syncGifAnimation();
        drawFrame(canvas);

        if (!isPaused()) {
            mHandler.post(mSyncAnimRunnable);
        }
    }

    private void drawFrame(Canvas canvas) {
        if (mMovie == null) {
            return;
        }

        final int xOffset = (int)(((getWidth() - getMeasuredWidth()) / 2f)
                / mDrawingFactor);
        final int yOffset = (int)(((getHeight() - getMeasuredHeight()) / 2f)
                / mDrawingFactor);

        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        canvas.scale(mDrawingFactor, mDrawingFactor);

        if (mHasTintColor) {
            mMovie.draw(canvas, xOffset, yOffset, mTintPaint);
        } else {
            mMovie.draw(canvas, xOffset, yOffset);
        }

        canvas.restore();
    }

    @SuppressLint("NewApi")
    private void invalidateView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postInvalidateOnAnimation();
        } else {
            invalidate();
        }
    }

    private void syncGifAnimation() {
        if (mMovie == null) {
            return;
        }

        final long now = SystemClock.uptimeMillis();

        if (mTimeStart == 0) {
            mTimeStart = now;
        }

        int duration = mMovie.duration();
        if (duration <= 0) {
            pause();

            return;
        }

        mTimeOffset = (int) (now - mTimeStart);

        if (mTimeOffset >= duration) {
            if (isInfinite()) {
                mLastOffset = 0;
                mTimeOffset = 0;
                mTimeStart = now;
            } else {
                mTimeOffset = duration - 1;

                pause();
            }
        } else {
            mLastOffset = mTimeOffset;
        }

        mMovie.setTime(mTimeOffset);
//        Logger.debug("sync anim time to: %d(%s)",
//                mTimeOffset,
//                CalendarUtils.durationToReadableString(mTimeOffset));
    }

    public void setMovie(Movie movie) {
        mMovie = movie;
        requestLayout();
    }

    public void pause() {
        if (isPaused()) {
            return;
        }

        Logger.debug("pause gif: %s", this);

        mPaused = true;
        mHandler.removeCallbacks(mSyncAnimRunnable);
    }

    public void resume() {
        if (!isPaused()) {
            return;
        }

        Logger.debug("resume gif: %s", this);

        mTimeStart = SystemClock.uptimeMillis() - mTimeOffset;
        mPaused = false;

        mHandler.post(mSyncAnimRunnable);
    }

    public boolean isPaused() {
        return mPaused;
    }

    public void setInfinite(boolean enabled) {
        mInfinite = true;
    }

    public boolean isInfinite() {
        return mInfinite;
    }

    public void setFillCanvas(boolean fill) {
        mFillCanvas = fill;

        requestLayout();
    }

    public boolean isFillCanvas() {
        return mFillCanvas;
    }

    private Runnable mSyncAnimRunnable = new Runnable() {

        @Override
        public void run() {
            syncGifAnimation();
            invalidateView();
        }

    };

    private Handler mHandler = new Handler();

}
