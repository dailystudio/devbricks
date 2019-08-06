package com.dailystudio.app.prefs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.dailystudio.development.Logger;

public abstract class AbsPrefs {

	public static final String ACTION_PREFS_CHANGED = "devbricks.intent.ACTION_PREFS_CHANGED";
	public static final String EXTRA_PREF_KEY = "devbricks.intent.EXTRA_PREF_KEY";

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

		notifyPrefChanged(context, pref);
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

		notifyPrefChanged(context, pref);
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

		notifyPrefChanged(context, pref);
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

		notifyPrefChanged(context, pref);
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

		notifyPrefChanged(context, pref);
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

	protected void notifyPrefChanged(Context context, String key) {
		if (context == null || TextUtils.isEmpty(key)) {
			return;
		}

		Intent i = new Intent(ACTION_PREFS_CHANGED);
		i.putExtra(EXTRA_PREF_KEY, key);

		LocalBroadcastManager.getInstance(context).sendBroadcast(i);
	}

	public void registerPrefChangesReceiver(Context context, BroadcastReceiver receiver) {
		if (context == null || receiver == null) {
			return;
		}

		IntentFilter filter = new IntentFilter(ACTION_PREFS_CHANGED);

		try {
			LocalBroadcastManager.getInstance(context)
					.registerReceiver(receiver, filter);
		} catch (Exception e) {
			Logger.warn("could not register receiver [%s] on %s: %s",
					receiver, ACTION_PREFS_CHANGED, e.toString());
		}
	}

	public void unregisterPrefChangesReceiver(Context context, BroadcastReceiver receiver) {
		if (context == null || receiver == null) {
			return;
		}

		try {
			LocalBroadcastManager.getInstance(context)
					.unregisterReceiver(receiver);
		} catch (Exception e) {
			Logger.warn("could not unregister receiver [%s] from %s: %s",
					receiver, ACTION_PREFS_CHANGED, e.toString());
		}
	}

	abstract protected String getPrefName();

}
