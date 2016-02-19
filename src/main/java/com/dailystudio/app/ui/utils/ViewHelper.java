package com.dailystudio.app.ui.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;

import com.dailystudio.development.Logger;

import java.lang.reflect.Method;

public class ViewHelper {
	
	private final static int SCREENLAYOUT_SIZE_XLARGE = 4;
	
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
				Logger.warnning("setLayerType() failure: %s", e.toString());
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

}
