package com.dailystudio.app.xml;

import android.util.AttributeSet;

public abstract class XmlAttributesParser<T> {

	public static final int INVALID_ID = -1;
	
	public void parseAttributes(T object, AttributeSet attrs) {
        int attrCount = attrs.getAttributeCount();
        if (attrCount <= 0) {
    		return;
        }
        
        String attrName = null;

        for (int i = 0; i < attrCount; i++) {
        	attrName = attrs.getAttributeName(i);

			onParseAttribute(object, attrName, i, attrs);
        }
	}
	
	abstract protected void onParseAttribute(T object, String attrName,
			int attrIndex, AttributeSet attrs);


}
