package com.dailystudio.app.utils;

import android.graphics.Movie;
import android.os.Handler;
import android.support.v4.util.LruCache;

import com.dailystudio.development.Logger;

import java.util.Observable;
import java.util.Observer;

public class MovieCacheManager {
	
	private static class MovieCacheObservable extends Observable {
		
		public void notifyCacheChanges() {
			setChanged();
			
			notifyObservers();
		}
		
	}
	
	private final static int CACHE_SIZE = 5;

	private static LruCache<String, Movie> sMoviesCache =
		 new LruCache<String, Movie>(CACHE_SIZE);

	private static MovieCacheObservable sMovieCacheObservable =
		new MovieCacheObservable();
	
    public static void clear() {
    	synchronized (sMoviesCache) {
    		sMoviesCache.evictAll();
		}
    }
    
    public static void setCacheSize(int size) {
    	synchronized (sMoviesCache) {
    		sMoviesCache.evictAll();
    		sMoviesCache = new LruCache<String, Movie>(size);
		}
    }
    
    public static void addCacheObserver(Observer observer) {
    	synchronized (sMovieCacheObservable) {
    		sMovieCacheObservable.addObserver(observer);
		}
    }
    
    public static void removeCacheObserver(Observer observer) {
    	synchronized (sMovieCacheObservable) {
    		sMovieCacheObservable.deleteObserver(observer);
		}
    }
    
    public static Movie queryCachedMovie(String thumbKey) {
    	if (thumbKey == null) {
    		return null;
    	}
    	
    	Movie movie = null;
    	synchronized (sMoviesCache) {
    		movie = sMoviesCache.get(thumbKey);
		}
    	
//    	printStatistics();
    	
    	return movie;
    }

    public static void cacheMovie(String thumbKey, Movie movie) {
    	if (thumbKey == null || movie == null) {
    		return;
    	}
    	
    	synchronized (sMoviesCache) {
    		sMoviesCache.put(thumbKey, movie);
		}
    	
    	sHandler.removeCallbacks(sNotifyChangesRunnable);
    	sHandler.post(sNotifyChangesRunnable);
    }
    
    public static void removeMovie(String thumbKey) {
    	if (thumbKey == null) {
    		return;
    	}
    	
    	synchronized (sMoviesCache) {
    		sMoviesCache.remove(thumbKey);
		}
    	
    	sHandler.removeCallbacks(sNotifyChangesRunnable);
    	sHandler.post(sNotifyChangesRunnable);
    }
    
    public static void printStatistics() {
    	final int hit = sMoviesCache.hitCount();
    	final int missed = sMoviesCache.missCount();
    	final int eviction = sMoviesCache.evictionCount();
    	final int created = sMoviesCache.createCount();
    	final int put = sMoviesCache.putCount();
    	
    	Logger.debug("hit(%.2f, %d / %d): m(%d), e(%d), c(%d), p(%d)",
				((float) hit / (hit + missed)),
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
	    	synchronized (sMovieCacheObservable) {
	    		sMovieCacheObservable.notifyCacheChanges();
			}
		}
		
	};
	
    private static Handler sHandler = new Handler();
    
}
