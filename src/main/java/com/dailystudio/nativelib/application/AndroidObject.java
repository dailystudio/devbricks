package com.dailystudio.nativelib.application;

import java.lang.reflect.Method;

import com.dailystudio.development.Logger;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;

public abstract class AndroidObject implements IResourceObject {
	
	protected final static int DEFAULT_ICON_DPI = 0;
	
	protected Drawable mIcon;
	protected CharSequence mLabel;
	
	private volatile boolean mResourcesResolved = false;
	
	private int mIconWidth;
	private int mIconHeight;
	
	private boolean mHiResIconRequired = false;
	
	private int mIconDpi;

	public Drawable getIcon() {
		return mIcon;
	}
	
	@Override
	public void setIconDimension(int width, int height) {
		mIconWidth = width;
		mIconHeight = height;
	}

	@Override
	public int getIconWidth() {
		return mIconWidth;
	}
	
	@Override
	public int getIconHeight() {
		return mIconHeight;
	}
	
	public CharSequence getLabel() {
		return mLabel;
	}
	
	@Override
	public void resolveResources(Context context) {
		if (mHiResIconRequired) {
			mIconDpi = resolveIconDpi(context);
		} else {
			mIconDpi = DEFAULT_ICON_DPI;
		}
		
		resolveLabelAndIcon(context);
		
		mResourcesResolved = true;
	}
	
	public int getIconDpi() {
		return mIconDpi;
	}
	
	public boolean isHiResIconRequired() {
		return mHiResIconRequired;
	}
	
	public void setHiResIconRequired(boolean required) {
		mHiResIconRequired = required;
	}
	
	@Override
	public boolean isResourcesResolved() {
		return mResourcesResolved;
	}

	abstract public void resolveLabelAndIcon(Context context);

	public int resolveIconDpi(Context context) {
		if (context == null) {
			return DEFAULT_ICON_DPI;
		}
		
		if (Build.VERSION.SDK_INT < 11) { // Android v3.0+
			return DEFAULT_ICON_DPI;
		}
		
		ActivityManager actmgr =
				(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (actmgr == null) {
			return DEFAULT_ICON_DPI;
		}

		Object object = null;
		try {
			Method method = ActivityManager.class.getMethod("getLauncherLargeIconDensity");
			
			object = method.invoke(actmgr, (Object[]) null);
		} catch (Exception e) {
			Logger.warnning("getLauncherLargeIconDensity() failure: %s", e.toString());
			object = null;
		}
		
		if (object instanceof Integer == false) {
			return DEFAULT_ICON_DPI;
		}
		
		return ((Integer)object).intValue();
	}
	
    public Drawable getFullResIcon(Resources resources, int iconId) {
    	if (resources == null || iconId <= 0) {
    		return null;
    	}
    	
		if (Build.VERSION.SDK_INT < 11) { // Android v3.0+
			return null;
		}
		
		Object object = null;
		try {
			Method method = Resources.class.getMethod("getDrawableForDensity",
					int.class,
					int.class);
			
			object = method.invoke(resources, iconId, mIconDpi);
		} catch (Exception e) {
			Logger.warnning("getDrawableForDensity() failure: %s [resId: %d]",
					e.toString(), iconId);
			
			object = null;
		}
		
		if (object == null) {
			try {
				object = resources.getDrawable(iconId);
			} catch (NotFoundException e) {
				Logger.warnning("getDrawable() failure: %s [resId: %d]",
						e.toString(), iconId);
				
				object = null;
			}
		}
		
		if (object instanceof Drawable == false) {
			return resources.getDrawable(iconId);
		}

        return ((Drawable)object);
    }

    public Drawable getFullResIcon(Context context,
    		String packageName, int iconId) {
    	if (context == null || packageName == null) {
    		return null;
    	}
    	
    	PackageManager pkgmgr = context.getPackageManager();
    	if (pkgmgr == null) {
    		return null;
    	}
    	
    	Resources res = null;
        try {
            res = pkgmgr.getResourcesForApplication(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            res = null;
        }
        
    	if (res == null) {
    		return null;
    	}

    	return getFullResIcon(res, iconId);
    }

    public Drawable getFullResIcon(Context context, ResolveInfo info) {
        return getFullResIcon(context, info.activityInfo);
    }

    public Drawable getFullResIcon(Context context, ActivityInfo info) {
    	if (context == null || info == null) {
    		return null;
    	}
    	
    	PackageManager pkgmgr = context.getPackageManager();
    	if (pkgmgr == null) {
    		return null;
    	}
    	
        Resources res;
        try {
            res = pkgmgr.getResourcesForApplication(
                    info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            res = null;
        }
        
        if (res == null) {
        	return null;
        }
        
        final int iconId = info.getIconResource();

        return getFullResIcon(res, iconId);
    }

}
