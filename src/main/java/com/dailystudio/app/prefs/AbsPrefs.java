package com.dailystudio.app.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class AbsPrefs {
    
	private SharedPreferences getSharedPreferences(Context context) {
		if (context == null) {
			return null;
		}
		
		return context.getSharedPreferences(
				getPrefName(), Context.MODE_PRIVATE);
	}
	
	private Editor getEditor(Context context) {
		if (context == null) {
			return null;
		}
		
		SharedPreferences sharedPref = getSharedPreferences(context);
		if (sharedPref == null) {
			return null;
		}
		
		return sharedPref.edit();
	}

	public void setStringPrefValue(Context context,
			String pref, String sValue) {
		if (context == null 
				|| pref == null
				|| sValue == null) {
			return;
		}
		
		Editor editor = getEditor(context);
		
		editor.putString(pref, sValue);
		editor.commit();
	}
	
	public void setBooleanPrefValue(Context context,
			String pref, boolean bValue) {
		if (context == null 
				|| pref == null) {
			return;
		}
		
		Editor editor = getEditor(context);
		
		editor.putBoolean(pref, bValue);
		editor.commit();
	}
	
	public void setLongPrefValue(Context context,
			String pref, long lValue) {
		if (context == null 
				|| pref == null) {
			return;
		}
		
		Editor editor = getEditor(context);
		
		editor.putLong(pref, lValue);
		editor.commit();
	}
	
	public void setIntegerPrefValue(Context context,
			String pref, int iValue) {
		if (context == null 
				|| pref == null) {
			return;
		}
		
		Editor editor = getEditor(context);
		
		editor.putInt(pref, iValue);
		editor.commit();
	}
	
	public void setFloatPrefValue(Context context,
			String pref, float fValue) {
		if (context == null 
				|| pref == null) {
			return;
		}
		
		Editor editor = getEditor(context);
		
		editor.putFloat(pref, fValue);
		editor.commit();
	}
	
	public String getStringPrefValue(Context context, String pref) {
		if (context == null 
				|| pref == null) {
			return null;
		}
		
		SharedPreferences sharedPref = getSharedPreferences(context);
		if (sharedPref == null) {
			return null;
		}
		
		return sharedPref.getString(pref, null);
	}
	
	public boolean getBooleanPrefValue(Context context, String pref) {
		return getBooleanPrefValue(context, pref, false);
	}
	
	public boolean getBooleanPrefValue(Context context, String pref,
			boolean defVal) {
		if (context == null 
				|| pref == null) {
			return defVal;
		}
		
		SharedPreferences sharedPref = getSharedPreferences(context);
		if (sharedPref == null) {
			return defVal;
		}
		
		return sharedPref.getBoolean(pref, defVal);
	}
	
	public long getLongPrefValue(Context context, String pref) {
		return getLongPrefValue(context, pref, 0l);
	}

	public long getLongPrefValue(Context context, String pref, long defVal) {
		if (context == null 
				|| pref == null) {
			return defVal;
		}
		
		SharedPreferences sharedPref = getSharedPreferences(context);
		if (sharedPref == null) {
			return defVal;
		}
		
		return sharedPref.getLong(pref, defVal);
	}

	public int getIntegerPreValue(Context context, String pref) {
		return getIntegerPreValue(context, pref, 0);
	}

	public int getIntegerPreValue(Context context, String pref, int defVal) {
		if (context == null 
				|| pref == null) {
			return defVal;
		}
		
		SharedPreferences sharedPref = getSharedPreferences(context);
		if (sharedPref == null) {
			return defVal;
		}
		
		return sharedPref.getInt(pref, defVal);
	}

	public float getFloatPreValue(Context context, String pref) {
		return getFloatPreValue(context, pref, 0.0f);
	}

	public float getFloatPreValue(Context context, String pref, float defVal) {
		if (context == null 
				|| pref == null) {
			return defVal;
		}
		
		SharedPreferences sharedPref = getSharedPreferences(context);
		if (sharedPref == null) {
			return defVal;
		}
		
		return sharedPref.getFloat(pref, defVal);
	}

	abstract protected String getPrefName();

}
