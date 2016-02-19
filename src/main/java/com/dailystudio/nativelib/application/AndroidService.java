package com.dailystudio.nativelib.application;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class AndroidService extends AndroidComponentObject {

	public AndroidService(ResolveInfo rInfo) {
		super(rInfo);
	}
	
	public AndroidService(ComponentName compName) {
		super(compName);
	}
	
	@Override
	protected ComponentName extractComponent(ResolveInfo rInfo) {
		ComponentName comp = null;
		
		final ServiceInfo serviceInfo = rInfo.serviceInfo;
		if (serviceInfo != null) {
			comp = new ComponentName(
					serviceInfo.packageName, serviceInfo.name);
		}

		return comp;
	}

	@Override
	public void resolveLabelAndIcon(Context context) {
		if (context == null) {
			return;
		}
		
		final ComponentName comp = getComponentName();
		if (comp == null) {
			return;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return;
		}
		
		try {
			ServiceInfo sInfo = pkgmgr.getServiceInfo(comp, 0);
			if (sInfo == null) {
				return;
			}
			
			Drawable d = sInfo.loadIcon(pkgmgr);
			
			final int iw = getIconWidth();
			final int ih = getIconHeight();
			
			if (iw > 0 && ih > 0) {
				if (d != null) {
					final Bitmap bitmap = 
						ResourceUtilities.createIconBitmap(d, context, 
								iw, ih);
					if (bitmap != null) {
						d = new BitmapDrawable(context.getResources(),
								bitmap);
					}
				}
			}
			
			mIcon = d;
			mLabel = sInfo.loadLabel(pkgmgr);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

}
