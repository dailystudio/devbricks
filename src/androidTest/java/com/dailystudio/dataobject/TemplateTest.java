package com.dailystudio.dataobject;

import java.util.ArrayList;
import java.util.List;

import com.dailystudio.dataobject.Column;
import com.dailystudio.dataobject.Template;
import com.dailystudio.test.Asserts;

import android.test.AndroidTestCase;

public class TemplateTest extends AndroidTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateATemplate() {
		Template templ = new Template();
		assertNotNull(templ);
		
		List<Column> columns = templ.listColumns();
		assertNotNull(columns);
		assertEquals(0, columns.size());
	}
	
	public void testAddColumn() {
		Template templ = new Template();
		assertNotNull(templ);
		
		Column column = new IntegerColumn("_id");
		assertNotNull(column);
		templ.addColumn(column);
		
		List<Column> columns = templ.listColumns();
		assertNotNull(columns);
		assertEquals(1, columns.size());
		
		Column actualColumn = columns.get(0);
		assertEquals(column, actualColumn);
	}

	public void testRemoveColumn() {
		Template templ = new Template();
		assertNotNull(templ);
		
		Column column = new IntegerColumn("_id");
		assertNotNull(column);
		templ.addColumn(column);
		
		List<Column> columns = templ.listColumns();
		assertNotNull(columns);
		assertEquals(1, columns.size());
		
		Column actualColumn = columns.get(0);
		assertEquals(column, actualColumn);
		
		templ.removeColumn(column);
		columns = templ.listColumns();
		assertEquals(0, columns.size());
	}

	public void testAddColumns() {
		Template templ = new Template();
		assertNotNull(templ);
		
		Column col1 = new IntegerColumn("_id");
		Column col2 = new LongColumn("time");
		Column col3 = new DoubleColumn("fp");
		Column col4 = new TextColumn("dummy");
		assertNotNull(col1);
		assertNotNull(col2);
		assertNotNull(col3);
		assertNotNull(col4);
		
		Column[] array = { col1, col2, col3, col4 }; 
		templ.addColumns(array);
		
		List<Column> columns = templ.listColumns();
		assertNotNull(columns);
		assertEquals(4, columns.size());
		
		List<Column> expected = new ArrayList<Column>();
		expected.add(col1);
		expected.add(col2);
		expected.add(col3);
		expected.add(col4);
		
		Asserts.assertEquals(expected, columns);
	}
	
	public void testGetColumn() {
		Template templ = new Template();
		assertNotNull(templ);
		
		Column col1 = new IntegerColumn("_id");
		Column col2 = new LongColumn("time");
		Column col3 = new DoubleColumn("fp");
		Column col4 = new TextColumn("dummy");
		assertNotNull(col1);
		assertNotNull(col2);
		assertNotNull(col3);
		assertNotNull(col4);
		
		templ.addColumn(col1);
		templ.addColumn(col2);
		templ.addColumn(col3);
		templ.addColumn(col4);
		
		Column result = null;
		result = templ.getColumn("_id");
		assertEquals(col1, result);
		result = templ.getColumn("time");
		assertEquals(col2, result);
		result = templ.getColumn("fp");
		assertEquals(col3, result);
		result = templ.getColumn("dummy");
		assertEquals(col4, result);
		result = templ.getColumn(null);
		assertNull(result);
		result = templ.getColumn("test");
		assertNull(result);
	}

	public void testListColumns() {
		Template templ = new Template();
		assertNotNull(templ);
		
		Column col1 = new IntegerColumn("_id");
		Column col2 = new LongColumn("time");
		Column col3 = new DoubleColumn("fp");
		Column col4 = new TextColumn("dummy");
		assertNotNull(col1);
		assertNotNull(col2);
		assertNotNull(col3);
		assertNotNull(col4);
		
		templ.addColumn(col1);
		templ.addColumn(col2);
		templ.addColumn(col3);
		templ.addColumn(col4);
		
		List<Column> columns = templ.listColumns();
		assertNotNull(columns);
		assertEquals(4, columns.size());
		
		List<Column> expected = new ArrayList<Column>();
		expected.add(col1);
		expected.add(col2);
		expected.add(col3);
		expected.add(col4);
		
		Asserts.assertEquals(expected, columns);
	}
	
	public void testContainsColumns() {
		Template templ = new Template();
		assertNotNull(templ);
		
		Column col1 = new IntegerColumn("col1");
		Column col2 = new LongColumn("col2");
		Column col3 = new DoubleColumn("col3");
		Column col4 = new TextColumn("col4");
		assertNotNull(col1);
		assertNotNull(col2);
		assertNotNull(col3);
		assertNotNull(col4);
		
		templ.addColumn(col1);
		templ.addColumn(col2);
		templ.addColumn(col3);
		templ.addColumn(col4);

		assertEquals(true, templ.containsColumn("col1"));
		assertEquals(true, templ.containsColumn("col2"));
		assertEquals(true, templ.containsColumn("col3"));
		assertEquals(true, templ.containsColumn("col4"));
		
		assertEquals(true, templ.containsColumn(col1));
		assertEquals(true, templ.containsColumn(col2));
		assertEquals(true, templ.containsColumn(col3));
		assertEquals(true, templ.containsColumn(col4));
		
		Column col5 = new TextColumn("col5");
		assertEquals(false, templ.containsColumn(col5));
		assertEquals(false, templ.containsColumn("col5"));

		assertEquals(false, templ.containsColumn((Column)null));
		assertEquals(false, templ.containsColumn((String)null));
	}

}
