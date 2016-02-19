package com.dailystudio.app.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import com.dailystudio.development.Logger;

public class ThumbAsyncDecoder {
	
	public static abstract class AbsDecodeThumbAsyncTask 
		extends AsyncTask<Context, Void, Bitmap> {

		private String mThumbKey;
		private String mDecodeSource;
		
		public AbsDecodeThumbAsyncTask(String thumbKey, String decodeSource) {
			mThumbKey = thumbKey;
			mDecodeSource = decodeSource;
		}
		
		public String getThumbKey() {
			return mThumbKey;
		}
		
		public String getDecodeSource() {
			return mDecodeSource;
		}
		
		public void decode(Context context) {
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
		}
		
		@Override
		protected Bitmap doInBackground(Context... params) {
			Context context = null;
			
			if (params != null && params.length > 0) {
				context = params[0];
			}
			
			if (mThumbKey == null || mDecodeSource == null) {
				return null;
			}

			Bitmap cachedBitmap = ThumbCacheManager.queryCachedThumb(mThumbKey);
			if (cachedBitmap != null) {
				Logger.debug("SKIP DECODE EXIST THUMB: thumbKey = %s", 
						mThumbKey);
				return null;
			}
			
			return doDecodeBitmap(context, mThumbKey, mDecodeSource);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			if (result != null) {
				ThumbCacheManager.cachedThumb(mThumbKey, result);
			}
			
			synchronized (sThumbDecodeRequests) {
				sThumbDecodeRequests.remove(mThumbKey);
			}
		}

		abstract protected Bitmap doDecodeBitmap(Context context,
				String thumbKey, String decodeSource);
		
	}
	
	private static class DecodeFileThumbAsyncTask extends AbsDecodeThumbAsyncTask {

		public DecodeFileThumbAsyncTask(String thumbKey, String decodeSource) {
			super(thumbKey, decodeSource);
		}

