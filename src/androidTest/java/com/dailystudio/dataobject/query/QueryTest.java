package com.dailystudio.dataobject.query;


import com.dailystudio.dataobject.IntegerColumn;
import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.dataobject.samples.TestDatabaseObject;

import android.test.AndroidTestCase;

public class QueryTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateQuery() {
		Template template = new Template();
		assertNotNull(template);
		
		Query query = null;
		
		query = new Query(TestDatabaseObject.class);
		assertNotNull(query);
		assertEquals(TestDatabaseObject.class, query.getObjectClass());
	}
	
	public void testSetAndGetSelection() {
		Column col1 = new IntegerColumn("intVal");
		
		Query query = null;
		
		query = new Query(TestDatabaseObject.class);
		assertNotNull(query);
		
		ExpressionToken selToken = col1.gt(100).and(col1.lt(200));
		query.setSelection(selToken);
		
		assertEquals(selToken, query.getSelection());
	}
	
	public void testSetAndGetGroupBy() {
		Column col1 = new IntegerColumn("intVal");
		
		Query query = null;
		
		query = new Query(TestDatabaseObject.class);
		assertNotNull(query);
		
		OrderingToken groupByToken = col1.groupBy();
		query.setGroupBy(groupByToken);
		
		assertEquals(groupByToken, query.getGroupBy());
	}
	
	public void testSetAndGetOrderBy() {
		Column col1 = new IntegerColumn("intVal");
		
		Query query = null;
		
		query = new Query(TestDatabaseObject.class);
		assertNotNull(query);
		
		OrderingToken groupByToken = col1.orderByAscending();
		query.setGroupBy(groupByToken);
		
		assertEquals(groupByToken, query.getGroupBy());
	}
	
	public void testSetAndGetLimit() {
		Query query = null;
		
		query = new Query(TestDatabaseObject.class);
		assertNotNull(query);
		
		ExpressionToken limitToken = new ExpressionToken("5");
		query.setLimit(limitToken);
		
		assertEquals(limitToken, query.getLimit());
	}
	
}
