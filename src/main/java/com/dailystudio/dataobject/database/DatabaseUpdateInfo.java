package com.dailystudio.dataobject.database;

public class DatabaseUpdateInfo {

	private int mOldVersion;
	private int mNewVersion;
	
	public DatabaseUpdateInfo(int newVer, int oldVer) {
		mNewVersion = newVer;
		mOldVersion = oldVer;
	}
	
	public int getNewVersion() {
		return mNewVersion;
	}

	public int getOldVersion() {
		return mOldVersion;
	}

	public boolean needUpdate() {
		return (mNewVersion != mOldVersion);
	}
	
	@Override
	public String toString() {
		return String.format("%s(0x%08x): newVer = %d, oldVer = %d, needUpdate(%s)",
				getClass(),
				hashCode(),
				mNewVersion,
				mOldVersion,
				needUpdate());
	}
	
}

