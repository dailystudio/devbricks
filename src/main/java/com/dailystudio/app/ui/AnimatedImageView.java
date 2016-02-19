package com.dailystudio.app.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AnimatedImageView extends ImageView {
	
	private class AnimStartRunnable implements Runnable {

		private AnimationDrawable mAnimDrawable;
		
		private AnimStartRunnable(AnimationDrawable d) {
			mAnimDrawable = d;
		}
		
		@Override
		public void run() {
			if (mAnimDrawable != null) {
				mAnimDrawable.start();
			}
		}
		
	}
	
	private class AnimStopRunnable implements Runnable {

		private AnimationDrawable mAnimDrawable;
		
		private AnimStopRunnable(AnimationDrawable d) {
			mAnimDrawable = d;
		}
		
		@Override
		public void run() {
			if (mAnimDrawable != null) {
				mAnimDrawable.stop();
			}
		}
		
	}
	
	protected Context mContext;
	
	public AnimatedImageView(Context context) {
        this(context, null);
    }

    public AnimatedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
		mContext = context;
		
		initMembers();
    }

	private void initMembers() {
		playAnimation();
	}
	
	public void playAnimation() {
		Drawable d = getDrawable();
		if (d instanceof AnimationDrawable) {
			d.setCallback(this);
			
			post(new AnimStartRunnable((AnimationDrawable)d));
		}
	}
	
	public void stopAnimation() {
		Drawable d = getDrawable();
		if (d instanceof AnimationDrawable) {
			post(new AnimStopRunnable((AnimationDrawable)d));
		}
	}
	
}
