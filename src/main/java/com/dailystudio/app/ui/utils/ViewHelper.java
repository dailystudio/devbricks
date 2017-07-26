package com.dailystudio.app.ui.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.dailystudio.development.Logger;

import java.lang.reflect.Method;

public class ViewHelper {
	
	private final static int SCREENLAYOUT_SIZE_XLARGE = 4;

	private static int[] sTempPos = new int[2];

	public synchronized static Rect getBoundsOnScreen(View view) {
		if (view == null) {
			return new Rect(0, 0, 0, 0);
		}

		view.getLocationOnScreen(sTempPos);

		Rect rect = new Rect();
		rect.set(view.getLeft(), view.getTop(),
				view.getRight(), view.getBottom());
		rect.offset(sTempPos[0] - view.getLeft(), sTempPos[1] - view.getTop());

		return rect;
	}

	public static void disableHardwareAccelerated(View view) {
		if (view == null) {
			return;
		}
		
		if (Build.VERSION.SDK_INT >= 11) {
			try {
				Method method = View.class.getMethod("setLayerType", 
						int.class,
						Paint.class);
				method.invoke(view, 1, new Paint()); 
			} catch (Exception e) {
				Logger.warn("setLayerType() failure: %s", e.toString());
			}
		}
	}
	
	public static boolean isLargeScreen(Context context) {
		if (context == null) {
			return false;
		}
		
		final Resources res = context.getResources();
		if (res == null) {
			return false;
		}
		
		final Configuration config = res.getConfiguration();
		if (config == null) {
			return false;
		}
		
        final int screenSize = 
        	(config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        
        return ((screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE)
        		|| (screenSize == SCREENLAYOUT_SIZE_XLARGE));
	}

	public static int dpToPixel(Context context, int dp) {
		if (context == null) {
			return dp;
		}

		final Resources res = context.getResources();
		if (res == null) {
			return dp;
		}

		DisplayMetrics displayMetrics = res.getDisplayMetrics();
		if (displayMetrics == null) {
			return dp;
		}

		return (int)((dp * displayMetrics.density) + 0.5);
	}

	public static int pixelToDp(Context context, int px) {
		if (context == null) {
			return px;
		}

		final Resources res = context.getResources();
		if (res == null) {
			return px;
		}

		DisplayMetrics displayMetrics = res.getDisplayMetrics();
		if (displayMetrics == null) {
			return px;
		}

		return (int) ((px / displayMetrics.density) + 0.5);
	}

}
