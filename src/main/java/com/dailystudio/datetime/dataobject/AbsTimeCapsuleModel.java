package com.dailystudio.datetime.dataobject;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dailystudio.app.utils.ArrayUtils;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DatabaseObjectFactory;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.datetime.dataobject.TimeCapsule;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseReader;
import com.dailystudio.datetime.dataobject.TimeCapsuleDatabaseWriter;
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

    public T addObject(Context context, Object... args) {
        Logger.debug("add new object: args = [%s]",
                args);
        if (context == null
                || args == null
                || args.length <= 0) {
            return null;
        }

        if (isObjectExisted(context, args)) {
            Logger.debug("object with args[%s] is already existed",
                    ArrayUtils.arrayToString(args));
            return null;
        }

        TimeCapsuleDatabaseWriter<T> writer =
                new TimeCapsuleDatabaseWriter<>(context, mObjectClass);

        T object = createObject(context, args);

        writer.insert(object);

        return object;
    }

    public T addOrUpdateObject(Context context,
                               Object... args) {
        Logger.debug("add or update object: args = [%s]",
                args);
        if (context == null
                || args == null
                || args.length <= 0) {
            return null;
        }

        TimeCapsuleDatabaseWriter<T> writer =
                new TimeCapsuleDatabaseWriter<>(context, mObjectClass);

        T object = getObject(context, args);
        if (object == null) {
            object = createObject(context, args);

            writer.insert(object);
        } else {
            applyArgsOnObject(object, args);

            writer.update(object);
        }

        return object;
    }

    public boolean isObjectExisted(Context context,
                                   Object... keys) {
        return (getObject(context, keys) != null);
    }

    public T getObject(Context context, Object... keys) {
        if (context == null
                || keys == null
                || keys.length <= 0) {
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

    public List<T> listObjects(Context context) {
        if (context == null) {
            return null;
        }

        TimeCapsuleDatabaseReader<T> reader =
                new TimeCapsuleDatabaseReader<>(context, mObjectClass);

        Query query = new Query(mObjectClass);

        return reader.query(query);
    }

    protected T createObject(Context context, Object... keys) {
        T object = (T)DatabaseObjectFactory.createDatabaseObject
                (mObjectClass, mObjectVersion);

        applyArgsOnObject(object, keys);

        return object;
    }

    abstract protected ExpressionToken objectExistenceToken(Object... keys);
    abstract protected void applyArgsOnObject(T object, Object... keys);

}
