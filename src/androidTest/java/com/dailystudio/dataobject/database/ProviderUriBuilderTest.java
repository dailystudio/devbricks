package com.dailystudio.dataobject.database;

import android.net.Uri;

import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.samples.SampleObject1;
import com.dailystudio.test.ActivityTestCase;

public class ProviderUriBuilderTest extends ActivityTestCase {
	
	private static final String AUTHORITY = "com.dailystudio";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testBuildQueryUri() {
		final long serial = System.currentTimeMillis();
		
		Uri uri1 = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, serial);
		assertNotNull(uri1);
		
		Uri uri2 = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, 0x3, serial);
		assertNotNull(uri2);
		
		Uri uri3 = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, 0x4);
		assertNotNull(uri3);
		
		Uri uri4 = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class);
		assertNotNull(uri4);
		
		final Template template = new SampleObject1(mContext).getTemplate();
		assertNotNull(template);
		
		final String table = DatabaseObject.classToTable(SampleObject1.class);
		assertNotNull(table);

		String uristr = null;
		Uri expectedUri = null;
			
		uristr = String.format(
				"content://com.dailystudio/query/com.dailystudio.dataobject.samples.SampleObject1.db/1/SampleObject1?serial=%d",
				serial);
		expectedUri = Uri.parse(uristr);
		assertEquals(expectedUri, uri1);
		
		uristr = String.format(
				"content://com.dailystudio/query/com.dailystudio.dataobject.samples.SampleObject1.db/3/SampleObject1?serial=%d",
				serial);
		expectedUri = Uri.parse(uristr);
		assertEquals(expectedUri, uri2);
		
		uristr = "content://com.dailystudio/query/com.dailystudio.dataobject.samples.SampleObject1.db/4/SampleObject1";
		expectedUri = Uri.parse(uristr);
		assertEquals(expectedUri, uri3);
		
		uristr = "content://com.dailystudio/query/com.dailystudio.dataobject.samples.SampleObject1.db/1/SampleObject1";
		expectedUri = Uri.parse(uristr);
		assertEquals(expectedUri, uri4);
	}
	
	public void testAttachCreateTableParamter() {
		final long serial = System.currentTimeMillis();
		
		Uri uri = ProviderUriBuilder.buildQueryUri(AUTHORITY, SampleObject1.class, serial);
		assertNotNull(uri);
		
		DatabaseObject sample = new SampleObject1(mContext);
		assertNotNull(sample);

		final Template template = sample.getTemplate();
		assertNotNull(template);
		
		final String table = DatabaseObject.classToTable(SampleObject1.class);
		assertNotNull(table);

		uri = ProviderUriBuilder.attachCreateTableParamter(
				uri, sample.toSQLTableCreationString());
		
		String uristr = String.format(
				"content://com.dailystudio/query/com.dailystudio.dataobject.samples.SampleObject1.db/1/SampleObject1?serial=%d&createTable=%s",
				serial,
				"CREATE%20TABLE%20IF%20NOT%20EXISTS%20SampleObject1%20(%20_id%20INTEGER%20NOT%20NULL%20PRIMARY%20KEY%2C%20time%20LONG%20NOT%20NULL%20)%3B");
		Uri expectedUri = 
			Uri.parse(uristr);
		
		assertEquals(expectedUri, uri);
	}

	public void testBuildResultUri() {
		final String database = DatabaseObject.classToDatabase(SampleObject1.class);
		final String table = DatabaseObject.classToTable(SampleObject1.class);
		
		Uri uri = null;
		String uristr = null;
		Uri expectedUri = null;
		
		uri = ProviderUriBuilder.buildResultUri(
				AUTHORITY, database, 1, table, 1234567890987654321l);
		assertNotNull(uri);
		
		uristr = String.format(
				"content://com.dailystudio/query/com.dailystudio.dataobject.samples.SampleObject1.db/1/SampleObject1/%d",
				1234567890987654321l);
		expectedUri = Uri.parse(uristr);
		assertEquals(expectedUri, uri);
		
		uri = ProviderUriBuilder.buildResultUri(AUTHORITY, database, 1, table);
		assertNotNull(uri);
		
		uristr = 
			"content://com.dailystudio/query/com.dailystudio.dataobject.samples.SampleObject1.db/1/SampleObject1";
		expectedUri = Uri.parse(uristr);
		assertEquals(expectedUri, uri);
	}

	public void testBuildCommandUri() {
		Uri uri = null;
		String uristr = null;
		Uri expectedUri = null;
		
		uri = ProviderUriBuilder.buildCommandUri(
				AUTHORITY, SampleObject1.class, GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(uri);
		
		uristr = 
			"content://com.dailystudio/command/com.dailystudio.dataobject.samples.SampleObject1.db/1/SampleObject1/cmd_get_update_info";
		expectedUri = Uri.parse(uristr);
		assertEquals(expectedUri, uri);
		
		uri = ProviderUriBuilder.buildCommandUri(
				AUTHORITY, SampleObject1.class, 10, GetUpdateInfoCmdCursor.COMMAND_NAME);
		assertNotNull(uri);
		
		uristr = 
			"content://com.dailystudio/command/com.dailystudio.dataobject.samples.SampleObject1.db/10/SampleObject1/cmd_get_update_info";
		expectedUri = Uri.parse(uristr);
		assertEquals(expectedUri, uri);
	}

}
