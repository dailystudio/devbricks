package com.dailystudio.app.utils;

import java.lang.reflect.Field;

import com.dailystudio.development.Logger;

public class ReflectionUtils {

    public static Field getField(Class<?> clazz, String fieldName) {
        Class<?> tmpClass = clazz;
        do {
            try {
                Field f = tmpClass.getDeclaredField(fieldName);
                return f;
            } catch (NoSuchFieldException e) {
                tmpClass = tmpClass.getSuperclass();
            }
        } while (tmpClass != null);

        throw new RuntimeException("Field '" + fieldName
                + "' not found on class " + clazz);
    }

    public static Object dumpFieldInObject(Object o, String filed) {
        Object filedVal = null;

        try {
            Field f = getField(o.getClass(), filed);
            f.setAccessible(true);

            filedVal = f.get(o);
        } catch (IllegalArgumentException e) {
            Logger.warnning("could not call %s in object(%s): %s",
                    filed, o, e.toString());

            filedVal = null;
        } catch (IllegalAccessException e) {
            Logger.warnning("could not access %s in object(%s): %s",
                    filed, o, e.toString());

            filedVal = null;
        }

        return filedVal;
    }

}
