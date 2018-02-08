package com.dailystudio.dataobject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nanye on 17/12/6.
 */

public class DatabaseObjectKeys {

    private Map<String, Object> mValues = new HashMap<>();

    public boolean hasValue(Column column, boolean ignoreType) {
        if (column == null) {
            return false;
        }

        final String key = column.getName();
        if (!mValues.containsKey(key)) {
            return false;
        }

        if (ignoreType) {
            return true;
        }

        final Object value = mValues.get(key);

        return value.getClass() == column.getValueClass();
    }

    public boolean hasValue(Column column) {
        return hasValue(column, false);
    }

    public boolean hasArrayValue(Column column, boolean ignoreType) {
        if (column == null) {
            return false;
        }

        final String key = column.getName();
        if (!mValues.containsKey(key)) {
            return false;
        }

        final Object value = mValues.get(key);
        if (value instanceof Object[]) {
            return false;
        }

        final Object[] array = (Object[]) value;
        if (array.length <= 0) {
            return false;
        }

        if (ignoreType) {
            return true;
        }

        return array[0].getClass() == column.getValueClass();
    }

    public boolean hasArrayValue(Column column) {
        return hasArrayValue(column, false);
    }

    public void putValue(Column column, Object value) {
        if (column == null
                || value == null) {
            return;
        }

        mValues.put(column.getName(), value);
    }

    public Object getValue(Column column) {
        if (column == null) {
            return null;
        }

        return mValues.get(column.getName());
    }

    @Override
    public String toString() {
        return String.format("%s(0x%08x): %s",
                getClass().getSimpleName(),
                hashCode(),
                mValues);
    }

}
