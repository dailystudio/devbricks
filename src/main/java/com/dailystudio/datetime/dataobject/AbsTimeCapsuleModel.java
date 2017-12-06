package com.dailystudio.datetime.dataobject;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dailystudio.dataobject.DatabaseObjectKeys;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DatabaseObjectFactory;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;

import java.util.List;

/**
 * Created by nanye on 17/11/23.
 */

public abstract class AbsTimeCapsuleModel<T extends TimeCapsule> {

    private Class<T> mObjectClass;
    private int mObjectVersion;

    public AbsTimeCapsuleModel(@NonNull Class<T> objectClass) {
        this(objectClass, DatabaseObject.VERSION_LATEST);
    }

    public AbsTimeCapsuleModel(@NonNull Class<T> objectClass, int version) {
        mObjectClass = objectClass;
        mObjectVersion = version;
    }

    public T addObject(Context context, DatabaseObjectKeys keys) {
        Logger.debug("add new object: keys = [%s]",
                keys);
        if (context == null
                || keys == null) {
            return null;
        }

        if (isObjectExisted(context, keys)) {
            Logger.debug("object with keys[%s] is already existed",
                    keys);
            return null;
        }

        TimeCapsuleDatabaseWriter<T> writer =
                new TimeCapsuleDatabaseWriter<>(context, mObjectClass);

        T object = createObject(keys);

        writer.insert(object);

        return object;
    }

    public T addOrUpdateObject(Context context,
                               DatabaseObjectKeys keys) {
        Logger.debug("add or update object: keys = [%s]",
                keys);
        if (context == null
                || keys == null) {
            return null;
        }

        TimeCapsuleDatabaseWriter<T> writer =
                new TimeCapsuleDatabaseWriter<>(context, mObjectClass);

        T object = getObject(context, keys);
        if (object == null) {
            object = createObject(keys);
            Logger.debug("no object existed, add new one: %s", object);

            writer.insert(object);
        } else {
            applyArgsOnObject(object, keys);
            Logger.debug("object existed, update with new keys: %s", object);

            writer.update(object);
        }

        return object;
    }

    public boolean isObjectExisted(Context context, DatabaseObjectKeys keys) {
        return (getObject(context, keys) != null);
    }

    public T getObject(Context context, DatabaseObjectKeys keys) {
        if (context == null
                || keys == null) {
            return null;
        }

        TimeCapsuleDatabaseReader<T> reader =
                new TimeCapsuleDatabaseReader<>(context, mObjectClass);

        Query query = new Query(mObjectClass);

        ExpressionToken selToken = objectExistenceToken(keys);
        if (selToken == null) {
            return null;
        }

        query.setSelection(selToken);

        return (reader.queryLastOne(query));
    }

    public T deleteObject(Context context, DatabaseObjectKeys keys) {
        T oldObject = getObject(context, keys);

        TimeCapsuleDatabaseWriter<T> writer =
                new TimeCapsuleDatabaseWriter<>(context, mObjectClass);

        writer.delete(oldObject);

        return oldObject;
    }

    public int deleteObjects(Context context, DatabaseObjectKeys keys) {
        if (context == null) {
            return 0;
        }

        Query query = new Query(mObjectClass);

        ExpressionToken token = objectsDeletionToken(keys);
        if (token == null) {
            return 0;
        }

        query.setSelection(token);

        TimeCapsuleDatabaseWriter<T> writer =
                new TimeCapsuleDatabaseWriter<>(context, mObjectClass);

        return writer.delete(query);
    }

    public List<T> listObjects(Context context) {
        if (context == null) {
            return null;
        }

        TimeCapsuleDatabaseReader<T> reader =
                new TimeCapsuleDatabaseReader<>(context, mObjectClass);

        Query query = new Query(mObjectClass);

        return reader.query(query);
    }

    protected T createObject(DatabaseObjectKeys keys) {
        T object = (T)DatabaseObjectFactory.createDatabaseObject
                (mObjectClass, mObjectVersion);

        applyArgsOnObject(object, keys);

        return object;
    }

    protected ExpressionToken objectsDeletionToken(DatabaseObjectKeys keys) {
        return objectExistenceToken(keys);
    }

    abstract protected ExpressionToken objectExistenceToken(DatabaseObjectKeys keys);
    abstract protected void applyArgsOnObject(T object, DatabaseObjectKeys args);

}
