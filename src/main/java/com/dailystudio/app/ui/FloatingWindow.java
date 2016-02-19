package com.dailystudio.app.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public abstract class FloatingWindow extends AbstractWindow {

	private WindowManager.LayoutParams mLayoutParams;
	
	private boolean mIsShown;

	public FloatingWindow(Context context) {
		this(context, null);
	}
	
	public FloatingWindow(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FloatingWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		initMembers();
	}
	
	private void initMembers() {
		mLayoutParams = generateWindowLayoutParameter();
	}
	
	protected WindowManager.LayoutParams generateWindowLayoutParameter() {
		WindowManager.LayoutParams lp = 
			new WindowManager.LayoutParams();
		
		lp.type = (WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
		lp.format = PixelFormat.TRANSPARENT;

		lp.gravity = Gravity.CENTER;  
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		
		lp.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		
		lp.x = 0;
		lp.y = 0;
		
		return lp;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return false;
	}
	
	public boolean isShown() {
		return mIsShown;
	}
	
	public WindowManager.LayoutParams getWindowLayoutParams() {
		return mLayoutParams;
	}
	
	public void updateLayoutPosition(int x, int y) {
		mLayoutParams.x = x;
		mLayoutParams.y = y;
		
		if (mIsShown == false) {
			openWindow();
			return;
		}
		
		WindowManager winmgr = 
			(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		if (winmgr == null) {
			return;
		}
		
		winmgr.updateViewLayout(this, mLayoutParams);
	}

	public void updateLayoutParams(WindowManager.LayoutParams lp) {
		if (lp == null) {
			return;
		}
		
		mLayoutParams = lp;
		
		if (mIsShown == false) {
			openWindow();
			return;
		}
		
		WindowManager winmgr = 
			(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		if (winmgr == null) {
			return;
		}
		
		winmgr.updateViewLayout(this, mLayoutParams);
	}
	

	@Override
	public void onOpenWindow() {
		displayWindow();

		View child = null; 
		if (getChildCount() > 0) {
			child = getChildAt(0);
		}
		
		final Animation animIn = getWindowOpenAnimation();
		if (animIn != null && child != null) {
			child.startAnimation(animIn);
		}
	}
	
	private void displayWindow() {
		if (mIsShown) {
			return;
		}
		
		if (mLayoutParams == null) {
			return;
		}
		
		WindowManager winmgr = 
			(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		if (winmgr == null) {
			return;
		}
		
		winmgr.addView(this, mLayoutParams);
		
		mIsShown = true;
	}
	
	public void onCloseWindow() {
		View child = null; 
		if (getChildCount() > 0) {
			child = getChildAt(0);
		}
		
		final Animation animOut = getWindowCloseAnimation();
		if (animOut != null && child != null) {
			animOut.setAnimationListener(mOutAnimationListener);
			
			child.startAnimation(animOut);
			
			return;
		}
		
		hideWindow();
	}
	
	public void hideWindow() {
		if (mIsShown == false) {
			return;
		}
		
		WindowManager winmgr = 
			(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		if (winmgr == null) {
			return;
		}
		
		winmgr.removeView(this);
		
		mIsShown = false;
	}
	
	protected Animation getWindowOpenAnimation() {
		return null;
	}

	protected Animation getWindowCloseAnimation() {
		return null;
	}
	
	private AnimationListener mOutAnimationListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			if (animation != null) {
				animation.setAnimationListener(null);
			}
			
			hideWindow();
		}
		
	};

}