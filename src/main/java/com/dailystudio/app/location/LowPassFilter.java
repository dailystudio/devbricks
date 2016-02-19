package com.dailystudio.app.location;

public class LowPassFilter {

    private static final float ALPHA = 0.2f;

    private LowPassFilter() {
    }

    /**
     * Filter the given input against the previous values and return a low-pass
     * filtered result.
     * 
     * @param input
     *            float array to smooth.
     * @param prev
     *            float array representing the previous values.
     * @return float array smoothed with a low-pass filter.
     */
    public static float[] filter(float[] input, float[] prev) {
        if (input == null || prev == null) {
        	throw new NullPointerException("input and prev float arrays must be non-NULL");
        }
        
        if (input.length != prev.length) {
        	throw new IllegalArgumentException("input and prev must be the same length");
        }

        for (int i = 0; i < input.length; i++) {
            prev[i] = prev[i] + ALPHA * (input[i] - prev[i]);
        }
        
        return prev;
    }
    
}