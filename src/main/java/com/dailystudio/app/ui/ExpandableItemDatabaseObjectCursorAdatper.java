package com.dailystudio.app.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.dailystudio.R;
import com.dailystudio.app.widget.SimpleDatabaseObjectCursorAdapter;
import com.dailystudio.dataobject.DatabaseObject;

public abstract class ExpandableItemDatabaseObjectCursorAdatper<T extends DatabaseObject> 
	extends SimpleDatabaseObjectCursorAdapter<T> {
	
	private int mExpendedPosition = -1;
	private LayoutAnimationController mExpandLayoutAnimtion = null;
	
	public ExpandableItemDatabaseObjectCursorAdatper(Context context, 
			int layout, Class<? extends T> klass) {
		this(context, layout, klass, DatabaseObject.VERSION_LATEST);
	}
	
	public ExpandableItemDatabaseObjectCursorAdatper(Context context, 
			int layout, Class<? extends T> klass, int version) {
		super(context, layout, klass, version);
		
		mExpandLayoutAnimtion = onCreateExpandItemLayoutAnimation();
	}
	
	public void expandItem(int position) {
		if (position < 0 || position >= getCount()) {
			return;
		}
		
		mExpendedPosition = position;
		
		notifyDataSetChanged();
	}
	
	public void collapseItem() {
		mExpendedPosition = -1;
		
		notifyDataSetChanged();
	}
	
	
	protected int getExpendedPosition() {
		return mExpendedPosition;
	}
	
	public boolean isItemExpanded(int position) {
		return (position == mExpendedPosition);
	}
	
	@Override
	public Cursor swapCursor(Cursor c) {
		mExpendedPosition = -1;
		return super.swapCursor(c);
	}
	
	@Override
	public void changeCursor(Cursor cursor) {
		mExpendedPosition = -1;
		super.changeCursor(cursor);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor c) {
		bindCollapsedView(view, context, c);
		
		final int position = c.getPosition();
		final boolean dispExpand = (position == getExpendedPosition());
		
		final View expandView = getExpandView(view);
		if (dispExpand) {
			bindExpandedView(expandView, context, c);
			displayExpand(expandView);
		} else {
			hideExpand(expandView);
		}
	}

	protected LayoutAnimationController onCreateExpandItemLayoutAnimation() {
		return AnimationUtils.loadLayoutAnimation(mContext, 
				R.anim.expand_list_item_layout_anim);
	}
	
	protected void displayExpand(View expandView) {
		expandView.setVisibility(View.VISIBLE);
//		expandView.startAnimation(mExpandAnim);
		if (expandView instanceof ViewGroup
				&& mExpandLayoutAnimtion != null) {
			final ViewGroup viewGroup = (ViewGroup)expandView;
			
			if (viewGroup.getLayoutAnimation() == null) {
				viewGroup.setLayoutAnimation(mExpandLayoutAnimtion);
			}
			
			viewGroup.scheduleLayoutAnimation();
		}
	}

	protected void hideExpand(View expandView) {
		if (expandView == null) {
			return;
		}

		expandView.setVisibility(View.GONE);
	}

	abstract protected View getExpandView(View itemView);

	abstract protected void bindExpandedView(View expandView, 
			Context context, Cursor c);
		
	abstract protected void bindCollapsedView(View view, 
			Context context, Cursor c);


}
