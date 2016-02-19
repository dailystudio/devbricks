package com.dailystudio.nativelib.application;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dailystudio.app.utils.ActivityLauncher;
import com.dailystudio.development.Logger;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

public class AndroidApplication extends AndroidObject {

	public static class AndroidApplicationComparator
	    extends ResourceObjectComparator<AndroidApplication> {

		@Override
		public int compare(AndroidApplication app1, AndroidApplication app2) {
			int ret = 0;
			long lret = 0;
			
			final int fsys1 = (app1.mFlags & ApplicationInfo.FLAG_SYSTEM); 
			final int fsys2 = (app2.mFlags & ApplicationInfo.FLAG_SYSTEM); 
			ret = fsys1 - fsys2;
			if (ret != 0) {
				return ret;
			}
			
			final int fpersis1 = (app1.mFlags & ApplicationInfo.FLAG_PERSISTENT); 
			final int fpersis2 = (app2.mFlags & ApplicationInfo.FLAG_PERSISTENT); 
			ret = fpersis1 - fpersis2;
			if (ret != 0) {
				return ret;
			}
			
/*			final int fsd1 = (app1.mFlags & ApplicationInfo.FLAG_EXTERNAL_STORAGE); 
			final int fsd2 = (app2.mFlags & ApplicationInfo.FLAG_EXTERNAL_STORAGE); 
			ret = fsd1 - fsd2;
			if (ret != 0) {
				return ret;
			}
*/			
			final long updateTime1 = app1.getLastUpdateTime();
			final long updateTime2 = app2.getLastUpdateTime();
			lret = updateTime1 - updateTime2;
			if (lret != 0) {
				return (lret > 0 ? -1: 1);
			}
			
			final long installTime1 = app1.getFirstInstallTime();
			final long installTime2 = app2.getFirstInstallTime();
			lret = installTime1 - installTime2;
			if (lret != 0) {
				return (lret > 0 ? -1: 1);
			}
			
			return super.compare(app1, app2);
		}
		
	}	

	private String mPackageName = null;
	private String mPackagePath = null;
	
	private int mFlags = 0;
	private long mFirstInstallTime = 0l;
	private long mLastUpdateTime = 0l;
	
	private String mAppVerName;
	private int mAppVerCode = 0;
	
	
	public AndroidApplication(PackageInfo pInfo) {
        fillInfo(pInfo);
	}
	
	public AndroidApplication(String packageName) {
		mPackageName = packageName;
	}

    private void fillInfo(PackageInfo pInfo) {
        if (pInfo != null) {
            mPackageName = pInfo.packageName;
            mPackagePath = pInfo.applicationInfo.sourceDir;

            mFirstInstallTime = getFirstInstallTime(pInfo);
            mLastUpdateTime = getLastUpdateTime(pInfo);

            if (pInfo.applicationInfo != null) {
                mFlags = pInfo.applicationInfo.flags;
            }

            mAppVerCode = pInfo.versionCode;
            mAppVerName = pInfo.versionName;
        }
    }

    public void queryAndFillInfo(Context context) {
        if (context == null || TextUtils.isEmpty(mPackageName)) {
            return;
        }

        final PackageManager pkgmgr = context.getPackageManager();
        if (pkgmgr == null) {
            return;
        }

        PackageInfo pInfo = null;
        try {
            pInfo = pkgmgr.getPackageInfo(mPackageName, 0);
        } catch (NameNotFoundException e) {
            Logger.warnning("Could not get package info for [pkg: %s]: %s",
                    mPackageName, e.toString());
            pInfo = null;
        }

        if (pInfo == null) {
            return;
        }

        fillInfo(pInfo);
    }
	
	private long getFirstInstallTime(PackageInfo pInfo) {
		// API level 9 and above have the "firstInstallTime" field.
		// Check for it with reflection and return if present. 
		long firstIntallTime = 0;
		
		try {
			Field field = PackageInfo.class.getField("firstInstallTime");
			
			firstIntallTime = field.getLong(pInfo);
		} catch (IllegalAccessException e) {
			Logger.warnning("fetch firstIntallTime failure: %s", e.toString());

			firstIntallTime = 0l; 
		} catch (NoSuchFieldException e) {
			Logger.warnning("fetch firstIntallTime failure: %s", e.toString());

			firstIntallTime = 0l; 
		} catch (IllegalArgumentException e) {
			Logger.warnning("fetch firstIntallTime failure: %s", e.toString());

			firstIntallTime = 0l; 
		} catch (SecurityException e) {
			Logger.warnning("fetch firstIntallTime failure: %s", e.toString());

			firstIntallTime = 0l; 
		}
	
		return firstIntallTime;
	}
	
