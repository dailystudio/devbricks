package com.dailystudio.dataobject.database;

import android.net.Uri;

class ProviderCommandUriParser extends ProviderUriParser {

	final static String BASE_COMMAND = "command";
	
	protected final static int SEG_COMMAND_IND_DATABASE = SEG_INDEX_BASE + 1;
	protected final static int SEG_COMMAND_IND_VERSION = SEG_COMMAND_IND_DATABASE + 1;
	protected final static int SEG_COMMAND_IND_TABLE = SEG_COMMAND_IND_DATABASE + 2;
	protected final static int SEG_COMMAND_IND_COMMAND = SEG_COMMAND_IND_DATABASE + 3;
	
	public ProviderCommandUriParser(Uri uri) {
		super(uri);
	}

	@Override
	protected boolean hasValidBase() {
		final String base = getBase();

		return BASE_COMMAND.equals(base);
	}
	
	@Override
	public String getDatabase() {
		return getSegmentFromUri(SEG_COMMAND_IND_DATABASE);
	}

	@Override
	public String getTable() {
		return getSegmentFromUri(SEG_COMMAND_IND_TABLE);
	}


	public int getVersion() {
		String versionStr = getSegmentFromUri(SEG_COMMAND_IND_VERSION);
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
	
	public String getCommand() {
		return getSegmentFromUri(SEG_COMMAND_IND_COMMAND);
	}
	
}
