package com.dailystudio.app.utils;

import java.util.Observable;
import java.util.Observer;

import com.dailystudio.development.Logger;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.LruCache;

public class ThumbCacheManager {
	
	private static class ThumbCacheObserable extends Observable {
		
		public void notifyCacheChanges() {
			setChanged();
			
			notifyObservers();
		}
		
	}
	
	private final static int CACHE_SIZE = 15;

	private static LruCache<String, Bitmap> sThumbsCache =
		 new LruCache<String, Bitmap>(CACHE_SIZE);

	private static ThumbCacheObserable sThumbCacheObserable = 
		new ThumbCacheObserable();
	
    public static void clear() {
    	synchronized (sThumbsCache) {
    		sThumbsCache.evictAll();
		}
    }
    
    public static void setCacheSize(int size) {
    	synchronized (sThumbsCache) {
    		sThumbsCache.evictAll();
    		sThumbsCache = new LruCache<String, Bitmap>(size);
		}
    }
    
    public static void addCacheObserver(Observer observer) {
    	synchronized (sThumbCacheObserable) {
    		sThumbCacheObserable.addObserver(observer);
		}
    }
    
    public static void removeCacheObserver(Observer observer) {
    	synchronized (sThumbCacheObserable) {
    		sThumbCacheObserable.deleteObserver(observer);
		}
    }
    
    public static Bitmap queryCachedThumb(String thumbKey) {
    	if (thumbKey == null) {
    		return null;
    	}
    	
    	Bitmap bitmap = null;
    	synchronized (sThumbsCache) {
    		bitmap = sThumbsCache.get(thumbKey);
		}
    	
//    	printStatistics();
    	
    	return bitmap;
    }

    public static void cachedThumb(String thumbKey, Bitmap thumb) {
    	if (thumbKey == null || thumb == null) {
    		return;
    	}
    	
    	synchronized (sThumbsCache) {
    		sThumbsCache.put(thumbKey, thumb);
		}
    	
    	sHandler.removeCallbacks(sNotifyChangesRunnable);
    	sHandler.post(sNotifyChangesRunnable);
    }
    
    public static void removeThumb(String thumbKey) {
    	if (thumbKey == null) {
    		return;
    	}
    	
    	synchronized (sThumbsCache) {
    		sThumbsCache.remove(thumbKey);
		}
    	
    	sHandler.removeCallbacks(sNotifyChangesRunnable);
    	sHandler.post(sNotifyChangesRunnable);
    }
    
    public static void printStatistics() {
    	final int hit = sThumbsCache.hitCount();
    	final int missed = sThumbsCache.missCount();
    	final int eviction = sThumbsCache.evictionCount();
    	final int created = sThumbsCache.createCount();
    	final int put = sThumbsCache.putCount();
    	
    	Logger.debug("hit(%.2f, %d / %d): m(%d), e(%d), c(%d), p(%d)", 
    			((float)hit / (hit + missed)),
    			hit,
    			(hit + missed),
    			missed,
    			eviction,
    			created,
    			put);
    }
    
    private static Runnable sNotifyChangesRunnable = new Runnable() {
		
		@Override
		public void run() {
	    	synchronized (sThumbCacheObserable) {
	    		sThumbCacheObserable.notifyCacheChanges();
			}
		}
		
	};
	
    private static Handler sHandler = new Handler();
    
}