	private long getLastUpdateTime(PackageInfo pInfo) {
		// API level 9 and above have the "lastUpdatedTime" field.
		// Check for it with reflection and return if present. 
		long lastUpdatedTime = 0;
		
		try {
			Field field = PackageInfo.class.getField("lastUpdateTime");
			
			lastUpdatedTime = field.getLong(pInfo);
		} catch (IllegalAccessException e) {
			Logger.warnning("fetch lastUpdatedTime failure: %s", e.toString());

			lastUpdatedTime = 0l; 
		} catch (NoSuchFieldException e) {
			Logger.warnning("fetch lastUpdatedTime failure: %s", e.toString());

			lastUpdatedTime = 0l; 
		} catch (IllegalArgumentException e) {
			Logger.warnning("fetch lastUpdatedTime failure: %s", e.toString());

			lastUpdatedTime = 0l; 
		} catch (SecurityException e) {
			Logger.warnning("fetch lastUpdatedTime failure: %s", e.toString());

			lastUpdatedTime = 0l; 
		}
	
		return lastUpdatedTime;
	}

	public String getPackageName() {
		return mPackageName;
	}
	
	public String getPackagePath() {
		return mPackagePath;
	}
	
	public long getFirstInstallTime() {
		return mFirstInstallTime;
	}
	
	public long getLastUpdateTime() {
		return mLastUpdateTime;
	}
	
	public void setFlags(int flags) {
		mFlags = flags;
	}
	
	public int getFlags() {
		return mFlags;
	}
	
	public boolean isSystem() {
		return ((mFlags & ApplicationInfo.FLAG_SYSTEM) 
				== ApplicationInfo.FLAG_SYSTEM);
	}
	
	public boolean isOnSdCard() {
		return ((mFlags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) 
				== ApplicationInfo.FLAG_EXTERNAL_STORAGE);
	}
	
	public boolean isDebuggable() {
		return ((mFlags & ApplicationInfo.FLAG_DEBUGGABLE) 
				== ApplicationInfo.FLAG_DEBUGGABLE);
	}
	
	public boolean isPersistent() {
		return ((mFlags & ApplicationInfo.FLAG_PERSISTENT) 
				== ApplicationInfo.FLAG_PERSISTENT);
	}
	
	public String getVersionName() {
		return mAppVerName;
	}
	
	public int getVersionCode() {
		return mAppVerCode;
	}
	
	@Override
	public CharSequence getLabel() {
		CharSequence label = super.getLabel();
		if (label != null) {
			return label;
		}
		
		return mPackageName;
	}
	
