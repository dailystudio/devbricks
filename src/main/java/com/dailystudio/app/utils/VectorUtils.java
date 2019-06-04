package com.dailystudio.app.utils;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class VectorUtils {

    public static RectF getTruncatedBounds(Path path) {
        RectF fBounds = new RectF();
        path.computeBounds(fBounds, true);

        Rect realRegion = new Rect(
                (int)Math.floor(fBounds.left),
                (int)Math.floor(fBounds.top),
                (int)Math.floor(fBounds.right),
                (int)Math.floor(fBounds.bottom));


        Rect bounds = new Rect();
        Region region = new Region();
        region.setPath(path, new Region(realRegion));
        region.getBounds(bounds);

        return new RectF(bounds);
    }

}
