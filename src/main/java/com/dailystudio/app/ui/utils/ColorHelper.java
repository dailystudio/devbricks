package com.dailystudio.app.ui.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;

public class ColorHelper {
	
	public static int getColorResource(Context context, int resId) {
		if (context == null) {
			return 0;
		}
		
		final Resources res = context.getResources();
		if (res == null) {
			return 0;
		}
		
		return res.getColor(resId);
	}

    public static ColorMatrixColorFilter getTintFilter(int color) {
        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;

        ColorMatrix cm = new ColorMatrix(new float[] {
                // Change red channel
                r, 0, 0, 0, 0,
                // Change green channel
                0, g, 0, 0, 0,
                // Change blue channel
                0, 0, b, 0, 0,
                // Keep alpha channel
                0, 0, 0, 1, 0,
        });

        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);

        return cf;
    }

    public static void fillColor(Drawable src, int color) {
        if (src == null) {
            return;
        }

        ColorMatrixColorFilter cf = getTintFilter(color);

        src.setColorFilter(cf);
    }

    public static void greyColor(Drawable src) {
        if (src == null) {
            return;
        }

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);

        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        src.setColorFilter(f);
    }

}
