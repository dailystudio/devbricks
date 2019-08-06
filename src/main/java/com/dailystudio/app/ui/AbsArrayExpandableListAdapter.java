package com.dailystudio.app.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanye on 16/9/28.
 */

public abstract class AbsArrayExpandableListAdapter<Group extends ExpandableGroup, Item, MapKey> extends BaseExpandableListAdapter {

    private Context mContext;

    private List<Group> mGroups;
    private List<Item> mItems;
    private Map<MapKey, List<Item>> mItemsMap;


    private final Object mLock = new Object();

    protected LayoutInflater mLayoutInflater;


    public AbsArrayExpandableListAdapter(Context context) {
        this(context, new ArrayList<Group>(),
                new ArrayList<Item>(), new HashMap<MapKey, List<Item>>());
    }

    public AbsArrayExpandableListAdapter(Context context,
                                         @NonNull List<Group> groups,
                                         @NonNull List<Item> items,
                                         @NonNull Map<MapKey, List<Item>> itemsMap) {
        mContext = context;

        mGroups = groups;
        mItems = items;
        mItemsMap = itemsMap;

        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(ExpandableListData data) {
        synchronized (mLock) {
            mGroups.clear();
            if (data != null && data.groups != null) {
                mGroups.addAll(data.groups);
            }

            mItems.clear();
            if (data != null && data.items != null) {
                mItems.addAll(data.items);
            }

            mItemsMap.clear();
            if (data != null && data.itemsMap != null) {
                mItemsMap.putAll(data.itemsMap);
            }
        }
    }

    @Override
    public int getGroupCount() {
        return (mGroups == null ? 0 : mGroups.size());
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mGroups == null || mItemsMap == null) {
            return 0;
        }

        if (groupPosition < 0 || groupPosition >= mGroups.size()) {
            return 0;
        }

        final Group g = mGroups.get(groupPosition);
        if (g == null) {
            return 0;
        }

        List<Item> items = mItemsMap.get(g.getGroupKey());

        return (items == null ? 0 : items.size());
    }

    @Override
    public Group getGroup(int groupPosition) {
        if (mGroups == null) {
            return null;
        }

        if (groupPosition < 0 || groupPosition >= mGroups.size()) {
            return null;
        }

        return mGroups.get(groupPosition);
    }

    @Override
    public Item getChild(int groupPosition, int childPosition) {
        if (mGroups == null || mItemsMap == null) {
            return null;
        }

        if (groupPosition < 0 || groupPosition >= mGroups.size()) {
            return null;
        }

        final Group g = mGroups.get(groupPosition);

        List<Item> items = mItemsMap.get(g.getGroupKey());
        if (items == null) {
            return null;
        }

        if (childPosition < 0 || childPosition >= items.size()) {
            return null;
        }

        return items.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return (groupPosition << 32 | childPosition);
    }

    public Context getContext() {
        return mContext;
    }

}
