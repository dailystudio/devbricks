package com.dailystudio.system;

import java.io.File;
import java.util.HashMap;

class CommandFinder {

	private static final String[] sSUCmdCandidates = {
		"/system/bin/su",
		"/system/xbin/su",
		"/data/bin/su",
	};

	private static final String[] sSHCmdCandidates = {
		"/system/bin/sh",
		"/data/bin/sh",
	};
	
	private static final HashMap<String, String[]> mCmdCandidatesMap  = new HashMap<String, String[]>();
	
	static {
		mCmdCandidatesMap.put("su", sSUCmdCandidates);
		mCmdCandidatesMap.put("sh", sSHCmdCandidates);
	}
	
	public static String findCommnd(String cmdCode) {
		if (cmdCode == null) {
			return null;
		}
		
		if (mCmdCandidatesMap == null) {
			return null;
		}
		
		final String[] candidates = mCmdCandidatesMap.get(cmdCode);
		if (candidates == null) {
			return null;
		}
		
		final int count = candidates.length;
		if (count <= 0) {
			return null;
		}
		
		File file = null;
		int i;
		
		for (i = 0; i < count; i++) {
			file = new File(candidates[i]);
			if (file.exists()) {
				break;
			}
		}
		
		if (i >= count) {
			return null;
		}
		
		return candidates[i];
	}
	
	public static String findSuCommand() {
		return findCommnd("su");
	}

	public static String findShCommand() {
		return findCommnd("sh");
	}
	
}
