package com.dailystudio.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;

public abstract class FloatingSystemWindow extends FloatingWindow {

	public FloatingSystemWindow(Context context) {
		this(context, null);
	}

	public FloatingSystemWindow(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FloatingSystemWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected WindowManager.LayoutParams generateWindowLayoutParameter() {
		WindowManager.LayoutParams lp = super.generateWindowLayoutParameter();

		lp.type = (WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);

		return lp;
	}

}