	@Override
	public void resolveLabelAndIcon(Context context) {
		if (context == null || mPackageName == null) {
			return;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return;
		}
			
		try {
			ApplicationInfo aInfo = 
				pkgmgr.getApplicationInfo(mPackageName, 0);
			if (aInfo == null) {
				return;
			}
			
			Drawable d = null;
			if (isHiResIconRequired()
					&& getIconDpi() != DEFAULT_ICON_DPI) {
				d = getFullResIcon(context, aInfo.packageName, aInfo.icon);
//				Logger.debug("use HiRes icon[%ddpi] for pkg[%s]: dimen[%-3dx%-3d]",
//						getIconDpi(),
//						getPackageName(), 
//						(d == null ? 0 : d.getIntrinsicWidth()),
//						(d == null ? 0 : d.getIntrinsicHeight()));
			} else {
				d = aInfo.loadIcon(pkgmgr);
//				Logger.debug("use normal icon[%ddpi] for pkg[%s]: dimen[%-3dx%-3d]",
//						getIconDpi(),
//						getPackageName(), 
//						(d == null ? 0 : d.getIntrinsicWidth()),
//						(d == null ? 0 : d.getIntrinsicHeight()));
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
			Logger.warnning("resolve resources failure: %s", e.toString());
		}
	}

	public void uninstall(Context context) {
		String packageName = getPackageName();
		if (packageName == null) {
			return;
		}
		
        Intent intent = new Intent(Intent.ACTION_DELETE,
        		Uri.fromParts("package", packageName, null));
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        
        ActivityLauncher.launchActivity(context, intent);
	}
	
	public void launch(Context context) {
		Intent launchIntent = getLaunchIntent(context);
		if (launchIntent == null) {
			return;
		}
		
		launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		
		ActivityLauncher.launchActivity(context, launchIntent);
	}
	
	public void manage(Context context) {
        String packageName = getPackageName();
        
        Intent intent = new Intent(
        		"android.settings.APPLICATION_DETAILS_SETTINGS",
                Uri.fromParts("package", packageName, null));
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
        		| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		
		ActivityLauncher.launchActivity(context, intent);
	}
	
	public boolean isInstalled(Context context) {
		return isInstalled(context, mPackageName);
	}
	
	public Intent getLaunchIntent(Context context) {
		if (context == null || mPackageName == null) {
			return null;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return null;
		}
		
		Intent i = null;
		i = pkgmgr.getLaunchIntentForPackage(mPackageName);
		
		return i;
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x): pkg = %s, [ver(%d, %s), flags: 0x%08x]",
				getClass().getSimpleName(),
				hashCode(),
				getPackageName(),
				getVersionCode(),
				getVersionName(),
				getFlags());
	}

	public boolean isLaunchable(Context context) {
		if (context == null || mPackageName == null) {
			return false;
		}
		
		final Intent i = getLaunchIntent(context);
		
		return (i != null);
	}
	
	public final static List<AndroidApplication> queryApplications(Context context) {
		return queryApplications(context, -1);
	}
	
	public final static List<AndroidApplication> queryApplications(
			Context context, int appFlags) {
		if (context == null) {
			return null;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return null;
		}
		
		List<PackageInfo> pInfos = pkgmgr.getInstalledPackages(0);
		if (pInfos == null) {
			return null;
		}
		
		final int N = pInfos.size();
		if (N <= 0) {
			return null;
		}
		
		List<AndroidApplication> applications = 
			new ArrayList<AndroidApplication>(N);
		
		PackageInfo pInfo = null;
		for (int i = 0; i < N; i++) {
			pInfo = pInfos.get(i);
			
			if (pInfo.applicationInfo == null) {
				continue;
			}
			
			if (appFlags != -1) {
				if ((pInfo.applicationInfo.flags & appFlags) == 0)   {
						continue;
				}
			}
			
			applications.add(new AndroidApplication(pInfo));
		}
		
		Collections.sort(applications, new AndroidApplicationComparator());
		
		return applications;
	}
	
	public static final boolean isSystemApplication(Context context, String pkg) {
		if (context == null) {
			return false;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return false;
		}
		
		ApplicationInfo appInfo = null;
		try {
			appInfo = pkgmgr.getApplicationInfo(pkg, 0);
		} catch (NameNotFoundException e) {
			Logger.warnning("cannot get appInfo for pkg: %s", pkg);
			
			appInfo = null;
		}
		
		if (appInfo == null) {
			return false;
		}
		
		return ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) 
					== ApplicationInfo.FLAG_SYSTEM);
	}
	
	public static final boolean isDefaultLauncherApp(Context context, String pkg) {
		if (context == null || pkg == null) {
			return false;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return false;
		}
		
		List<ResolveInfo> launcherInfos = null;
		
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);

		launcherInfos = pkgmgr.queryIntentActivities(intent, 0);
		if (launcherInfos == null) {
			return false;
		}
		
		for (ResolveInfo rInfo: launcherInfos) {
			if (rInfo.activityInfo == null) {
				continue;
			}
			
			if (rInfo.activityInfo.applicationInfo == null) {
				continue;
			}
			
			if (pkg.equals(rInfo.activityInfo.applicationInfo.packageName)) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean isInstalled(Context context, String pkg) {
		if (context == null || pkg == null) {
			return false;
		}
		
		final PackageManager pkgmgr = context.getPackageManager();
		if (pkgmgr == null) {
			return false;
		}
			
		boolean installed = false;
		try {
			ApplicationInfo aInfo = 
				pkgmgr.getApplicationInfo(pkg, 0);
			
			installed = (aInfo != null);
		} catch (NameNotFoundException e) {
//			Logger.warnning("check installation failure: %s", e.toString());
					
			installed = false;
		}

		return installed;
	}

	public Drawable getFullResIcon(Context context,
			int iconId) {
		return getFullResIcon(context, getPackageName(), iconId);
	}
	
}
