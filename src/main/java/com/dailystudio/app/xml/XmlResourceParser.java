package com.dailystudio.app.xml;

import android.util.AttributeSet;
import android.util.Xml;

import com.dailystudio.development.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public abstract class XmlResourceParser {
	
	public boolean loadXml(XmlPullParser parser) {
		if (parser == null) {
			return false;
		}
		
    	final String xmlRoot = getXmlRoot();
    	if (xmlRoot == null) {
			return false;
    	}
    	
    	AttributeSet attrs = Xml.asAttributeSet(parser);
    	
    	boolean success = false;
    	try {
    		final int depth = parser.getDepth();
    		boolean gotRoot = false;
    		
		    int type;
		    
		    Object object = null;
	        while (((type = parser.next()) != XmlPullParser.END_TAG ||
	                parser.getDepth() > depth) && type != XmlPullParser.END_DOCUMENT) {
	
	            if (type != XmlPullParser.START_TAG) {
	                continue;
	            }
	            
			    String nodeName = parser.getName();
			    
			    if (gotRoot == false) {
				    if (!xmlRoot.equals(nodeName)) {
				    	Logger.warn("xml does not start with tag %s",
								xmlRoot);
				        
				        success = false;
				        
				        break;
				    }
				    
				    gotRoot = true;
			    }
			    
			    object = parseObject(nodeName, attrs);
			    if (object != null) {
			    	onObjectParsed(object);
			    }
	        }

	        success = true;
    	} catch (XmlPullParserException e) {
    		Logger.warn("parse xml failed: %s", e.toString());
    		
    		success = false;
    	} catch (IOException e) {
			Logger.warn("parse xml failed: %s", e.toString());
    		
    		success = false;
		}

    	return success;
	}
	
	protected Object parseObject(String propertyName, AttributeSet attrs) {
	   	if (propertyName == null || attrs == null) {
    		return null;
    	}
	   	
	   	XmlPropertyParser<?> parser = getPropertyParser(propertyName);
	   	if (parser == null) {
	   		return null;
	   	}
    	
	   	return parser.parseObject(propertyName, attrs);
	}
	
	abstract protected XmlPropertyParser<?> getPropertyParser(String propertyName);

	abstract protected String getXmlRoot();

	abstract protected void onObjectParsed(Object object);

}
