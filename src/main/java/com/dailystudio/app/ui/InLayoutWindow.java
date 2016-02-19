package com.dailystudio.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;

public abstract class InLayoutWindow extends AbstractWindow {
	
	public InLayoutWindow(Context context) {
		this(context, null);
	}
	
	public InLayoutWindow(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public InLayoutWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		initMembers();
	}
	
	private void initMembers() {
		setVisibility(GONE);
	}
	
	@Override
	public boolean isShown() {
		return (getVisibility() == VISIBLE);
	}
	
	@Override
	protected void onOpenWindow() {
		setVisibility(VISIBLE);
		final Animation animIn = getWindowOpenAnimation();
		if (animIn != null) {
			startAnimation(animIn);
		}
	}
	
	@Override
	protected void onCloseWindow() {
		final Animation animOut = getWindowCloseAnimation();
		if (animOut != null) {
			startAnimation(animOut);
		}
		
		setVisibility(GONE);
	}

}