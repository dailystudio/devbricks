package com.dailystudio.app.fragment;

import android.widget.BaseExpandableListAdapter;

import com.dailystudio.app.ui.AbsArrayExpandableListAdapter;
import com.dailystudio.app.ui.ExpandableGroup;
import com.dailystudio.app.ui.ExpandableListData;

public abstract class AbsArrayExpandableListAdapterFragment<Group extends ExpandableGroup<MapKey>, Item, MapKey>
		extends AbsExpandableListAdapterFragment<Group, Item, MapKey> {

	@Override
	protected void bindData(BaseExpandableListAdapter adapter, ExpandableListData<Group, Item, MapKey> data) {
		if (adapter instanceof AbsArrayExpandableListAdapter == false) {
			return;
		}

		((AbsArrayExpandableListAdapter)adapter).setData(data);

		adapter.notifyDataSetChanged();
	}

}