		@Override
		protected Bitmap doDecodeBitmap(Context context,
				String thumbKey, String decodeSource) {
			if (decodeSource == null) {
				return null;
			}
			
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeFile(decodeSource);
			} catch (OutOfMemoryError e) {
				Logger.debug("decode thumb failure: %s", e.toString());
				
				bitmap = null;
			}

/*			Logger.debug("DECODE THUMB: thumb = %s, bitmap = %s", 
					thumbPath,
					bitmap);
*/			
			return bitmap;
		}

	}

	private static class DecodeActivityIconThumbAsyncTask extends AbsDecodeThumbAsyncTask {

		public DecodeActivityIconThumbAsyncTask(String thumbKey, String activityComp) {
			super(thumbKey, activityComp);
		}

		@Override
		protected Bitmap doDecodeBitmap(Context context,
				String thumbKey, String decodeSource) {
			if (context == null || decodeSource == null) {
				return null;
			}
			
			final ComponentName comp = 
				ComponentName.unflattenFromString(decodeSource);
			if (comp == null) {
				return null;
			}
			
			final PackageManager pkgmgr = context.getPackageManager();
			if (pkgmgr == null) {
				return null;
			}
			
			Bitmap bitmap = null;
			try {
				Intent i = new Intent();
				
				i.setComponent(comp);
				
				List<ResolveInfo> activities = 
					pkgmgr.queryIntentActivities(i, 0);
				if (activities != null && activities.size() > 0) {
					Drawable d = activities.get(0).loadIcon(pkgmgr);
					
					if (d instanceof BitmapDrawable) {
						bitmap = ((BitmapDrawable)d).getBitmap();
					}
				}
			} catch (OutOfMemoryError e) {
				Logger.debug("decode thumb failure: %s", e.toString());
				
				bitmap = null;
			}

/*			Logger.debug("DECODE THUMB: thumb = %s, bitmap = %s", 
					thumbPath,
					bitmap);
*/			
			return bitmap;
		}

	}

	private static class DecodeResourceThumbAsyncTask extends AbsDecodeThumbAsyncTask {

		private static final String RES_IDENTIFIER_SPLITTER = ":";
		
		public DecodeResourceThumbAsyncTask(String thumbKey, String resPkg, int resId) {
			super(thumbKey, buildupResourceInfo(resPkg, resId));
		}

		@Override
		protected Bitmap doDecodeBitmap(Context context,
				String thumbKey, String decodeSource) {
			if (context == null || decodeSource == null) {
				return null;
			}
			
			final String pkg = dumpPackage(decodeSource);
			final int resId = dumpResourceId(decodeSource);
			if (pkg == null || resId <= 0) {
				Logger.debug("invalid params: pkg = %s, resId = %d", 
						pkg, resId);
				return null;
			}
			
			final PackageManager pkgmgr = context.getPackageManager();
			if (pkgmgr == null) {
				return null;
			}
			
			Bitmap bitmap = null;
			try {
				final Resources res = pkgmgr.getResourcesForApplication(pkg);
				if (res != null) {
					Drawable d = res.getDrawable(resId);
					
					if (d instanceof BitmapDrawable) {
						bitmap = ((BitmapDrawable)d).getBitmap();
					}
				}
			} catch (NameNotFoundException e) {
				Logger.debug("decode thumb failure: %s", e.toString());
				
				bitmap = null;
			} catch (OutOfMemoryError e) {
				Logger.debug("decode thumb failure: %s", e.toString());
				
				bitmap = null;
			}

/*			Logger.debug("DECODE THUMB: thumb = %s, bitmap = %s", 
					thumbPath,
					bitmap);
*/			
			return bitmap;
		}

		private static String buildupResourceInfo(String pkg, int resId) {
			if (pkg == null || resId < 0) {
				return null;
			}
			
			StringBuilder b = new StringBuilder(pkg);
			
			b.append(RES_IDENTIFIER_SPLITTER);
			b.append(resId);
			
			return b.toString();
		}
		
		private static String dumpPackage(String resInfo) {
			if (resInfo == null) {
				return null;
			}
			
			String[] parts = resInfo.split(RES_IDENTIFIER_SPLITTER);
			if (parts == null) {
				return null;
			}
			
			return parts[0];
		}
		
		private int dumpResourceId(String resInfo) {
			if (resInfo == null) {
				return -1;
			}
			
			String[] parts = resInfo.split(RES_IDENTIFIER_SPLITTER);
			if (parts == null) {
				return -1;
			}
			
			int resId = -1;
			
			try {
				resId = Integer.parseInt(parts[1]);
			} catch (NumberFormatException e) {
				Logger.warnning("parse resId failure: %s", e.toString());
				
				resId = -1;
			}
			
			return resId;
		}
		
	}

	private static class DecodeAndroidResourceSchemeThumbAsyncTask 
		extends AbsDecodeThumbAsyncTask {

		public DecodeAndroidResourceSchemeThumbAsyncTask(String thumbKey, String resUri) {
			super(thumbKey, resUri);
		}

		@Override
		protected Bitmap doDecodeBitmap(Context context,
				String thumbKey, String decodeSource) {
			if (context == null || decodeSource == null) {
				return null;
			}
			
			final String pkg = dumpPackage(decodeSource);
			final String type = dumpResourceType(decodeSource);
			final String resName = dumpResourceName(decodeSource);
			if (pkg == null || type == null|| resName == null) {
				Logger.debug("invalid params: pkg = %s, typev = %s, resName = %d", 
						pkg, type, resName);
				return null;
			}
			
			final PackageManager pkgmgr = context.getPackageManager();
			if (pkgmgr == null) {
				return null;
			}
			
			Bitmap bitmap = null;
			try {
				final Resources res = pkgmgr.getResourcesForApplication(pkg);
				
				if (res != null) {
					final int resId = res.getIdentifier(resName, type, pkg);
					if (resId <= 0) {
						Logger.debug("resId = %d, no res found for: pkg = %s, typev = %s, resName = %d", 
								resId, pkg, type, resName);
						return null;
					}
					
					Drawable d = res.getDrawable(resId);
					
					if (d instanceof BitmapDrawable) {
						bitmap = ((BitmapDrawable)d).getBitmap();
					}
				}
			} catch (NameNotFoundException e) {
				Logger.debug("decode thumb failure: %s", e.toString());
				
				bitmap = null;
			} catch (OutOfMemoryError e) {
				Logger.debug("decode thumb failure: %s", e.toString());
				
				bitmap = null;
			}

/*			Logger.debug("DECODE THUMB: thumb = %s, bitmap = %s", 
					thumbPath,
					bitmap);
*/			
			return bitmap;
		}

		public static String dumpPackage(String resUri) {
			if (resUri == null) {
				return null;
			}
			
			Uri uri = Uri.parse(resUri);
			
			return uri.getHost();
		}
	   
		public static String dumpResourceName(String resUri) {
			if (resUri == null) {
				return null;
			}
			
			Uri uri = Uri.parse(resUri);
			
			return uri.getLastPathSegment();
		}
		
		public static String dumpResourceType(String resUri) {
			if (resUri == null) {
				return null;
			}
			
			Uri uri = Uri.parse(resUri);
			
			List<String> segments = uri.getPathSegments();
			if (segments == null || segments.size() < 2) {
				return null;
			}
			
			return segments.get(0);
		}
		
	}

	private static Set<String> sThumbDecodeRequests = new HashSet<String>();
    
	public static void requestDecodeFileThumb(Context context,
			String thumbKey, String thumbFilePath) {
		if (context == null) {
			return;
		}
		
		if (thumbKey == null || thumbFilePath == null) {
			return;
		}

		requestDecodeThumb(context, 
				new DecodeFileThumbAsyncTask(thumbKey, thumbFilePath));
	}
	
	public static void requestDecodeActivityIconThumb(Context context,
			String thumbKey, ComponentName comp) {
		if (context == null) {
			return;
		}
		
		if (thumbKey == null || comp == null) {
			return;
		}

		requestDecodeThumb(context, new DecodeActivityIconThumbAsyncTask(
				thumbKey, comp.flattenToString()));
	}
	
	public static void requestDecodeResourceThumb(Context context,
			String thumbKey, String resPkg, int resId) {
		if (context == null) {
			return;
		}
		
		if (thumbKey == null || resPkg == null || resId < 0) {
			return;
		}

		requestDecodeThumb(context, new DecodeResourceThumbAsyncTask(
				thumbKey, resPkg, resId));
	}
	
	public static void requestDecodeAndroidResourceSchemeThumb(Context context,
			String thumbKey, String resUri) {
		if (context == null) {
			return;
		}
		
		if (thumbKey == null || resUri == null) {
			return;
		}

		requestDecodeThumb(context, new DecodeAndroidResourceSchemeThumbAsyncTask(
				thumbKey, resUri));
	}
	
	public static void requestDecodeThumb(Context context, 
			AbsDecodeThumbAsyncTask decodeTask) {
		if (context == null || decodeTask == null) {
			return;
		}
		
		final String thumbKey = decodeTask.getThumbKey();
		final String decodeSource = decodeTask.getDecodeSource();
		if (thumbKey == null || decodeSource == null) {
			Logger.warnning("invalid decode params: key = %s, source = %s", 
					thumbKey, decodeSource);
			
			return;
		}
		
		synchronized (sThumbDecodeRequests) {
			if (sThumbDecodeRequests.contains(thumbKey)) {
				Logger.debug("SKIP PENDING DECODE: thumbKey = %s", thumbKey);
				return;
			}
			
			sThumbDecodeRequests.add(thumbKey);
			
			decodeTask.decode(context);
		}
	}
	
}
