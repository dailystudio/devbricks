package com.dailystudio.dataobject.database;

import android.content.Context;
import android.net.Uri;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.samples.SampleObject1;
import com.dailystudio.development.Logger;
import com.dailystudio.test.ActivityTestCase;

public class ProviderResultUriParserTest extends ActivityTestCase {
	
	private static final String AUTHORITY = "com.dailystudio";

	private class ResultUriTestObject extends DatabaseObject {

		public ResultUriTestObject(Context context) {
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
		Uri resultUri = null;
		ProviderResultUriParser parser = null;
		
		resultUri = ProviderUriBuilder.buildResultUri(
				AUTHORITY,
				DatabaseObject.classToDatabase(SampleObject1.class),
				1,
				DatabaseObject.classToTable(SampleObject1.class),
				1);
		assertNotNull(resultUri);
		parser = new ProviderResultUriParser(resultUri);
		assertNotNull(parser);
		
		assertEquals("com.dailystudio.dataobject.samples.SampleObject1.db", 
				parser.getDatabase());
		
		resultUri = ProviderUriBuilder.buildResultUri(
				AUTHORITY,
				DatabaseObject.classToDatabase(ResultUriTestObject.class),
				1,
				DatabaseObject.classToTable(ResultUriTestObject.class),
				1);
		assertNotNull(resultUri);
		parser = new ProviderResultUriParser(resultUri);
		assertNotNull(parser);
		
		assertEquals("com.dailystudio.dataobject.database.ProviderResultUriParserTest_ResultUriTestObject.db", 
				parser.getDatabase());
	}

	public void testGetTable() {
		Uri resultUri = null;
		ProviderResultUriParser parser = null;
		
		resultUri = ProviderUriBuilder.buildResultUri(
				AUTHORITY,
				DatabaseObject.classToDatabase(SampleObject1.class),
				1,
				DatabaseObject.classToTable(SampleObject1.class),
				1);
		assertNotNull(resultUri);
		parser = new ProviderResultUriParser(resultUri);
		assertNotNull(parser);
		
		assertEquals("SampleObject1", 
				parser.getTable());
		
		resultUri = ProviderUriBuilder.buildResultUri(
				AUTHORITY,
				DatabaseObject.classToDatabase(ResultUriTestObject.class),
				1,
				DatabaseObject.classToTable(ResultUriTestObject.class),
				1);
		assertNotNull(resultUri);
		parser = new ProviderResultUriParser(resultUri);
		assertNotNull(parser);
		
		assertEquals("ProviderResultUriParserTest_ResultUriTestObject", 
				parser.getTable());
	}

	public void testGetRowId() {
		Uri resultUri = null;
		ProviderResultUriParser parser = null;
	
		final long rowId = 9999999999999999l;
		
		resultUri = ProviderUriBuilder.buildResultUri(
				AUTHORITY,
				DatabaseObject.classToDatabase(SampleObject1.class),
				1,
				DatabaseObject.classToTable(SampleObject1.class),
				rowId);
		assertNotNull(resultUri);
		parser = new ProviderResultUriParser(resultUri);
		assertNotNull(parser);
		Logger.debug("resultUri = %s", resultUri);
		
		assertEquals(rowId, parser.getRowId());
	}

}
