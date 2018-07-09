package com.dailystudio.datetime.dataobject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dailystudio.dataobject.DatabaseObjectKeys;
import com.dailystudio.dataobject.DatabaseObject;
import com.dailystudio.dataobject.DatabaseObjectFactory;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.dataobject.query.Query;
import com.dailystudio.development.Logger;

import java.util.List;

/**
 * Created by nanye on 17/11/23.
 */

public abstract class AbsTimeCapsuleModel<T extends TimeCapsule> {

    protected String TOKEN_TYPE_EXISTENCE = "existence";
    protected String TOKEN_TYPE_DELETION = "deletion";

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

        ExpressionToken selToken = objectsToken(keys, TOKEN_TYPE_EXISTENCE);
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

    public int deleteObjects(Context context) {
        return deleteObjects(context, null);
    }

    public int deleteObjects(Context context, DatabaseObjectKeys keys) {
        if (context == null) {
            return 0;
        }

        Query query = new Query(mObjectClass);

        if (keys != null) {
            ExpressionToken token = objectsToken(keys, TOKEN_TYPE_DELETION);
            if (token == null) {
                return 0;
            }

            query.setSelection(token);
        }

        TimeCapsuleDatabaseWriter<T> writer =
                new TimeCapsuleDatabaseWriter<>(context, mObjectClass);

        return writer.delete(query);
    }

    public List<T> listObjects(Context context) {
        return listObjects(context, null, null);
    }

    public List<T> listObjects(Context context,
                               DatabaseObjectKeys keys,
                               String listTokenType) {
        if (context == null) {
            return null;
        }

        TimeCapsuleDatabaseReader<T> reader =
                new TimeCapsuleDatabaseReader<>(context, mObjectClass);

        Query query = new Query(mObjectClass);

        if (keys != null
                && !TextUtils.isEmpty(listTokenType)) {
            ExpressionToken token = objectsToken(keys, listTokenType);
            if (token == null) {
                return null;
            }

            query.setSelection(token);
        }

        OrderingToken orderByToken = orderByToken(listTokenType);
        if (orderByToken != null) {
            query.setOrderBy(orderByToken);
        }

        OrderingToken groupByToken = groupByToken(listTokenType);
        if (groupByToken != null) {
            query.setGroupBy(groupByToken);
        }

        return reader.query(query);
    }

    protected T createObject(DatabaseObjectKeys keys) {
        T object = (T)DatabaseObjectFactory.createDatabaseObject
                (mObjectClass, mObjectVersion);

        applyArgsOnObject(object, keys);

        return object;
    }

    protected ExpressionToken objectsToken(DatabaseObjectKeys keys,
                                           @NonNull String tokenType) {
        if (TOKEN_TYPE_EXISTENCE.equals(tokenType)) {
            return objectExistenceToken(keys);
        } else if (TOKEN_TYPE_DELETION.equals(tokenType)) {
            return objectsDeletionToken(keys);
        }

        return null;
    }

    protected OrderingToken orderByToken(@NonNull String tokenType) {
        return TimeCapsule.COLUMN_TIME.orderByDescending();
    }

    protected OrderingToken groupByToken(@NonNull String tokenType) {
        return null;
    }

    protected ExpressionToken objectsDeletionToken(DatabaseObjectKeys keys) {
        return objectExistenceToken(keys);
    }

    abstract protected ExpressionToken objectExistenceToken(DatabaseObjectKeys keys);
    abstract protected void applyArgsOnObject(T object, DatabaseObjectKeys keys);

}
