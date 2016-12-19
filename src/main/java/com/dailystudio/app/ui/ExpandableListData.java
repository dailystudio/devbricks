package com.dailystudio.app.ui;

import java.util.List;
import java.util.Map;

/**
 * Created by nanye on 16/9/28.
 */

public class ExpandableListData<Group extends ExpandableGroup<MapKey>, Item, MapKey> {

    public List<Group> groups;
    public List<Item> items;

    public Map<MapKey, List<Item>> itemsMap;

    public int getGroupsSize() {
        return (groups == null ? 0 : groups.size());
    }

    public int getItemsSize() {
        return (items == null ? 0 : items.size());
    }

    @Override
    public String toString() {
        return String.format("%s(0x%08x): groups: [%s], items: [%s], map: [%s]",
                getClass().getSimpleName(),
                hashCode(),
                groups,
                items,
                itemsMap);
    }
}
