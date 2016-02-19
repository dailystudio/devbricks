package com.dailystudio.dataobject.database;

import com.dailystudio.dataobject.DatabaseObject;

import android.net.Uri;
import android.net.Uri.Builder;

class ProviderUriBuilder {
	
	public static Uri buildQueryUri(String authority,
			Class<? extends DatabaseObject> klass) {
		return buildQueryUri(authority, klass, DatabaseObject.VERSION_START);
	}
	
	public static Uri buildQueryUri(String authority,
			Class<? extends DatabaseObject> klass, 
			long serial) {
		return buildQueryUri(authority, klass, DatabaseObject.VERSION_START, serial);
	}
	
	public static Uri buildQueryUri(String authority, 
			Class<? extends DatabaseObject> klass, 
			int version) {
		return buildQueryUri(authority, klass, version, -1);
	}
	
	public static Uri buildQueryUri(String authority, 
			Class<? extends DatabaseObject> klass, 
			int version, 
			long serial) {
		if (authority == null 
				|| klass == null 
				|| version < 0) {
			return null;
		}
		
		Uri baseUri = Uri.parse("content://" + authority);
		if (baseUri == null) {
			return null;
		}
		
		Builder builder = baseUri.buildUpon();
		if (builder == null) {
			return null;
		}
		
		final String database = DatabaseObject.classToDatabase(klass);
		final String table = DatabaseObject.classToTable(klass);
		
		if (database == null 
				|| table == null) {
			return null;
		}
		
		builder.appendPath(ProviderQueryUriParser.BASE_QUERY);
		builder.appendPath(database);
		builder.appendPath(String.valueOf(version));
		builder.appendPath(table);
//		if (serial > 0) {
//			builder.appendPath(String.valueOf(serial));
//		}
		
		Uri uri = builder.build();
		if (uri == null) {
			return null;
		}
		
		if (serial > 0) {
			uri = attachSerialParamter(uri, String.valueOf(serial));
		}
		
		return uri;
	}
	
	public static Uri buildResultUri(String authority, String database, int version, String table) {
		return buildResultUri(authority, database, version, table, -1);
	}
	
	public static Uri buildResultUri(String authority, String database, int version, String table, 
			long rowId) {
		if (authority == null 
				|| database == null 
				|| table == null) {
			return null;
		}
		
		Uri baseUri = Uri.parse("content://" + authority);
		if (baseUri == null) {
			return null;
		}
		
		Builder builder = baseUri.buildUpon();
		if (builder == null) {
			return null;
		}
		
		builder.appendPath(ProviderResultUriParser.BASE_RESULT);
		builder.appendPath(database);
		builder.appendPath(String.valueOf(version));
		builder.appendPath(table);
		
		if (rowId > 0) { 
			builder.appendPath(String.valueOf(rowId));
		}
		
		return builder.build();
	}
	
	private static Uri attachSerialParamter(Uri uri, String serial) {
		if (uri == null || serial == null) {
			return uri;
		}
		
		Builder builder = uri.buildUpon();
		if (builder == null) {
			return uri;
		}
		
		builder.appendQueryParameter(ProviderQueryUriParser.QUERY_KEY_SERIAL, 
				serial);
		
		return builder.build();
	}
	
	public static Uri attachCreateTableParamter(Uri uri, String createTabelParamter) {
		if (uri == null || createTabelParamter == null) {
			return uri;
		}
		
		Builder builder = uri.buildUpon();
		if (builder == null) {
			return uri;
		}
		
		builder.appendQueryParameter(ProviderQueryUriParser.QUERY_KEY_CREATE_TABLE, 
				createTabelParamter);
		
		return builder.build();
	}
	
	public static Uri buildCommandUri(String authority,
			Class<? extends DatabaseObject> klass,
			String command) {
		return buildCommandUri(authority, klass, DatabaseObject.VERSION_START, command);
	}
	
	public static Uri buildCommandUri(String authority,
			Class<? extends DatabaseObject> klass,
			int version,
			String command) {
		if (authority == null 
				|| klass == null
				|| version < 0
				|| command == null) {
			return null;
		}
		
		Uri baseUri = Uri.parse("content://" + authority);
		if (baseUri == null) {
			return null;
		}
		
		Builder builder = baseUri.buildUpon();
		if (builder == null) {
			return null;
		}
		
		final String database = DatabaseObject.classToDatabase(klass);
		final String table = DatabaseObject.classToTable(klass);
		
		if (database == null 
				|| table == null) {
			return null;
		}
		
		builder.appendPath(ProviderCommandUriParser.BASE_COMMAND);
		builder.appendPath(database);
		builder.appendPath(String.valueOf(version));
		builder.appendPath(table);
		builder.appendPath(command);
		
		return builder.build();
	}
	


}
