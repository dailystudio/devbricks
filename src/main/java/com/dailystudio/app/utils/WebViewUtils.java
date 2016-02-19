package com.dailystudio.app.utils;

import java.lang.reflect.Method;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;
import android.webkit.WebView;

import com.dailystudio.development.Logger;

public class WebViewUtils {
	
	public static void setBackgroundToTransparent(WebView webView) {
		if (webView == null) {
			return;
		}
		
		webView.setBackgroundColor(Color.TRANSPARENT);
		webView.setBackgroundDrawable(null);
//		Logger.debug("Build.VERSION.SDK_INT = %d", Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT >= 11) { // Android v3.0+
			try {
				Method method = View.class.getMethod("setLayerType", 
						int.class,
						Paint.class);
				method.invoke(webView, 1, new Paint()); // 1 = LAYER_TYPE_SOFTWARE
														// (API11)
			} catch (Exception e) {
				Logger.warnning("setLayerType() failure: %s", e.toString());
			}
		}
	}

}
