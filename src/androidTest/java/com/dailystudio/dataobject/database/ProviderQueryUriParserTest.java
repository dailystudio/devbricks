package com.dailystudio.dataobject.database;

import android.content.Context;
import android.net.Uri;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.samples.SampleObject1;
import com.dailystudio.test.ActivityTestCase;

public class ProviderQueryUriParserTest extends ActivityTestCase {
	
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
		final long now = System.currentTimeMillis();
		Uri queryUri = null;
		ProviderQueryUriParser parser = null;
		
		queryUri = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, now);
		assertNotNull(queryUri);
		parser = new ProviderQueryUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals("com.dailystudio.dataobject.samples.SampleObject1.db", 
				parser.getDatabase());
		
		queryUri = ProviderUriBuilder.buildQueryUri(AUTHORITY, QueryUriTestObject.class, now);
		assertNotNull(queryUri);
		parser = new ProviderQueryUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals("com.dailystudio.dataobject.database.ProviderQueryUriParserTest_QueryUriTestObject.db", 
				parser.getDatabase());
	}
	
	public void testGetVersion() {
		final long now = System.currentTimeMillis();
		Uri queryUri = null;
		ProviderQueryUriParser parser = null;
		
		queryUri = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, now);
		assertNotNull(queryUri);
		parser = new ProviderQueryUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals(DatabaseObject.VERSION_START, parser.getVersion());
		
		queryUri = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, 10, now);
		assertNotNull(queryUri);
		parser = new ProviderQueryUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals(10, parser.getVersion());
	}

	public void testGetTable() {
		final long now = System.currentTimeMillis();
		Uri queryUri = null;
		ProviderQueryUriParser parser = null;
		
		queryUri = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, now);
		assertNotNull(queryUri);
		parser = new ProviderQueryUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals("SampleObject1", parser.getTable());
		
		queryUri = ProviderUriBuilder.buildQueryUri(AUTHORITY, QueryUriTestObject.class, now);
		assertNotNull(queryUri);
		parser = new ProviderQueryUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals("ProviderQueryUriParserTest_QueryUriTestObject", parser.getTable());
	}

	public void testGetSerial() {
		final long now = System.currentTimeMillis();
		Uri queryUri = null;
		ProviderQueryUriParser parser = null;
		
		queryUri = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, now);
		assertNotNull(queryUri);
		parser = new ProviderQueryUriParser(queryUri);
		assertNotNull(parser);
		
		assertEquals(now, parser.getSerial());
	}

	public void testGetCreateTabelSQL() {
		final long now = System.currentTimeMillis();
		Uri queryUri = null;
		ProviderQueryUriParser parser = null;
		
		queryUri = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, now);
		assertNotNull(queryUri);
		parser = new ProviderQueryUriParser(queryUri);
		assertNotNull(parser);
		
		assertNull(parser.getCreateTableSQL());
		
		DatabaseObject sample = new SampleObject1(mContext);
		assertNotNull(sample);
		
		final Template template = sample.getTemplate();
		assertNotNull(template);
		
		final String table = DatabaseObject.classToTable(SampleObject1.class);
		assertNotNull(table);
		
		queryUri = ProviderUriBuilder.attachCreateTableParamter(
				queryUri, sample.toSQLTableCreationString());
		parser = new ProviderQueryUriParser(queryUri);
		assertNotNull(parser);
		
		String expected = sample.toSQLTableCreationString();
	
		assertEquals(expected, parser.getCreateTableSQL());
	}

}
