package com.dailystudio.dataobject.database;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;

import com.dailystudio.test.ActivityTestCase;

public class DatabaseOpenHandlerTest extends ActivityTestCase {
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateDatabaseOpenHandler() {
		DatabaseOpenHandler handler = 
			new DatabaseOpenHandler(mTargetContext, "test.db", 1);
		assertNotNull(handler);
		
		SQLiteDatabase db = null;
		
		db = handler.getReadableDatabase();
		assertNotNull(db);
		assertTrue(db.isOpen());
		db.close();
		
		db = handler.getWritableDatabase();
		assertNotNull(db);
		assertTrue(db.isOpen());
		db.close();
	}
	
	public void testGetVersion() {
		DatabaseOpenHandler handler = null;
		
		handler = new DatabaseOpenHandler(mTargetContext, "test-upgrade.db", 1);
		assertNotNull(handler);
		
		SQLiteDatabase db = null;
		
		String dbPath = null;
		
		db = handler.getWritableDatabase();
		assertNotNull(db);
		dbPath = db.getPath();
		db.close();
		
		assertEquals(1, handler.getNewVersion());
		assertEquals(1, handler.getOldVersion());

		
		handler = new DatabaseOpenHandler(mTargetContext, "test-upgrade.db", 2);
		assertNotNull(handler);

		db = handler.getWritableDatabase();
		db.close();
		
		assertEquals(2, handler.getNewVersion());
		assertEquals(1, handler.getOldVersion());
		
		handler = new DatabaseOpenHandler(mTargetContext, "test-upgrade.db", 3);
		assertNotNull(handler);

		db = handler.getWritableDatabase();
		db.close();
		
		assertEquals(3, handler.getNewVersion());
		assertEquals(2, handler.getOldVersion());
		
		handler = new DatabaseOpenHandler(mTargetContext, "test-upgrade.db", 1);
		assertNotNull(handler);

		db = handler.getWritableDatabase();
		db.close();
		
		assertEquals(1, handler.getNewVersion());
		assertEquals(3, handler.getOldVersion());
		

		File dbFile = new File(dbPath);
		if (dbFile.exists()) {
			dbFile.delete();
		}
	}



}
