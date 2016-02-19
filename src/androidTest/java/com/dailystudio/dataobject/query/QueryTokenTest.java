package com.dailystudio.dataobject.query;

import com.dailystudio.dataobject.query.QueryToken;
import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.test.ActivityTestCase;

public class QueryTokenTest extends ActivityTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateQueryToken() {
		QueryToken token = null;

		token = new QueryToken("1234");
		assertEquals("1234", token.toString());

		token = new QueryToken(new IntegerColumn("intVal"));
		assertEquals("intVal", token.toString());
	}
	
}
