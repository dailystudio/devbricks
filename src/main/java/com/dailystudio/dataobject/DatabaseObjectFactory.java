package com.dailystudio.dataobject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;

import com.dailystudio.factory.Factory;

public class DatabaseObjectFactory extends Factory<DatabaseObject, DatabaseObjectCreationParams> {

	static class ObjectCreationParams {
		
	};
	
	public synchronized static final DatabaseObjectFactory getInstance() {
		return Factory.getInstance(DatabaseObjectFactory.class);
	}
	
	public static final DatabaseObject createDatabaseObject() {
		return createDatabaseObject(null);
	}
	
	public static final DatabaseObject createDatabaseObject(Class<? extends DatabaseObject> klass) {
		return createDatabaseObject(klass, Column.VERSION_LATEST);
	}
	
	public static final DatabaseObject createDatabaseObject(Class<? extends DatabaseObject> klass, int version) {
		DatabaseObjectFactory factory = DatabaseObjectFactory.getInstance();
		if (factory == null) {
			return null;
		}
		
		return factory.createObject(new DatabaseObjectCreationParams(klass, version));
	}
	
	@Override
	protected void initMembers() {
		super.initMembers();
	}
	
	@Override
	protected DatabaseObject newObject(DatabaseObjectCreationParams params) {
		if (params == null) {
			return null;
		}
		
		Class<? extends DatabaseObject> klass = params.objectClass;
		if (klass == null) {
			klass = DatabaseObject.class;
		}
		
		final int version = params.version;
		
//		Logger.debug("klass(%s)", klass.getName());
//		Logger.debug("version(%d)", version);
		
		Object object = null;
		Constructor<?> constructor = null;
			
		try {
			if (version == Column.VERSION_LATEST) {
				constructor = klass.getConstructor(Context.class);
			} else {
				constructor = klass.getConstructor(Context.class, int.class);
			}
		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//				
			constructor = null;
		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//				
			constructor = null;
		} 
			
//		Logger.debug("constructor(%s)", constructor);

		try {
			if (constructor == null) {
				object = klass.newInstance();
			} else {
				constructor.setAccessible(true);
				
				final Context context = getContext();
				if (context != null) {
					if (version == Column.VERSION_LATEST) {
						object = constructor.newInstance(new Object[] { context });
					} else {
						object = constructor.newInstance(new Object[] { context, version });
					}
				}
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			
			object = null;
		} catch (InstantiationException e) {
			e.printStackTrace();
			
			object = null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			
			object = null;
		}
		
		if (object instanceof DatabaseObject == false) {
			return null;
		}

		DatabaseObject dObject = (DatabaseObject)object;
//		Logger.debug("klass(%s), dObject(%s)", klass, dObject);
		
		return dObject;
	}
	
}
