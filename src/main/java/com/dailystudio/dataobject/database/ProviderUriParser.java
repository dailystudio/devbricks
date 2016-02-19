package com.dailystudio.dataobject.database;

import java.util.List;

import android.net.Uri;

class ProviderUriParser {
	
	protected final static int SEG_INDEX_BASE = 0;
	
	protected Uri mUri = null;
	
	public ProviderUriParser(Uri uri) {
		mUri = uri;
		
		if (hasValidBase() == false) {
			throw new IllegalArgumentException(
					String.format("URI(%s) is NOT a valid uri for parser %s", 
							uri,
							getClass().getSimpleName()));
		}
	}
	
	protected String getSegmentFromUri(int segmentIndex) {
		if (mUri == null) {
			return null;
		}
	
		List<String> segments = mUri.getPathSegments();
		if (segments == null || segments.size() <= 0) {
			return null;
		}
		
		if (segmentIndex < 0 || segmentIndex >= segments.size()) {
			return null;
		}

		return segments.get(segmentIndex);
	}
	
	protected String getBase() {
		return getSegmentFromUri(SEG_INDEX_BASE);
	}
	
	protected boolean hasValidBase() {
		return true;
	}
	
	public String getDatabase() {
		return null;
	}
	
	public String getTable() {
		return null;
	}

}
