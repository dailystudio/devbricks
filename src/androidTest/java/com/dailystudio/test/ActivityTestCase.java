package com.dailystudio.test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.dailystudio.test.MainActivity;

public class ActivityTestCase extends ActivityInstrumentationTestCase2<MainActivity> {
	
	protected Context mContext;
	protected Context mTargetContext;
	
	public ActivityTestCase() {
		super("com.dailystudio.test", MainActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mContext = getInstrumentation().getContext();
		mTargetContext = getInstrumentation().getTargetContext();
	}

}
