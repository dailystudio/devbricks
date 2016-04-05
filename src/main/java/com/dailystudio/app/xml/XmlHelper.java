package com.dailystudio.app.xml;

import android.content.res.Resources;
import android.util.AttributeSet;

public class XmlHelper {

	public final static int INVALID_RESOURCE_ID = 0;
    
	public static String parseString(AttributeSet attrs, int index, Resources res) {
    	if (attrs == null || res == null) {
    		return null;
    	}
    	
		String label = null;

		int resId = attrs.getAttributeResourceValue(index, -1);
		if (resId > 0) {
			label = res.getString(resId);
		}
		
		if (label != null) {
			return label;
		}
		
		return attrs.getAttributeValue(index);
    }

	public static String parseLabel(AttributeSet attrs, int index, Resources res) {
    	if (attrs == null || res == null) {
    		return null;
    	}
    	
		String label = null;

		int resId = attrs.getAttributeResourceValue(index, -1);
		if (resId > 0) {
			label = res.getString(resId);
		}
		
		if (label != null) {
			return label;
		}
		
		return attrs.getAttributeValue(index);
    }
    
	public static int parseResource(AttributeSet attrs, int index, Resources res) {
    	if (attrs == null || res == null) {
    		return INVALID_RESOURCE_ID;
    	}
    	
		return attrs.getAttributeResourceValue(index, INVALID_RESOURCE_ID);
    }
    
}
