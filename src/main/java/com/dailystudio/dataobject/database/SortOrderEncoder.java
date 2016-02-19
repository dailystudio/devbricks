package com.dailystudio.dataobject.database;

class SortOrderEncoder {
	
	private final static String HEADER_GROUP_BY = "g: ";
	private final static String HEADER_HAVING = "h: ";
	private final static String HEADER_ORDER_BY = "o: ";
	private final static String HEADER_LIMIT = "l: ";
	
	private final static String SPLITTER = "\n";
	
	public static String encode(String groupBy, String having, String orderBy, String limit) {
		StringBuilder builder = new StringBuilder();
		
		if (groupBy != null) {
			builder.append(HEADER_GROUP_BY);
			builder.append(groupBy.replace(SPLITTER, ""));
			builder.append(SPLITTER);
		}
		
		if (having != null) {
			builder.append(HEADER_HAVING);
			builder.append(having.replace(SPLITTER, ""));
			builder.append(SPLITTER);
		}
		
		if (orderBy != null) {
			builder.append(HEADER_ORDER_BY);
			builder.append(orderBy.replace(SPLITTER, ""));
			builder.append(SPLITTER);
		}
		
		if (limit != null) {
			builder.append(HEADER_LIMIT);
			builder.append(limit.replace(SPLITTER, ""));
			builder.append(SPLITTER);
		}
		
		return builder.toString();
	}
	
	public static String[] decode(String encode) {
		if (encode == null) {
			return null;
		}
	
		String [] parts = encode.split(SPLITTER, 4);
		if (parts == null) {
			return null;
		}
		
		String[] decode = new String[4];
		for (String part: parts) {
			if (part == null || part.length() <= 0) {
				continue;
			}
			
			part = part.trim();
			part = part.replace("\n", "");
			
			if (part.startsWith(HEADER_GROUP_BY)) {
				decode[0] = part.substring(HEADER_GROUP_BY.length());
			} else if (part.startsWith(HEADER_HAVING)) {
				decode[1] = part.substring(HEADER_HAVING.length());
			} else if (part.startsWith(HEADER_ORDER_BY)) {
				decode[2] = part.substring(HEADER_ORDER_BY.length());
			} else if (part.startsWith(HEADER_LIMIT)) {
				decode[3] = part.substring(HEADER_LIMIT.length());
			}
		}
		
		return decode;
	}

}
