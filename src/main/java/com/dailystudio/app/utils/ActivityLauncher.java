package com.dailystudio.app.utils;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

public class ActivityLauncher {
	
	public static interface OnExceptionHandler {
		
		public void onException(Intent intent, Exception e);
		
	}
	
	private static OnExceptionHandler sOnExceptionHandler = new OnExceptionHandler() {
		
		@Override
		public void onException(Intent intent, Exception e) {
			Log.w(ActivityLauncher.class.getSimpleName(), 
					String.format("onException():%s", e));
		}
		
	};
	
	public static void launchActivity(Context context, Intent intent, OnExceptionHandler oec) {
		if (context != null && intent != null) {
			/*
			 * XXX: Application should take care of line below
			 * 		by themselves
			 */
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				if (oec != null) {
					oec.onException(intent, e);
				}
			} catch (SecurityException e) {
				if (oec != null) {
					oec.onException(intent, e);
				}
			}
		}
	}

	public static void launchActivity(Context context, Intent intent) {
		launchActivity(context, intent, sOnExceptionHandler);
	}
	
	public static void launchActivityForResult(Fragment fragment, Intent intent, int requestCode) {
		launchActivityForResult(fragment, intent, requestCode, sOnExceptionHandler);
	}
	
	public static void launchActivityForResult(Fragment fragment, Intent intent, int requestCode, OnExceptionHandler oec) {
		if (fragment != null && intent != null) {
			try {
				fragment.startActivityForResult(intent, requestCode);
			} catch (ActivityNotFoundException e) {
				if (oec != null) {
					oec.onException(intent, e);
				}
			} catch (SecurityException e) {
				if (oec != null) {
					oec.onException(intent, e);
				}
			}
		}
	}

	public static void launchActivityForResult(Activity activity, Intent intent, int requestCode, OnExceptionHandler oec) {
		if (activity != null && intent != null) {
			try {
				activity.startActivityForResult(intent, requestCode);
			} catch (ActivityNotFoundException e) {
				if (oec != null) {
					oec.onException(intent, e);
				}
			} catch (SecurityException e) {
				if (oec != null) {
					oec.onException(intent, e);
				}
			}
		}
	}
	
	public static void launchActivityForResult(Activity activity, Intent intent, int requestCode) {
		launchActivityForResult(activity, intent, requestCode, sOnExceptionHandler);
	}

	public static boolean isActivityLaunched(Context context, Context appContext, Class<?> cls) {
		return isActivityLaunched(context, new ComponentName(appContext, cls));
	}
	
	public static boolean isActivityLaunched(Context context, String pkg, String cls) {
		return isActivityLaunched(context, new ComponentName(pkg, cls));
	}
	
	public static boolean isActivityLaunched(Context context, ComponentName activityComp) {
		if (context == null || activityComp == null) {
			return false;
		}

		final ComponentName topActivity = getTopActivity(context);
		
		return (activityComp.compareTo(topActivity) == 0);
	}
	
	public static ComponentName getTopActivity(Context context) {
		if (context == null) {
			return null;
		}
		
		ActivityManager actmgr = 
				(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		
		List<RunningTaskInfo> taskInfos = actmgr.getRunningTasks(1);
		if (taskInfos == null || taskInfos.size() <= 0) {
			return null;
		}
		
		return taskInfos.get(0).topActivity;
	}
	
}
