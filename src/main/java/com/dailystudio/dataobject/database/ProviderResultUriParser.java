package com.dailystudio.dataobject.database;

import android.net.Uri;

class ProviderResultUriParser extends ProviderUriParser {
	
//	final static String BASE_RESULT = "result";
	final static String BASE_RESULT = "query";
	
	protected final static int SEG_RESULT_IND_DATABASE = SEG_INDEX_BASE + 1;
	protected final static int SEG_RESULT_IND_VERSION = SEG_RESULT_IND_DATABASE + 1;
	protected final static int SEG_RESULT_IND_TABLE = SEG_RESULT_IND_DATABASE + 2;
	protected final static int SEG_RESULT_IND_ROWID = SEG_RESULT_IND_DATABASE + 3;
	
	public ProviderResultUriParser(Uri uri) {
		super(uri);
	}

	@Override
	protected boolean hasValidBase() {
		final String base = getBase();

		return BASE_RESULT.equals(base);
	}
	
	@Override
	public String getDatabase() {
		return getSegmentFromUri(SEG_RESULT_IND_DATABASE);
	}

	@Override
	public String getTable() {
		return getSegmentFromUri(SEG_RESULT_IND_TABLE);
	}

	public long getRowId() {
		String rowIdStr = getSegmentFromUri(SEG_RESULT_IND_ROWID);
		if (rowIdStr == null) {
			return 0l;
		}
		
		long rowId = 0l;
		try {
			rowId = Long.parseLong(rowIdStr);
		} catch (NumberFormatException e) {
			rowId = 0l;
		}
		
		return rowId;
	}

}
