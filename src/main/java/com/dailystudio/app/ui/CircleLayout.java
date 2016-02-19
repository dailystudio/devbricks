package com.dailystudio.app.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class CircleLayout extends FrameLayout {
	
	private static final float PI = 3.1415926f;
	private static final float DEFALT_RADIUS_SCALE = 3f;
	
	private static class LayoutInfo {
		
		public Rect boundRect;
		public int centerX;
		public int centerY;
		
		public int[] childRadius;
		public int[] centerDistances;
		public float[] childAngles;
		public int[][] childCoords;
		
		public String toString() {
			StringBuffer anglesString = new StringBuffer("{ ");
			StringBuffer radiiString = new StringBuffer("{ ");
			StringBuffer coordsString = new StringBuffer("{ ");
			int i;
			
			for (i = 0; i < childRadius.length; i++) {
				anglesString.append(String.format("[arc: %3.1fPI, ang: %3.1f]",  
						childAngles[i] / PI,
						childAngles[i] / PI * 180));
				anglesString.append((i == childRadius.length - 1 ? "" : ", "));
				
				radiiString.append(childRadius[i]);
				radiiString.append((i == childRadius.length - 1 ? "" : ", "));
				
				coordsString.append("[");
				coordsString.append(childCoords[i][0]);
				coordsString.append(", ");
				coordsString.append(childCoords[i][1]);
				coordsString.append("]");
				coordsString.append((i == childCoords.length - 1 ? "" : ", "));
			}
			
			radiiString.append(" }");
			
			return String.format("%s:\n%-15s: %s\n%-15s: %d\n%-15s: %d\n%-15s: %s\n%-15s: %s",
					this.getClass().getSimpleName(),
					"boundRect", boundRect,
					"centerX", centerX,
					"centerY", centerY,
					"childAngles", anglesString.toString(),
					"childRadius", radiiString.toString(),
					"childCoords", coordsString.toString());
		}
		
	};
	
	
	protected Context mContext;
	
	private float mStartAngle = (- PI / 2);
	private float mValidAreaStart = PI / 2;
	private float mValidAreaEnd = PI / 2;
	private Rect mVisibleRect = new Rect();;
	private boolean mReversed = false;
	
	private boolean mAutoAdjustment = false;
	
	private LayoutInfo mLayoutInfo;
	
	public CircleLayout(Context context) {
		this(context, null);
	}
	
	public CircleLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mContext = context;
		
		initMembers();
	}
	
	private void initMembers() {

	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		final int count = getChildCount();
		int maxRadius = 0;
		int radius = 0;
		View child = null;
		
		for (int i = 0; i < count; i++) {
			child = getChildAt(i);
			radius = (int) Math.sqrt(
					(Math.pow(child.getMeasuredWidth() / 2, 2) + 
					Math.pow(child.getMeasuredHeight() / 2, 2)));
			
/*			Log.d(this.getClass().getSimpleName(),
					String.format("onMeasure(): child(%s), radius(%d)",
							child, radius));
*/			
			if (radius > maxRadius) {
				maxRadius = radius;
			}
		}
		
/*		Log.d(this.getClass().getSimpleName(),
				String.format("onMeasure(): maxRadius(%d)",
						maxRadius));
*/		
		setMeasuredDimension(resolveSize((int)((maxRadius * DEFALT_RADIUS_SCALE) * 2), widthMeasureSpec), 
				resolveSize((int)((maxRadius * DEFALT_RADIUS_SCALE) * 2), heightMeasureSpec));
/*		Log.d(this.getClass().getSimpleName(),
				String.format("onMeasure(): width(%d), height(%d)",
						getMeasuredWidth(), getMeasuredHeight()));
*/		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
/*		Log.d(this.getClass().getSimpleName(),
				String.format("onLayout(): changed(%s), l(%d), t(%d), r(%d), b(%d)",
						changed, l, t, r, b));
*/		
		if (mAutoAdjustment) {
			checkValidArea();
		}
		
		final int count = getChildCount();
		if (count <= 0) {
			return;
		}
		
		mLayoutInfo = fillLayoutInfo(mStartAngle);
		if (mLayoutInfo == null) {
			return;
		}
		
		View child = null;
		for (int i = 0; i < count; i++) {
			child = getChildAt(i);
			
			if (child != null) {
/*				if (child instanceof StarlikeNodeView) {
					((StarlikeNodeView)child).setLayoutInfo(
							mLayoutInfo.centerX, 
							mLayoutInfo.centerY,
							mLayoutInfo.childCoords[i][0],
							mLayoutInfo.childCoords[i][1]);
				}
*/				
				child.layout(
						mLayoutInfo.childCoords[i][0] - child.getMeasuredWidth() / 2,
						mLayoutInfo.childCoords[i][1] - child.getMeasuredHeight() / 2,
						mLayoutInfo.childCoords[i][0] + child.getMeasuredWidth() / 2,
						mLayoutInfo.childCoords[i][1] + child.getMeasuredHeight() / 2);
			}
		}
	}
	
	private void checkValidArea() {
		final int count = getChildCount();
		if (count < 0) {
			return;
		}
		
		getLocalVisibleRect(mVisibleRect);
/*		Log.d(this.getClass().getSimpleName(),
				String.format("onLayout(): localVisibleRect(%s)",
						mVisibleRect));
*/
		int width = getWidth();
		int height = getHeight();
		
		int coordFirst[] = new int[]{0, 0};
		int coordLast[] = new int[]{0, 0};
		View childFirst =  getChildAt(0);
		View childLast =  getChildAt(count - 1);
		
		/*
		 * Case		First Node		Last Node										Condition			
		 *			x				y					x			y				l	t	r	b
		 *	1		r - c0.w / 2	t + c0.h / 2	l + cl.w / 2	b - cl.h / 2	>0	>0	w	h
		 *	2		l + c0.w / 2	t + c0.h / 2	l + cl.w / 2	b - cl.h / 2	>0	0	w	h
		 *	3		l + c0.w / 2	t + c0.h / 2	r - cl.w / 2	b - cl.h / 2	>0	0	w	<h
		 *	4		l + c0.w / 2	b - c0.h / 2	r - cl.w / 2	b - cl.h / 2	0	0	w	<h
		 *	5		r - c0.w / 2	t + c0.h / 2	l + cl.w / 2	b - cl.h / 2	0	0	<w	<h
		 *	6		r - c0.w / 2	t + c0.h / 2	r - cl.w / 2	b - cl.h / 2	0	0	<w	h
		 *	7		l + c0.w / 2	t + c0.h / 2	r - cl.w / 2	b - cl.h / 2	0	>0	<w	h
		 * 	8		r - c0.w / 2	t + c0.h / 2	l + cl.w / 2	t + cl.h / 2	0	>0	w	h
		 */
		if (mVisibleRect.left > 0 && mVisibleRect.top > 0 &&
				mVisibleRect.right == width && mVisibleRect.bottom == height ) {
			/* case 1 */
			coordFirst[0] = mVisibleRect.right - childFirst.getWidth() / 2;
			coordFirst[1] = mVisibleRect.top + childFirst.getHeight() / 2;
			coordLast[0] = mVisibleRect.left + childLast.getWidth() / 2;
			coordLast[1] = mVisibleRect.bottom - childLast.getHeight() / 2;
		}
		
		if (mVisibleRect.left > 0 && mVisibleRect.top == 0 &&
				mVisibleRect.right == width && mVisibleRect.bottom == height ) {
			/* case 2 */
			coordFirst[0] = mVisibleRect.left + childFirst.getWidth() / 2;
			coordFirst[1] = mVisibleRect.top + childFirst.getHeight() / 2;
			coordLast[0] = mVisibleRect.left + childLast.getWidth() / 2;
			coordLast[1] = mVisibleRect.bottom - childLast.getHeight() / 2;
		}
		
		if (mVisibleRect.left > 0 && mVisibleRect.top == 0 &&
				mVisibleRect.right == width && mVisibleRect.bottom < height ) {
			/* case 3 */
			coordFirst[0] = mVisibleRect.left + childFirst.getWidth() / 2;
			coordFirst[1] = mVisibleRect.top + childFirst.getHeight() / 2;
			coordLast[0] = mVisibleRect.right - childLast.getWidth() / 2;
			coordLast[1] = mVisibleRect.bottom - childLast.getHeight() / 2;
		}
		
		if (mVisibleRect.left == 0 && mVisibleRect.top == 0 &&
				mVisibleRect.right == width && mVisibleRect.bottom < height ) {
			/* case 4 */
			coordFirst[0] = mVisibleRect.left + childFirst.getWidth() / 2;
			coordFirst[1] = mVisibleRect.bottom - childFirst.getHeight() / 2;
			coordLast[0] = mVisibleRect.right - childLast.getWidth() / 2;
			coordLast[1] = mVisibleRect.bottom - childLast.getHeight() / 2;
		}
		
		if (mVisibleRect.left == 0 && mVisibleRect.top == 0 &&
				mVisibleRect.right < width && mVisibleRect.bottom < height ) {
			/* case 5 == 1 */
			coordFirst[0] = mVisibleRect.right - childFirst.getWidth() / 2;
			coordFirst[1] = mVisibleRect.top + childFirst.getHeight() / 2;
			coordLast[0] = mVisibleRect.left + childLast.getWidth() / 2;
			coordLast[1] = mVisibleRect.bottom - childLast.getHeight() / 2;
		}
		
		if (mVisibleRect.left == 0 && mVisibleRect.top == 0 &&
				mVisibleRect.right < width && mVisibleRect.bottom == height ) {
			/* case 6 */
			coordFirst[0] = mVisibleRect.right - childFirst.getWidth() / 2;
			coordFirst[1] = mVisibleRect.top + childFirst.getHeight() / 2;
			coordLast[0] = mVisibleRect.right - childLast.getWidth() / 2;
			coordLast[1] = mVisibleRect.bottom - childLast.getHeight() / 2;
		}
		
		if (mVisibleRect.left == 0 && mVisibleRect.top > 0 &&
				mVisibleRect.right < width && mVisibleRect.bottom == height ) {
			/* case 7 == 3*/
			coordFirst[0] = mVisibleRect.left + childFirst.getWidth() / 2;
			coordFirst[1] = mVisibleRect.top + childFirst.getHeight() / 2;
			coordLast[0] = mVisibleRect.right - childLast.getWidth() / 2;
			coordLast[1] = mVisibleRect.bottom - childLast.getHeight() / 2;
		}
		
		if (mVisibleRect.left == 0 && mVisibleRect.top > 0 &&
				mVisibleRect.right == width && mVisibleRect.bottom == height ) {
			/* case 8 */
			coordFirst[0] = mVisibleRect.right - childFirst.getWidth() / 2;
			coordFirst[1] = mVisibleRect.top + childFirst.getHeight() / 2;
			coordLast[0] = mVisibleRect.left + childLast.getWidth() / 2;
			coordLast[1] = mVisibleRect.top + childLast.getHeight() / 2;
		}
		
/*		Log.d(this.getClass().getSimpleName(),
				String.format("checkValidArea(): case(%d), first[x: %d, y: %d], last[x: %d, y: %d]",
						caseNum,
						coordFirst[0], coordFirst[1],
						coordLast[0], coordLast[1]));
*/		
		float start = coordsToAngle(coordFirst[0] - width / 2, coordFirst[1] - height / 2);
		float end = coordsToAngle(coordLast[0] - width / 2, coordLast[1] - height / 2);
/*		Log.d(this.getClass().getSimpleName(),
				String.format("checkValidArea(): start(%d), end(%d)",
						radianToDegree(start),
						radianToDegree(end)));
*/
		mReversed = false;
		
		if (mVisibleRect.left > 0 && start > end) {
			start -= 2 * PI;
		} else if (mVisibleRect.right < width) {
			if (start < 0) {
				start += 2 * PI;
			}
			mReversed = true;
		} else if (mVisibleRect.top == 0 && mVisibleRect.left == 0 
				&& mVisibleRect.right == width && mVisibleRect.bottom < height) {
			if (start > end) {
				start -= 2 * PI;
			}
		}
/*		Log.d(this.getClass().getSimpleName(),
				String.format("checkValidArea(): FIXED: start(%d), end(%d), reversed(%s)",
						radianToDegree(start),
						radianToDegree(end),
						mReversed));
*/		
		setValidAreaInRadian(start, end);
	}

	private LayoutInfo fillLayoutInfo(float startAngle) {
		LayoutInfo info = null;
		
		final int width = getWidth();
		final int height = getHeight();
		final int count = getChildCount();
		
		if (width == 0 || height == 0 || count == 0) {
			return info;
		}

		final int paddingLeft = getPaddingLeft();
		final int paddingRight = getPaddingRight();
		final int paddingTop = getPaddingTop();
		final int paddingBottom = getPaddingBottom();

/*		Log.d(this.getClass().getSimpleName(),
				String.format("fillLayoutInfo(): width(%d), height(%d), padding(l:%d, t:%d, r:%d, b:%d)",
						width, height, paddingLeft, paddingTop, paddingRight, paddingBottom));
*/		
		int size = Math.min(
				width - (paddingLeft + paddingRight), 
				height - (paddingTop + paddingBottom));		
		
		info = new LayoutInfo();
		
		info.boundRect = new Rect();
		info.boundRect.left = (width - size) / 2;
		info.boundRect.top = (height - size) / 2;
		info.boundRect.right = info.boundRect.left + size;
		info.boundRect.bottom = info.boundRect.top + size;
		
		info.centerX = info.boundRect.left + info.boundRect.width() / 2;
		info.centerY = info.boundRect.top + info.boundRect.height() / 2;
		
/*		Log.d(this.getClass().getSimpleName(),
				String.format("fillLayoutInfo(): boundRect(%s), center(%d, %d)",
						info.boundRect, info.centerX, info.centerY));
*/
		int radius = 0;
		View child = null; 
		
		info.centerDistances = new int[count];
		info.childAngles = new float[count]; 
		info.childRadius = new int[count];
		info.childCoords = new int[count][2];
		
		if (mValidAreaStart == mValidAreaEnd) {
			for (int i = 0; i < count; i++) {
				child = getChildAt(i);
				
				info.childAngles[i] = 2 * PI * i / count + startAngle;
				info.childRadius[i] = Math.min(child.getMeasuredWidth(), child.getMeasuredHeight()) / 2;
				radius = info.boundRect.width() / 2 - info.childRadius[i];
				getCoordsInCircle(radius, info.childAngles[i], info.childCoords[i]);
				
				info.centerDistances[i] = (int)Math.sqrt(
						(Math.pow(info.childCoords[i][0], 2) + 
						Math.pow(info.childCoords[i][1], 2)));
				
				info.childCoords[i][0] += info.centerX;
				info.childCoords[i][1] += info.centerY;
/*				Log.d(this.getClass().getSimpleName(),
						String.format("fillLayoutInfo(): index(%d), child(%d, %d), coords[%d, %d], dist(%d)",
								i, 
								child.getMeasuredWidth(),
								child.getMeasuredHeight(),
								info.childCoords[i][0], info.childCoords[i][1],
								info.centerDistances[i]));
*/				
			}
		} else {
			float increament = ((mValidAreaEnd - mValidAreaStart) / (count - 1));
			float angle = mValidAreaStart;
			for (int i = 0; i < count; i++) {
				child = getChildAt(i);
				
				info.childAngles[i] = angle % (PI * 2);
				info.childRadius[i] = Math.min(child.getMeasuredWidth(), child.getMeasuredHeight()) / 2;
				radius = info.boundRect.width() / 2 - info.childRadius[i];
				getCoordsInCircle(radius, info.childAngles[i], info.childCoords[i]);
				
				info.centerDistances[i] = (int)Math.sqrt(
						(Math.pow(info.childCoords[i][0], 2) + 
						Math.pow(info.childCoords[i][1], 2)));
				
				info.childCoords[i][0] += info.centerX;
				info.childCoords[i][1] += info.centerY;
/*				Log.d(this.getClass().getSimpleName(),
						String.format("fillLayoutInfo(): index(%d), child(%d, %d), coords[%d, %d], dist(%d)",
								i, 
								child.getMeasuredWidth(),
								child.getMeasuredHeight(),
								info.childCoords[i][0], info.childCoords[i][1],
								info.centerDistances[i]));
*/				
				angle += increament;
				
			}
			
		}
/*		Log.d(this.getClass().getSimpleName(),
				String.format("fillLayoutInfo(): info(%s)",
						info));
*/
		return info;
	}
	

	private void getCoordsInCircle(int radius, float angle, int[] retCoords) {
		if (retCoords == null) {
			return;
		}
		
		retCoords[0] = (int)(radius * Math.cos(angle));
		retCoords[1] = (int)(radius * Math.sin(angle));
		
		return;
	}
	
	private float coordsToAngle(int x, int y) {
		if (x == 0) {
			return (y > 0 ? PI / 2 : -(PI / 2));
		}
		
		float arc = (float)Math.atan((float) y / x); 
		
		return (x < 0 ? (PI + arc) : arc);
	}

	public void setValidAreaInRadian(float start, float end) {
/*		Log.d(this.getClass().getSimpleName(),
				String.format("setValidAreaInRadian(): start(%d), end (%d)",
						radianToDegree(start),
						radianToDegree(end)));
*/		
		start = normalizeIn2PI(start);
		end = normalizeIn2PI(end);
		
		if (start > end && mReversed == false) {
//			start -= 2 * PI;
			float tmp = end;
			end = start;
			start = tmp;
		}
		
		if ((end - start) % (2 * PI) == 0) {
			end = start;
		}
		
		
/*		Log.d(this.getClass().getSimpleName(),
				String.format("setValidAreaInRadian(): FIXED: start(%d), end (%d)",
						radianToDegree(start),
						radianToDegree(end)));
*/		
		mValidAreaStart = start;
		mValidAreaEnd = end;
		mStartAngle = start;
		
		requestLayout();
	}
	
	private float normalizeIn2PI(float radian) {
		return (radian % (2 * PI));
	}
	
}
