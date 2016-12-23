package com.dailystudio.app.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Movie;
import android.net.Uri;
import android.os.AsyncTask;

import com.dailystudio.development.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieAsyncDecoder {
	
	public static abstract class AbsDecodeMovieAsyncTask
		extends AsyncTask<Context, Void, Movie> {

		private String mMovieKey;
		private String mDecodeSource;
		
		public AbsDecodeMovieAsyncTask(String thumbKey, String decodeSource) {
			mMovieKey = thumbKey;
			mDecodeSource = decodeSource;
		}
		
		public String getMovieKey() {
			return mMovieKey;
		}
		
		public String getDecodeSource() {
			return mDecodeSource;
		}
		
		public void decode(Context context) {
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
		}
		
		@Override
		protected Movie doInBackground(Context... params) {
			Context context = null;
			
			if (params != null && params.length > 0) {
				context = params[0];
			}
			
			if (mMovieKey == null || mDecodeSource == null) {
				return null;
			}

			Movie cachedMovie = MovieCacheManager.queryCachedMovie(mMovieKey);
			if (cachedMovie != null) {
				Logger.debug("SKIP DECODE EXIST MOVIE: thumbKey = %s",
						mMovieKey);
				return null;
			}
			
			return doDecodeMovie(context, mMovieKey, mDecodeSource);
		}
		
		@Override
		protected void onPostExecute(Movie result) {
			super.onPostExecute(result);
			
			if (result != null) {
				MovieCacheManager.cacheMovie(mMovieKey, result);
			}
			
			synchronized (sThumbDecodeRequests) {
				sThumbDecodeRequests.remove(mMovieKey);
			}
		}

		abstract protected Movie doDecodeMovie(Context context,
												String thumbKey,
												String decodeSource);
		
	}
	
	private static class DecodeFileMovieAsyncTask extends AbsDecodeMovieAsyncTask {

		public DecodeFileMovieAsyncTask(String thumbKey, String decodeSource) {
			super(thumbKey, decodeSource);
		}

		@Override
		protected Movie doDecodeMovie(Context context,
									   String thumbKey, String decodeSource) {
			if (decodeSource == null) {
				return null;
			}
			
			Movie movie = null;
			try {
				movie = Movie.decodeFile(decodeSource);
			} catch (OutOfMemoryError e) {
				Logger.debug("decode movie failure: %s", e.toString());
				
				movie = null;
			}

/*			Logger.debug("DECODE THUMB: thumb = %s, movie = %s",
					thumbPath,
					movie);
*/			
			return movie;
		}

	}

	private static class DecodeResourceMovieAsyncTask extends AbsDecodeMovieAsyncTask {

		private static final String RES_IDENTIFIER_SPLITTER = ":";
		
		public DecodeResourceMovieAsyncTask(String movieKey, String resPkg, int resId) {
			super(movieKey, buildupResourceInfo(resPkg, resId));
		}

		@Override
		protected Movie doDecodeMovie(Context context,
									  String movieKey,
									  String decodeSource) {
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

			Movie movie = null;
			try {
				final Resources res = pkgmgr.getResourcesForApplication(pkg);
				if (res != null) {
					movie = Movie.decodeStream(res.openRawResource(resId));

				}
			} catch (NameNotFoundException e) {
				Logger.debug("decode movie failure: %s", e.toString());

				movie = null;
			} catch (OutOfMemoryError e) {
				Logger.debug("decode movie failure: %s", e.toString());

				movie = null;
			}

/*			Logger.debug("DECODE MOVIE: thumb = %s, movie = %s",
					thumbPath,
					movie);
*/
			return movie;
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
				Logger.warn("parse resId failure: %s", e.toString());
				
				resId = -1;
			}
			
			return resId;
		}
		
	}

	private static class DecodeAndroidResourceSchemeMovieAsyncTask
		extends AbsDecodeMovieAsyncTask {

		public DecodeAndroidResourceSchemeMovieAsyncTask(String thumbKey, String resUri) {
			super(thumbKey, resUri);
		}

		@Override
		protected Movie doDecodeMovie(Context context,
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
			
			Movie movie = null;
			try {
				final Resources res = pkgmgr.getResourcesForApplication(pkg);
				
				if (res != null) {
					final int resId = res.getIdentifier(resName, type, pkg);
					if (resId <= 0) {
						Logger.debug("resId = %d, no res found for: pkg = %s, typev = %s, resName = %d",
								resId, pkg, type, resName);
						return null;
					}
					
					movie = Movie.decodeStream(res.openRawResource(resId));
				}
			} catch (NameNotFoundException e) {
				Logger.debug("decode movie failure: %s", e.toString());
				
				movie = null;
			} catch (OutOfMemoryError e) {
				Logger.debug("decode movie failure: %s", e.toString());
				
				movie = null;
			}

/*			Logger.debug("DECODE MOVIE: thumb = %s, movie = %s",
					thumbPath,
					movie);
*/			
			return movie;
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
    
	public static void requestDecodeFileMoive(Context context,
			String movieKey, String moiveFilePath) {
		if (context == null) {
			return;
		}
		
		if (movieKey == null || moiveFilePath == null) {
			return;
		}

		requestDecodeMovie(context,
				new DecodeFileMovieAsyncTask(movieKey, moiveFilePath));
	}

	public static void requestDecodeResourceMovie(Context context,
			String moveiKey, String resPkg, int resId) {
		if (context == null) {
			return;
		}
		
		if (moveiKey == null || resPkg == null || resId < 0) {
			return;
		}

		requestDecodeMovie(context, new DecodeResourceMovieAsyncTask(
				moveiKey, resPkg, resId));
	}
	
	public static void requestDecodeAndroidResourceSchemeMovie(Context context,
			String movieKey, String resUri) {
		if (context == null) {
			return;
		}
		
		if (movieKey == null || resUri == null) {
			return;
		}

		requestDecodeMovie(context, new DecodeAndroidResourceSchemeMovieAsyncTask(
				movieKey, resUri));
	}
	
	public static void requestDecodeMovie(Context context,
										  AbsDecodeMovieAsyncTask decodeTask) {
		if (context == null || decodeTask == null) {
			return;
		}
		
		final String movieKey = decodeTask.getMovieKey();
		final String decodeSource = decodeTask.getDecodeSource();
		if (movieKey == null || decodeSource == null) {
			Logger.warn("invalid decode params: key = %s, source = %s",
					movieKey, decodeSource);
			
			return;
		}
		
		synchronized (sThumbDecodeRequests) {
			if (sThumbDecodeRequests.contains(movieKey)) {
				Logger.debug("SKIP PENDING DECODE: movieKey = %s", movieKey);
				return;
			}
			
			sThumbDecodeRequests.add(movieKey);
			
			decodeTask.decode(context);
		}
	}
	
}
