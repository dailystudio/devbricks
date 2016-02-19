package com.dailystudio.app.location;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.development.Logger;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationTracker {
	
	public static interface LocationChangedListener {
		
		public void onLocationChanged(Location location);
	
	}
	
	private LocationManager mLocationManager = null;
	private String mProviderName = null;
	
	private Location mLastLocation;
	
	private List<LocationChangedListener> mListeners = 
			new ArrayList<LocationTracker.LocationChangedListener>();
	
	public LocationTracker(Context context) {
		if (context == null) {
			return;
		}
		
		mLocationManager = 
			(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		refreshAndListenOnProvider();
	}
	
	public void beginLocationTracking() {
		refreshAndListenOnProvider();
	}
	
	public void endLocationTracking() {
		if (mLocationManager == null) {
			return;
		}
		
		mLocationManager.removeUpdates(mLocationListener);
	}

	private void refreshAndListenOnProvider() {
		if (mLocationManager == null) {
			return;
		}
		
		mProviderName = findBestProvider(mLocationManager);
		Logger.debug("mProviderName(%s)", mProviderName);
		
		if (mProviderName == null) {
			return;
		}
		
		try {
			mLocationManager.requestLocationUpdates(mProviderName, 
					0, 0, mLocationListener);
		} catch (Exception e) {
			Logger.warnning("request location updates failure: %s", e.toString());
		}
	}
	
	private String findBestProvider(LocationManager locmgr) {
		if (locmgr == null) {
			return null;
		}
		
		Criteria crietria = new Criteria();
		
		crietria.setAccuracy(Criteria.ACCURACY_FINE);
		crietria.setSpeedRequired(true);
		
		return locmgr.getBestProvider(crietria, true);
	}

	public void printProviders(List<String> providers) {
		if (providers == null) {
			return;
		}
		
		final int count = providers.size();
		
		String proName = null;
		for(int i = 0; i < count; i++) {
			proName = providers.get(i);
			Logger.debug("[%03d]: proName(%s)", 
					i, proName);
		}
	}
	
	public Location getLocation() {
		if (mLastLocation == null) {
			mLastLocation = mLocationManager.getLastKnownLocation(mProviderName);
		}
		
		return mLastLocation;
	}

	public void addLocationChangedListener(LocationChangedListener l) {
		if (l == null) {
			return;
		}
		
		mListeners.add(l);
	}
	
	private void triggerLocationUpdate(Location loc) {
		if (mListeners == null || mListeners.size() <= 0) {
			return;
		}
		
		for (LocationChangedListener l: mListeners) {
			l.onLocationChanged(loc);
		}
	}

	private LocationListener mLocationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Logger.debug("provider(%s), status(%d), extras(%s)",
					provider,
					status,
					extras);
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			Logger.debug("provider(%s)",
						provider);
			
			refreshAndListenOnProvider();
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Logger.debug("provider(%s)",
						provider);
			
			refreshAndListenOnProvider();
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Logger.debug("location(%s)",
					location);
			
			mLastLocation = location;
			
			triggerLocationUpdate(mLastLocation);
		}
		
	};

}
