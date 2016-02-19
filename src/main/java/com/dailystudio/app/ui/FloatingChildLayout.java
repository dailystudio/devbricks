package com.dailystudio.app.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FloatingChildLayout extends FrameLayout {
	
    private int mFixedTopPosition = -1;
    private boolean mAutoAdjustPosition = true;

    private View mChild;
    private Rect mTargetScreen = new Rect();

    public FloatingChildLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
    	if (getChildCount() <= 0) {
    		return;
    	}
        
    	mChild = getChildAt(0);
        mChild.setDuplicateParentStateEnabled(true);
    }

    public View getChild() {
        return mChild;
    }

    /**
     * Set {@link Rect} in screen coordinates that {@link #getChild()} should be
     * centered around.
     */
    public void setChildTargetScreen(Rect targetScreen) {
        mTargetScreen = targetScreen;
        requestLayout();
    }

    /**
     * Return {@link #mTargetScreen} in local window coordinates, taking any
     * decor insets into account.
     */
    private Rect getTargetInWindow() {
        final Rect windowScreen = new Rect();
        getWindowVisibleDisplayFrame(windowScreen);

        final Rect target = new Rect(mTargetScreen);
        target.offset(-windowScreen.left, -windowScreen.top);
        target.offset(-getPaddingLeft(), -getPaddingTop());

        return target;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final View child = mChild;
        if (child == null) {
        	return;
        }
        
        int gravity = Gravity.CENTER;
        
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp instanceof LayoutParams) {
        	gravity = ((LayoutParams)lp).gravity;
        }
        
        final Rect target = getTargetInWindow();
//        Logger.debug("gravity = %d, target = %s", gravity, target);

        final int availabelWidth = getWidth() - (getPaddingLeft() + getPaddingRight());
        final int availabelHeight = getHeight() - (getPaddingTop() + getPaddingBottom());
        final int childWidth = child.getMeasuredWidth();
        final int childHeight = child.getMeasuredHeight();
//        Logger.debug("availabelWidth = %d, availabelHeight = %d", availabelWidth, availabelHeight);
//        Logger.debug("childWidth = %d, childHeight = %d", childWidth, childHeight);

        if (mAutoAdjustPosition) {
        	gravity = Gravity.CENTER;
        	
        	final int leftSpace = target.left - getPaddingLeft();
        	final int rightSpace = (getRight() - getPaddingRight()) - target.right;
        	final int topSpace = target.top - getPaddingTop();
        	final int bottomSpace = (getBottom() - getPaddingBottom()) - target.bottom;
        	
//        	Logger.debug("leftSpace = %d, rightSpace = %d", leftSpace, rightSpace);
//        	Logger.debug("topSpace = %d, bottomSpace = %d", topSpace, bottomSpace);
        	
        	if (leftSpace >= rightSpace) {
        		gravity |= Gravity.LEFT;
        	} else {
        		gravity |= Gravity.RIGHT;
        	}
        	
        	if (topSpace > bottomSpace) {
        		gravity |= Gravity.TOP;
        	} else {
        		gravity |= Gravity.BOTTOM;
        	}
        	
//            Logger.debug("AUTO POSITION: gravity = %d[h: %d, v: %d", 
//            		gravity,
//            		gravity & Gravity.HORIZONTAL_GRAVITY_MASK,
//            		gravity & Gravity.VERTICAL_GRAVITY_MASK);
        }
        
        if (mFixedTopPosition != -1) {
            // Horizontally centered, vertically fixed position
            final int childLeft = (getWidth() - childWidth) / 2;
            final int childTop = mFixedTopPosition;
            layoutChild(child, childLeft, childTop);
        } else {
            int childLeft = target.centerX() - (childWidth / 2);
            int childTop = target.centerY() - (childHeight / 2);
            
            switch (gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            	case Gravity.LEFT:
            		childLeft = target.left - childWidth;
//                    Logger.debug("HANDLE LEFT: childLeft[%d] = target.left[%d] - childWidht[%d]",
//                    		childLeft, target.left, childWidth);
            		break;
            		
            	case Gravity.RIGHT:
            		childLeft = target.right; 
//                    Logger.debug("HANDLE RIGHT: childLeft[%d] = target.right[%d]",
//                    		childLeft, target.right);
            		break;
            }

            switch (gravity & Gravity.VERTICAL_GRAVITY_MASK) {
	        	case Gravity.TOP:
	        		childTop = target.top - childHeight;
//                    Logger.debug("HANDLE TOP: childTop[%d] = target.top[%d] - childHeight[%d]",
//                    		childTop, target.top, childHeight);
	        		break;
	        	case Gravity.BOTTOM:
                    childTop = target.bottom; 
//                    Logger.debug("HANDLE BOTTOM: childTop[%d] = target.bottom[%d]",
//                    		childTop, target.bottom);
	        		break;
            }

            // when child is outside bounds, nudge back inside
            int clampedChildLeft = clampDimension(childLeft, childWidth, 
            		availabelWidth, getPaddingLeft());
            int clampedChildTop = clampDimension(childTop, childHeight, 
            		availabelHeight, getPaddingTop());
//            Logger.debug("left = %d, clampedChildLeft = %d", childLeft, clampedChildLeft);
//            Logger.debug("top = %d, clampedChildTop = %d", childTop, clampedChildTop);
            
            layoutChild(child, getPaddingLeft() + clampedChildLeft, 
            		getPaddingTop() + clampedChildTop);
        }
    }

    private static int clampDimension(int value, int size, int max, int minValue) {
        // when larger than bounds, just center
        if (size > max) {
            return (max - size) / 2;
        }

        // clamp to bounds
        return Math.min(Math.max(value, 0), max - size);
    }

    private static void layoutChild(View child, int left, int top) {
        child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
    }

    private OnTouchListener mOutsideTouchListener;

    public void setOnOutsideTouchListener(OnTouchListener listener) {
        mOutsideTouchListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // at this point, touch wasn't handled by child view; assume outside
        if (mOutsideTouchListener != null) {
            return mOutsideTouchListener.onTouch(this, event);
        }
        return false;
    }
    
}
