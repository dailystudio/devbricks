package com.dailystudio.dataobject.database;

import android.content.Context;
import android.net.Uri;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.samples.SampleObject1;
import com.dailystudio.test.ActivityTestCase;

public class ProviderCommandUriParserTest extends ActivityTestCase {
	
	private static final String AUTHORITY = "com.dailystudio";

	private class QueryUriTestObject extends DatabaseObject {

		public QueryUriTestObject(Context context) {
			super(context);
		}
		
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetDatabase() {
		Uri queryUri = null;
		ProviderCommandUriParser parser = null;
		
		queryUri = ProviderUriBuilder.buildCommandUri(AUTHORITY, SampleObject1.class, 
				GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(queryUri);
		parser = new ProviderCommandUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals("com.dailystudio.dataobject.samples.SampleObject1.db", 
				parser.getDatabase());
		
		queryUri = ProviderUriBuilder.buildCommandUri(AUTHORITY, QueryUriTestObject.class,
				GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(queryUri);
		parser = new ProviderCommandUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals("com.dailystudio.dataobject.database.ProviderCommandUriParserTest_QueryUriTestObject.db", 
				parser.getDatabase());
	}
	
	public void testGetVersion() {
		Uri queryUri = null;
		ProviderCommandUriParser parser = null;
		
		queryUri = ProviderUriBuilder.buildCommandUri(AUTHORITY, SampleObject1.class, 
				GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(queryUri);
		parser = new ProviderCommandUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals(DatabaseObject.VERSION_START, parser.getVersion());
		
		queryUri = ProviderUriBuilder.buildCommandUri(AUTHORITY, SampleObject1.class, 10,
				GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(queryUri);
		parser = new ProviderCommandUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals(10, parser.getVersion());
	}

	public void testGetTable() {
		Uri queryUri = null;
		ProviderCommandUriParser parser = null;
		
		queryUri = ProviderUriBuilder.buildCommandUri(AUTHORITY, SampleObject1.class, 
				GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(queryUri);
		parser = new ProviderCommandUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals("SampleObject1", parser.getTable());
		
		queryUri = ProviderUriBuilder.buildCommandUri(AUTHORITY, QueryUriTestObject.class,
				GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(queryUri);
		parser = new ProviderCommandUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals("ProviderCommandUriParserTest_QueryUriTestObject", parser.getTable());
	}

	public void testGetCommand() {
		Uri queryUri = null;
		ProviderCommandUriParser parser = null;
		
		queryUri = ProviderUriBuilder.buildCommandUri(AUTHORITY, SampleObject1.class, 
				GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(queryUri);
		parser = new ProviderCommandUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals(GetUpdateInfoCmdCursor.COMMAND_NAME, parser.getCommand());
	}

}
