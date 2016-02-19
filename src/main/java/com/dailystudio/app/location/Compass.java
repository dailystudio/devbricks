package com.dailystudio.app.location;

import java.util.concurrent.atomic.AtomicBoolean;

import com.dailystudio.development.Logger;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Compass {
	
	public static interface CompassListener {
	
		public void onCompassChanged(Compass compass, double oldBearing, double newBearding);

	}
	
	private final static double DEFAULT_BEARING_CHANGE_THRESHOLD = 2.0;
	
    private final AtomicBoolean mComputing = new AtomicBoolean(false);
    
	private SensorManager mSensorManager;
	private Sensor mSensorMag;
	private Sensor mSensorGrav;

    private final float mGravValues[] = new float[3]; // Gravity (a.k.a accelerometer data)
	private final float mMagValues[] = new float[3]; // Magnetic
	private final float mRotation[] = new float[9]; // Rotation matrix in

	// Android format
	private final float mOrientation[] = new float[3]; // azimuth, pitch,
	// roll
	private float mSmoothed[] = new float[3];

    private GeomagneticField mGmf = null;

    private double mFloatBearing = 0;
    
    private Context mContext;
    
    private CompassListener mListenr;
    private double mBearingChangeThreshold = DEFAULT_BEARING_CHANGE_THRESHOLD;
    
	public Compass(Context context) {
		mContext = context;
		
		setupSensors();
	}
	
	private void setupSensors() {
		mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		
		if (mSensorManager != null) {
			mSensorMag = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			mSensorGrav = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
	}
	
	public void beginAnalyzeBearing() {
		Logger.debug("mSensorManager = %s, mSensor = %s", 
				mSensorManager, mSensorMag);
		if (mSensorManager != null
				&& mSensorMag != null) {
			mSensorManager.registerListener(mSensorListener,
					mSensorMag, SensorManager.SENSOR_DELAY_NORMAL);
			mSensorManager.registerListener(mSensorListener,
					mSensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
		}

	}

	public void endAnalyzeBearing() {
		Logger.debug("mSensorManager = %s, mSensor = %s", 
				mSensorManager, mSensorMag);
		if (mSensorManager != null
				&& mSensorMag != null) {
			mSensorManager.unregisterListener(mSensorListener,
					mSensorMag);
			mSensorManager.unregisterListener(mSensorListener,
					mSensorGrav);
		}
	}
	
	public double getBearing() {
		return mFloatBearing;
	}
	
	public void setBearingChangeThreshold(double threshold) {
		mBearingChangeThreshold = threshold;
	}
	
	public double getBearingChangeThreshold() {
		return mBearingChangeThreshold;
	}
	
	public void setCompassListenr(CompassListener l) {
		mListenr = l;
	}
	
	private SensorEventListener mSensorListener = new SensorEventListener() {
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
	        if (!mComputing.compareAndSet(false, true)) {
	        	return;
	        }

	        final double oldBearing = mFloatBearing;
	        
	        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	            mSmoothed = LowPassFilter.filter(event.values, mGravValues);
	            mGravValues[0] = mSmoothed[0];
	            mGravValues[1] = mSmoothed[1];
	            mGravValues[2] = mSmoothed[2];
	        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
	            mSmoothed = LowPassFilter.filter(event.values, mMagValues);
	            mMagValues[0] = mSmoothed[0];
	            mMagValues[1] = mSmoothed[1];
	            mMagValues[2] = mSmoothed[2];
	        }

	        // Get rotation matrix given the gravity and geomagnetic matrices
	        SensorManager.getRotationMatrix(mRotation, null, mGravValues, mMagValues);
	        SensorManager.getOrientation(mRotation, mOrientation);
	        mFloatBearing = mOrientation[0];

	        // Convert from radians to degrees
	        mFloatBearing = Math.toDegrees(mFloatBearing); // degrees east of true
	                                                     // north (180 to -180)

	        // Compensate for the difference between true north and magnetic north
	        if (mGmf != null){
	        	mFloatBearing += mGmf.getDeclination();
	        }

	        // adjust to 0-360
	        if (mFloatBearing < 0) {
	        	mFloatBearing += 360;
	        }

	        final double delta = mFloatBearing - oldBearing;
	        if (Math.abs(delta) > mBearingChangeThreshold) {
	        	if (mListenr != null) {
	        		mListenr.onCompassChanged(Compass.this, oldBearing, mFloatBearing);
	        	}
	        }
	        
	        mComputing.set(false);
		}
		
	};

}
