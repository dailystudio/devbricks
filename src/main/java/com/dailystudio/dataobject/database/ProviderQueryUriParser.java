package com.dailystudio.dataobject.database;

import android.net.Uri;

class ProviderQueryUriParser extends ProviderUriParser {
	
	final static String BASE_QUERY = "query";
	
	protected final static int SEG_QUERY_IND_DATABASE = SEG_INDEX_BASE + 1;
	protected final static int SEG_QUERY_IND_VERSION = SEG_QUERY_IND_DATABASE + 1;
	protected final static int SEG_QUERY_IND_TABLE = SEG_QUERY_IND_DATABASE + 2;
//	protected final static int SEG_QUERY_IND_SERIAL = SEG_QUERY_IND_DATABASE + 3;
	
	protected final static String QUERY_KEY_SERIAL = "serial";
	protected final static String QUERY_KEY_CREATE_TABLE = "createTable";

	public ProviderQueryUriParser(Uri uri) {
		super(uri);
	}

	@Override
	protected boolean hasValidBase() {
		final String base = getBase();

		return BASE_QUERY.equals(base);
	}
	
	@Override
	public String getDatabase() {
		return getSegmentFromUri(SEG_QUERY_IND_DATABASE);
	}

	@Override
	public String getTable() {
		return getSegmentFromUri(SEG_QUERY_IND_TABLE);
	}

	public int getVersion() {
		String versionStr = getSegmentFromUri(SEG_QUERY_IND_VERSION);
		if (versionStr == null) {
			return 0;
		}
		
		int version = 0;
		try {
			version = Integer.parseInt(versionStr);
		} catch (NumberFormatException e) {
			version = 0;
		}
		
		return version;
	}
	
	public long getSerial() {
		if (mUri == null) {
			return 0l;
		}
		
		String serialStr = mUri.getQueryParameter(QUERY_KEY_SERIAL);
		
		long serial = 0;
		try {
			serial = Long.parseLong(serialStr);
		} catch (NumberFormatException e) {
			serial = 0;
		}
		
		return serial;
	}
	
	public String getCreateTableSQL() {
		if (mUri == null) {
			return null;
		}
		
		return mUri.getQueryParameter(QUERY_KEY_CREATE_TABLE);
	}

}
