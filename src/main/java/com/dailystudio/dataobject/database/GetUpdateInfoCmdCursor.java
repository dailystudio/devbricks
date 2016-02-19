package com.dailystudio.dataobject.database;

public class GetUpdateInfoCmdCursor extends CommandCursor {

	final static String COMMAND_NAME = "cmd_get_update_info";
	
	final static String COLUMN_NEW_VERSION = "new_version";
	final static String COLUMN_OLD_VERSION = "old_version";
	
	private final static String[] sColumns = {
		COLUMN_NEW_VERSION,
		COLUMN_OLD_VERSION,
	};
	
	public GetUpdateInfoCmdCursor() {
		super(COMMAND_NAME);
		
		addColumns(sColumns);
	}

}
