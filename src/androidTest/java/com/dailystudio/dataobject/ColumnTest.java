package com.dailystudio.dataobject;

import com.dailystudio.dataobject.query.QueryToken;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class ColumnTest extends AndroidTestCase {
	
	static class InvalidTypeColumn extends Column {
		
		public InvalidTypeColumn(String colName) {
			this(colName, true);
		}
		
		public InvalidTypeColumn(String colName,  boolean allowNull) {
			this(colName, allowNull, false);
		}
		
		public InvalidTypeColumn(String colName, boolean allowNull, boolean isPrimary) {
			super(colName, null, allowNull, isPrimary);
		}

		@Override
		protected void attachValueTo(Intent intent, ContentValues container) {
			
		}

		@Override
		public String convertValueToString(Object value) {
			return null;
		}

		@Override
		Object getValue(ContentValues container) {
			return null;
		}

		@Override
		boolean matchColumnType(Object value) {
			return false;
		}

		@Override
		protected void parseValueFrom(Cursor cursor, ContentValues container) {
		}

		@Override
		void setValue(ContentValues container, Object value) {
		}
		
	}

	static class DummyColumn extends Column {
		
		private static final String TYPE_DUMMY = "DUMMY";
		
		public DummyColumn(String colName) {
			this(colName, true);
		}
		
		public DummyColumn(String colName, boolean allowNull) {
			this(colName, allowNull, false);
		}
		
		public DummyColumn(String colName, boolean allowNull, boolean isPrimary) {
			this(colName, allowNull, isPrimary, VERSION_1);
		}
		
		public DummyColumn(String colName, int version) {
			this(colName, true, version);
		}
		
		public DummyColumn(String colName, boolean allowNull, int version) {
			this(colName, allowNull, false, version);
		}

		public DummyColumn(String colName, boolean allowNull, boolean isPrimary, int version) {
			super(colName, TYPE_DUMMY, allowNull, isPrimary, version);
		}
		
		@Override
		protected void attachValueTo(Intent intent, ContentValues container) {
			
		}

		@Override
		public String convertValueToString(Object value) {
			return null;
		}

		@Override
		Object getValue(ContentValues container) {
			return null;
		}

		@Override
		boolean matchColumnType(Object value) {
			return false;
		}

		@Override
		protected void parseValueFrom(Cursor cursor, ContentValues container) {
		}

		@Override
		void setValue(ContentValues container, Object value) {
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
	
	public void testVersion() {
		Column column = null;
		
		column = new DummyColumn("_id");
		assertEquals(Column.VERSION_1, column.getVerion());
	
		column = new DummyColumn("_id", false);
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new DummyColumn("_id", false, true);
		assertEquals(Column.VERSION_1, column.getVerion());
		
		column = new DummyColumn("_id", 0x5);
		assertEquals(0x5, column.getVerion());
	
		column = new DummyColumn("_id", false, 0x5);
		assertEquals(0x5, column.getVerion());
		
		column = new DummyColumn("_id", false, true, 0x5);
		assertEquals(0x5, column.getVerion());
	}
	
	public void testIsValid() {
		Column column = null;
		
		column = new DummyColumn("_id");
		assertTrue(column.isValid());
	
		column = new DummyColumn(null);
		assertEquals(false, column.isValid());
		
		column = new InvalidTypeColumn("_id");
		assertEquals(false, column.isValid());
		
		column = new DummyColumn(null);
		assertEquals(false, column.isValid());
	}

	public void testEquals() {
		Column columnA = null;
		Column columnB = null;
		
		columnA = new DummyColumn("_id");
		columnB = new DummyColumn("_id");
		assertTrue(columnA.equals(columnB));

		columnA = new DummyColumn("_id", true);
		columnB = new DummyColumn("_id");
		assertTrue(columnA.equals(columnB));
		
		columnA = new DummyColumn("_id", false);
		columnB = new DummyColumn("_id");
		assertEquals(false, columnA.equals(columnB));
		
		columnA = new DummyColumn("_id", true, false);
		columnB = new DummyColumn("_id");
		assertTrue(columnA.equals(columnB));

		columnA = new DummyColumn("_id", true, true);
		columnB = new DummyColumn("_id");
		assertEquals(false, columnA.equals(columnB));

		columnA = new DummyColumn("_id");
		assertEquals(false, columnA.equals(null));

		columnA = new DummyColumn("_id");
		assertEquals(false, columnA.equals("Dummy"));
	}
	
	public void testPLUSOperator() {
		Column column1 = null;
		Column column2 = null;
		
		column1 = new IntegerColumn("intVal1");
		assertNotNull(column1);
		column2 = new IntegerColumn("intVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( intVal1 ) + ( intVal2 ) )"), 
				column1.plus(column2));
		assertEquals(new QueryToken("( ( intVal2 ) + ( intVal1 ) )"), 
				column2.plus(column1));

		column1 = new LongColumn("longVal1");
		assertNotNull(column1);
		column2 = new LongColumn("longVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( longVal1 ) + ( longVal2 ) )"), 
				column1.plus(column2));
		assertEquals(new QueryToken("( ( longVal2 ) + ( longVal1 ) )"), 
				column2.plus(column1));

		column1 = new DoubleColumn("dbVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( dbVal1 ) + ( dbVal2 ) )"), 
				column1.plus(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) + ( dbVal1 ) )"), 
				column2.plus(column1));

		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new TextColumn("textVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( textVal1 ) + ( textVal2 ) )"), 
				column1.plus(column2));
		assertEquals(new QueryToken("( ( textVal2 ) + ( textVal1 ) )"), 
				column2.plus(column1));
		
		column1 = new TimeColumn("timeVal1");
		assertNotNull(column1);
		column2 = new TimeColumn("timeVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( timeVal1 ) + ( timeVal2 ) )"), 
				column1.plus(column2));
		assertEquals(new QueryToken("( ( timeVal2 ) + ( timeVal1 ) )"), 
				column2.plus(column1));
		
		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		
		assertEquals(new QueryToken("( ( textVal1 ) + ( dbVal2 ) )"), 
				column1.plus(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) + ( textVal1 ) )"), 
				column2.plus(column1));
	}
	
	public void testMINUSOperator() {
		Column column1 = null;
		Column column2 = null;
		
		column1 = new IntegerColumn("intVal1");
		assertNotNull(column1);
		column2 = new IntegerColumn("intVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( intVal1 ) - ( intVal2 ) )"), 
				column1.minus(column2));
		assertEquals(new QueryToken("( ( intVal2 ) - ( intVal1 ) )"), 
				column2.minus(column1));

		column1 = new LongColumn("longVal1");
		assertNotNull(column1);
		column2 = new LongColumn("longVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( longVal1 ) - ( longVal2 ) )"), 
				column1.minus(column2));
		assertEquals(new QueryToken("( ( longVal2 ) - ( longVal1 ) )"), 
				column2.minus(column1));

		column1 = new DoubleColumn("dbVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( dbVal1 ) - ( dbVal2 ) )"), 
				column1.minus(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) - ( dbVal1 ) )"), 
				column2.minus(column1));

		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new TextColumn("textVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( textVal1 ) - ( textVal2 ) )"), 
				column1.minus(column2));
		assertEquals(new QueryToken("( ( textVal2 ) - ( textVal1 ) )"), 
				column2.minus(column1));
		
		column1 = new TimeColumn("timeVal1");
		assertNotNull(column1);
		column2 = new TimeColumn("timeVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( timeVal1 ) - ( timeVal2 ) )"), 
				column1.minus(column2));
		assertEquals(new QueryToken("( ( timeVal2 ) - ( timeVal1 ) )"), 
				column2.minus(column1));
		
		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		
		assertEquals(new QueryToken("( ( textVal1 ) - ( dbVal2 ) )"), 
				column1.minus(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) - ( textVal1 ) )"), 
				column2.minus(column1));
	}
	
	public void testMULTIPLEOperator() {
		Column column1 = null;
		Column column2 = null;
		
		column1 = new IntegerColumn("intVal1");
		assertNotNull(column1);
		column2 = new IntegerColumn("intVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( intVal1 ) * ( intVal2 ) )"), 
				column1.multiple(column2));
		assertEquals(new QueryToken("( ( intVal2 ) * ( intVal1 ) )"), 
				column2.multiple(column1));

		column1 = new LongColumn("longVal1");
		assertNotNull(column1);
		column2 = new LongColumn("longVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( longVal1 ) * ( longVal2 ) )"), 
				column1.multiple(column2));
		assertEquals(new QueryToken("( ( longVal2 ) * ( longVal1 ) )"), 
				column2.multiple(column1));

		column1 = new DoubleColumn("dbVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( dbVal1 ) * ( dbVal2 ) )"), 
				column1.multiple(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) * ( dbVal1 ) )"), 
				column2.multiple(column1));

		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new TextColumn("textVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( textVal1 ) * ( textVal2 ) )"), 
				column1.multiple(column2));
		assertEquals(new QueryToken("( ( textVal2 ) * ( textVal1 ) )"), 
				column2.multiple(column1));
		
		column1 = new TimeColumn("timeVal1");
		assertNotNull(column1);
		column2 = new TimeColumn("timeVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( timeVal1 ) * ( timeVal2 ) )"), 
				column1.multiple(column2));
		assertEquals(new QueryToken("( ( timeVal2 ) * ( timeVal1 ) )"), 
				column2.multiple(column1));
		
		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		
		assertEquals(new QueryToken("( ( textVal1 ) * ( dbVal2 ) )"), 
				column1.multiple(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) * ( textVal1 ) )"), 
				column2.multiple(column1));
	}
	
	public void testDIVIDEOperator() {
		Column column1 = null;
		Column column2 = null;
		
		column1 = new IntegerColumn("intVal1");
		assertNotNull(column1);
		column2 = new IntegerColumn("intVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( intVal1 ) / ( intVal2 ) )"), 
				column1.divide(column2));
		assertEquals(new QueryToken("( ( intVal2 ) / ( intVal1 ) )"), 
				column2.divide(column1));

		column1 = new LongColumn("longVal1");
		assertNotNull(column1);
		column2 = new LongColumn("longVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( longVal1 ) / ( longVal2 ) )"), 
				column1.divide(column2));
		assertEquals(new QueryToken("( ( longVal2 ) / ( longVal1 ) )"), 
				column2.divide(column1));

		column1 = new DoubleColumn("dbVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( dbVal1 ) / ( dbVal2 ) )"), 
				column1.divide(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) / ( dbVal1 ) )"), 
				column2.divide(column1));

		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new TextColumn("textVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( textVal1 ) / ( textVal2 ) )"), 
				column1.divide(column2));
		assertEquals(new QueryToken("( ( textVal2 ) / ( textVal1 ) )"), 
				column2.divide(column1));
		
		column1 = new TimeColumn("timeVal1");
		assertNotNull(column1);
		column2 = new TimeColumn("timeVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( timeVal1 ) / ( timeVal2 ) )"), 
				column1.divide(column2));
		assertEquals(new QueryToken("( ( timeVal2 ) / ( timeVal1 ) )"), 
				column2.divide(column1));
		
		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		
		assertEquals(new QueryToken("( ( textVal1 ) / ( dbVal2 ) )"), 
				column1.divide(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) / ( textVal1 ) )"), 
				column2.divide(column1));
	}
	
	public void testMODULOperator() {
		Column column1 = null;
		Column column2 = null;
		
		column1 = new IntegerColumn("intVal1");
		assertNotNull(column1);
		column2 = new IntegerColumn("intVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( intVal1 ) % ( intVal2 ) )"), 
				column1.modulo(column2));
		assertEquals(new QueryToken("( ( intVal2 ) % ( intVal1 ) )"), 
				column2.modulo(column1));

		column1 = new LongColumn("longVal1");
		assertNotNull(column1);
		column2 = new LongColumn("longVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( longVal1 ) % ( longVal2 ) )"), 
				column1.modulo(column2));
		assertEquals(new QueryToken("( ( longVal2 ) % ( longVal1 ) )"), 
				column2.modulo(column1));

		column1 = new DoubleColumn("dbVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( dbVal1 ) % ( dbVal2 ) )"), 
				column1.modulo(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) % ( dbVal1 ) )"), 
				column2.modulo(column1));

		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new TextColumn("textVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( textVal1 ) % ( textVal2 ) )"), 
				column1.modulo(column2));
		assertEquals(new QueryToken("( ( textVal2 ) % ( textVal1 ) )"), 
				column2.modulo(column1));
		
		column1 = new TimeColumn("timeVal1");
		assertNotNull(column1);
		column2 = new TimeColumn("timeVal2");
		assertNotNull(column2);
		assertEquals(new QueryToken("( ( timeVal1 ) % ( timeVal2 ) )"), 
				column1.modulo(column2));
		assertEquals(new QueryToken("( ( timeVal2 ) % ( timeVal1 ) )"), 
				column2.modulo(column1));
		
		column1 = new TextColumn("textVal1");
		assertNotNull(column1);
		column2 = new DoubleColumn("dbVal2");
		assertNotNull(column2);
		
		assertEquals(new QueryToken("( ( textVal1 ) % ( dbVal2 ) )"), 
				column1.modulo(column2));
		assertEquals(new QueryToken("( ( dbVal2 ) % ( textVal1 ) )"), 
				column2.modulo(column1));
	}
	
	public void testGTOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal > 1000"), column.gt(1000));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal > 987654321012345678"), column.gt(987654321012345678l));

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doubleVal > 3.141592653"), column.gt(3.141592653));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal > \'this is \"string\" gt. 123?\'"), column.gt("this is \"string\" gt. 123?"));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.gt(1000));
	}
	
	public void testGTEOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal >= 1000"), column.gte(1000));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal >= 987654321012345678"), column.gte(987654321012345678l));

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doubleVal >= 3.141592653"), column.gte(3.141592653));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal >= \'this is \"string\" gte. 123?\'"), column.gte("this is \"string\" gte. 123?"));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.gte(1000));
	}
	
	public void testLTOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal < 1000"), column.lt(1000));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal < 987654321012345678"), column.lt(987654321012345678l));

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doubleVal < 3.141592653"), column.lt(3.141592653));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal < \'this is \"string\" lt. 123?\'"), column.lt("this is \"string\" lt. 123?"));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.lt(1000));
	}
	
	public void testLTEOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal <= 1000"), column.lte(1000));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal <= 987654321012345678"), column.lte(987654321012345678l));

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doubleVal <= 3.141592653"), column.lte(3.141592653));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal <= \'this is \"string\" lte. 123?\'"), column.lte("this is \"string\" lte. 123?"));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.lte(1000));
	}
	
	public void testEQOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal == 1000"), column.eq(1000));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal == 987654321012345678"), column.eq(987654321012345678l));

		column = new DoubleColumn("doulbeVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doulbeVal == 3.141592653"), column.eq(3.141592653));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal == \'this is \"string\" eq. 123?\'"), column.eq("this is \"string\" eq. 123?"));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.eq(1000));
	}

	public void testNEQOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal != 1000"), column.neq(1000));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal != 987654321012345678"), column.neq(987654321012345678l));

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doubleVal != 3.141592653"), column.neq(3.141592653));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal != \'this is \"string\" neq. 123?\'"), column.neq("this is \"string\" neq. 123?"));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.neq(1000));
	}

	public void testIsNull() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal ISNULL"), 
				column.isNull());

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal ISNULL"), 
				column.isNull());

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doubleVal ISNULL"), 
				column.isNull());

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal ISNULL"), 
				column.isNull());
	}

	public void testNotNull() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal NOTNULL"), 
				column.notNull());

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal NOTNULL"), 
				column.notNull());

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doubleVal NOTNULL"), 
				column.notNull());

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal NOTNULL"), 
				column.notNull());
	}

	public void testINOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("( intVal >= 1000 ) AND ( intVal <= 2000 )"), 
				column.in(1000, 2000));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("( longVal >= 12345678987654321 ) AND ( longVal <= 987654321012345678 )"), 
				column.in(12345678987654321l, 987654321012345678l));

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("( doubleVal >= 1.4142135 ) AND ( doubleVal <= 3.141592653 )"), 
				column.in(1.4142135, 3.141592653));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("( textVal >= \'ABC\' ) AND ( textVal <= \'DEF\' )"), 
				column.in("ABC", "DEF"));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.in(1000, 2000));
	}

	public void testINValuesOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal IN ( 1000, 2000, 3000 )"), 
				column.inValues(new Integer[] {1000, 2000, 3000}));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal IN ( 12345678987654321, 987654321012345678, 1111111111 )"), 
				column.inValues(new Long[] {12345678987654321l, 987654321012345678l, 1111111111l}));

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doubleVal IN ( 1.4142135, 3.141592653, 1.732 )"), 
				column.inValues(new Double[] {1.4142135, 3.141592653, 1.732}));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal IN ( \'ABC\', \'DEF\', \'GHI\', \'JKL\', \'MNO\', \'PQR\', \'STU\', \'VWX\', \'YZ\' )"), 
				column.inValues(new String[] {"ABC", 
						"DEF", "GHI", "JKL", "MNO", 
						"PQR", "STU", "VWX", "YZ"}));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.inValues(new Integer[] {1000, 2000, 3000}));
	}
	
	public void testOUTOFOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("( intVal < 1000 ) OR ( intVal > 2000 )"), 
				column.outOf(1000, 2000));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("( longVal < 12345678987654321 ) OR ( longVal > 987654321012345678 )"), 
				column.outOf(12345678987654321l, 987654321012345678l));

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("( doubleVal < 1.4142135 ) OR ( doubleVal > 3.141592653 )"), 
				column.outOf(1.4142135, 3.141592653));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("( textVal < \'ABC\' ) OR ( textVal > \'DEF\' )"), 
				column.outOf("ABC", "DEF"));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.outOf(1000, 2000));
	}
	
	public void testOUTOFValuesOperator() {
		Column column = null;
		
		column = new IntegerColumn("intVal");
		assertNotNull(column);
		assertEquals(new QueryToken("intVal NOT IN ( 1000, 2000, 3000 )"), 
				column.outOfValues(new Integer[] {1000, 2000, 3000}));

		column = new LongColumn("longVal");
		assertNotNull(column);
		assertEquals(new QueryToken("longVal NOT IN ( 12345678987654321, 987654321012345678, 1111111111 )"), 
				column.outOfValues(new Long[] {12345678987654321l, 987654321012345678l, 1111111111l}));

		column = new DoubleColumn("doubleVal");
		assertNotNull(column);
		assertEquals(new QueryToken("doubleVal NOT IN ( 1.4142135, 3.141592653, 1.732 )"), 
				column.outOfValues(new Double[] {1.4142135, 3.141592653, 1.732}));

		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken("textVal NOT IN ( \'ABC\', \'DEF\', \'GHI\', \'JKL\', \'MNO\', \'PQR\', \'STU\', \'VWX\', \'YZ\' )"), 
				column.outOfValues(new String[] {"ABC", 
						"DEF", "GHI", "JKL", "MNO", 
						"PQR", "STU", "VWX", "YZ"}));
		
		column = new TextColumn("textVal");
		assertNotNull(column);
		assertEquals(new QueryToken(), column.outOfValues(new Integer[] {1000, 2000, 3000}));
	}
	

}
