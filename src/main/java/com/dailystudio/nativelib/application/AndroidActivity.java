package com.dailystudio.nativelib.application;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.development.Logger;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

public class AndroidActivity extends AndroidComponentObject {

	public AndroidActivity(ResolveInfo rInfo) {
		super(rInfo);
	}

	public AndroidActivity(ComponentName compName) {
		super(compName);
	}
	
	@Override
	protected ComponentName extractComponent(ResolveInfo rInfo) {
		ComponentName comp = null;
		
		final ActivityInfo activityInfo = rInfo.activityInfo;
		if (activityInfo != null) {
			comp = new ComponentName(
					activityInfo.packageName, activityInfo.name);
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
			ActivityInfo aInfo = pkgmgr.getActivityInfo(comp, 0);
			if (aInfo == null) {
				return;
			}
			
			Drawable d = null;
			if (isHiResIconRequired()
					&& getIconDpi() != DEFAULT_ICON_DPI) {
				d = getFullResIcon(context, aInfo);
			} else {
				d = aInfo.loadIcon(pkgmgr);
			}
			
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
			mLabel = aInfo.loadLabel(pkgmgr);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void launch(Context context) {
		if (context == null) {
			Logger.warnning("NULL context");
			
			return;
		}
		
		final ComponentName comp = getComponentName();
		if (comp == null) {
			Logger.warnning("NULL component");
			
			return;
		}
		
		Intent launchIntent = new Intent(Intent.ACTION_MAIN);
		launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		launchIntent.setComponent(comp);
		launchIntent.setFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		
		ActivityLauncher.launchActivity(context, launchIntent);
	}
	
	public final static List<AndroidActivity> queryActivities(Context context) {
		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		return queryActivities(context, mainIntent);
	}
	
	public final static List<AndroidActivity> queryActivities(Context context, Intent queryIntent) {
		if (context == null || queryIntent == null) {
			return null;
		}
		
        final PackageManager packageManager = context.getPackageManager();
        
        List<ResolveInfo> rInfos = packageManager.queryIntentActivities(queryIntent, 0);
        if (rInfos == null) {
        	return null;
        }
        
        final int N = rInfos.size();
        if (N <= 0) {
        	return null;
        }

        List<AndroidActivity> activities = new ArrayList<AndroidActivity>(N);
        
        ResolveInfo rInfo = null;
        for (int i = 0; i < N; i++) {
        	rInfo = rInfos.get(i);
        	
        	activities.add(new AndroidActivity(rInfo));
        }
        
        return activities;
	}

	public static ComponentName getTopActivity(Context context) {
		return getTopActivityAboveL(context);
	}
	
	public static ComponentName getTopActivityAboveL(Context context) {
		if (context == null) {
			return null;
		}

		ActivityManager actmgr = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		if (actmgr == null) {
			return null;
		}

		List<RunningTaskInfo> tasks = actmgr.getRunningTasks(1);
		if (tasks == null || tasks.size() <= 0) {
			return null;
		}

		List<RunningAppProcessInfo> processes = actmgr.getRunningAppProcesses();
		if (processes == null) {
			return null;
		}

		RunningAppProcessInfo p0 = processes.get(0);
		if (p0 == null) {
			return null;
		}

		String pkg = p0.processName;

		String[] pkglist = p0.pkgList;
		if (pkglist != null && pkglist.length > 0) {
			pkg = pkglist[0];
		}

		Logger.debug("top app-pkg: %s", pkg);
		if (TextUtils.isEmpty(pkg)) {
			return null;
		}

		AndroidApplication app = new AndroidApplication(pkg);

		Intent i = app.getLaunchIntent(context);
		if (i == null) {
			return null;
		}

		final ComponentName comp = i.getComponent();
		Logger.debug("top app-comp: %s", comp);

		return comp;
	}

}
