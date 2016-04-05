package com.dailystudio.app.xml;

import android.util.AttributeSet;

public abstract class XmlPropertyParser<T> {
	
	public Object parseObject(String propertyName, AttributeSet attrs) {
		T object = createObject(propertyName, attrs);
		
		XmlAttributesParser<T> parser = getAttributesParser(object, propertyName, attrs);
		if (parser != null) {
			parser.parseAttributes(object, attrs);
		}
		
		return object;
	}
	
	protected int findAttributeByName(AttributeSet attrs, String name) {
		if (attrs == null || name == null) {
			return -1;
		}
		
        int attrCount = attrs.getAttributeCount();
        if (attrCount <= 0) {
			return -1;
       }
        
        String attrName = null;

        for (int i = 0; i < attrCount; i++) {
        	attrName = attrs.getAttributeName(i);

        	if (name.equals(attrName)) {
        		return i;
        	}
        }

        return -1;
	}
	
	abstract protected T createObject(String propertyName, AttributeSet attrs);
	
	abstract protected XmlAttributesParser<T> getAttributesParser(T object, String propertyName, 
			AttributeSet attrs);


}
