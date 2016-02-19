package com.dailystudio.dataobject.database;

import com.dailystudio.test.Asserts;

import android.test.AndroidTestCase;

public class SortOrderEncoderTest extends AndroidTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testEncode() {
		String actual = SortOrderEncoder.encode(
				"intVal, longVal", 
				"intVal < 10",
				"intVal ASC, longVal DESC",
				"10 offset 1");
		String expected = 
			"g: intVal, longVal\n" +
			"h: intVal < 10\n" +
			"o: intVal ASC, longVal DESC\n" +
			"l: 10 offset 1\n";
		
		assertEquals(expected, actual);
	}
	
	public void testDecode() {
		String encode = null;
		String[] actual = null;
		String[] expected = null;
		
		encode = SortOrderEncoder.encode(
				"intVal, longVal", 
				"intVal < 10",
				"intVal ASC, longVal DESC",
				"10 offset 1");
		actual = SortOrderEncoder.decode(encode);
		expected = new String[] {
			"intVal, longVal",
			"intVal < 10",
			"intVal ASC, longVal DESC",
			"10 offset 1"
		};
		Asserts.assertEquals(expected, actual);
		
		encode = SortOrderEncoder.encode(
				null, 
				"intVal < 10",
				"intVal ASC, longVal DESC",
				"10 offset 1");
		actual = SortOrderEncoder.decode(encode);
		expected = new String[] {
			null,
			"intVal < 10",
			"intVal ASC, longVal DESC",
			"10 offset 1"
		};
		Asserts.assertEquals(expected, actual);

		encode = SortOrderEncoder.encode(
				"intVal, longVal", 
				null,
				"intVal ASC, longVal DESC",
				"10 offset 1");
		actual = SortOrderEncoder.decode(encode);
		expected = new String[] {
			"intVal, longVal",
			null,
			"intVal ASC, longVal DESC",
			"10 offset 1"
		};
		Asserts.assertEquals(expected, actual);
		
		encode = SortOrderEncoder.encode(
				"intVal, longVal", 
				"intVal < 10",
				null,
				"10 offset 1");
		actual = SortOrderEncoder.decode(encode);
		expected = new String[] {
			"intVal, longVal",
			"intVal < 10",
			null,
			"10 offset 1"
		};
		Asserts.assertEquals(expected, actual);
		
		encode = SortOrderEncoder.encode(
				"intVal, longVal", 
				"intVal < 10",
				"intVal ASC, longVal DESC",
				null);
		actual = SortOrderEncoder.decode(encode);
		expected = new String[] {
			"intVal, longVal",
			"intVal < 10",
			"intVal ASC, longVal DESC",
			null
		};
		Asserts.assertEquals(expected, actual);
		
		encode = SortOrderEncoder.encode(
				null, 
				"intVal < 10",
				"intVal ASC, longVal DESC",
				null);
		actual = SortOrderEncoder.decode(encode);
		expected = new String[] {
			null,
			"intVal < 10",
			"intVal ASC, longVal DESC",
			null
		};
		Asserts.assertEquals(expected, actual);
		
		encode = SortOrderEncoder.encode(
				null, 
				null,
				null,
				null);
		actual = SortOrderEncoder.decode(encode);
		expected = new String[] {
			null,
			null,
			null,
			null,
		};
		Asserts.assertEquals(expected, actual);
		
		encode = SortOrderEncoder.encode(
				"intVal, \n\nlongVal", 
				"intVal \n\n< 10",
				"intVal\n\n\n\n ASC, longVal DESC",
				"10 \n\noffset 1");
		actual = SortOrderEncoder.decode(encode);
		expected = new String[] {
			"intVal, longVal",
			"intVal < 10",
			"intVal ASC, longVal DESC",
			"10 offset 1"
		};
		Asserts.assertEquals(expected, actual);

		encode = SortOrderEncoder.encode(
				"intVal, \\\n\nlongVal", 
				"intVal \\n\n< 10",
				"intVal\n\n\n\n ASC, longVal DESC",
				"10 \n\noffset 1");
		actual = SortOrderEncoder.decode(encode);
		expected = new String[] {
			"intVal, \\longVal",
			"intVal \\n< 10",
			"intVal ASC, longVal DESC",
			"10 offset 1"
		};
		Asserts.assertEquals(expected, actual);
	}
	
}
