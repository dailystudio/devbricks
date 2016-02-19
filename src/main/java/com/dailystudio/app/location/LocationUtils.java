package com.dailystudio.app.location;

public class LocationUtils {
	
	private final static double EARTH_RADIS = (6378.137 * 1000);
	private final static double PI = 3.1415926535898;
	
	public static double getDistanceBetween(double lat1, double lon1, double lat2, double lon2) {
		return getDistanceAlgorithm2(
				degreeToRadius(lat1), degreeToRadius(lon1), 
				degreeToRadius(lat2), degreeToRadius(lon2));
	}

	/*
	 * XXX: From Google Maps scripts
	 */
	static double getDistanceAlgorithm2(double lat1, double lon1, double lat2, double lon2) {
		final double pow1 = Math.pow(Math.sin((lat1 - lat2) / 2), 2);
		final double pow2 = Math.pow(Math.sin((lon1 - lon2) / 2), 2); 
		
		return (2 * Math.asin(Math.sqrt(pow1 + Math.cos(lat1) * Math.cos(lat2) * pow2)) * EARTH_RADIS);
	}
	
	private static double degreeToRadius(double degree) {
		return (degree * PI / 180);
	}
	
}
