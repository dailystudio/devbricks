package com.dailystudio.app.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailystudio.development.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWindow extends FrameLayout {
	
	public static interface Callbacks {
		
		public void onWindowOpened(AbstractWindow window);
		
		public void onWindowClosed(AbstractWindow window);
		
	}
	
	protected Context mContext;

	private List<Callbacks> mCallbacks = null;
	
	private TextView mTitle;
	private ImageView mTitleIcon;
	private View mCloseButton;
	private ViewGroup mContentView;

	
	public AbstractWindow(Context context) {
		this(context, null);
	}
	
	public AbstractWindow(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AbstractWindow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mContext = context;
		
		initMembers();
		
		createLayout();
	}
	
	private void initMembers() {
		mCallbacks = new ArrayList<Callbacks>();
		
		setClipChildren(false);
		setClipToPadding(false);
	}
	
	private void createLayout() {
		onCreateWindow();
		
		mTitle = findTitle();
		mTitleIcon = findTitleIcon();
		mCloseButton = findCloseButton();
		if (mCloseButton != null) {
			mCloseButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					closeWindow();
				}
				
			});
		}
		
		mContentView = findContent();
	}

	public void openWindow() {
		if (isShown()) {
			return;
		}
		
		onOpenWindow();
		
		if (mCallbacks != null) {
			for (Callbacks callbacks: mCallbacks) {
				callbacks.onWindowOpened(this);
			}
		}
	}
	
	public void closeWindow() {
		if (!isShown()) {
			return;
		}
		
		onCloseWindow();
		
		if (mCallbacks != null) {
			for (Callbacks callbacks: mCallbacks) {
				callbacks.onWindowClosed(this);
			}
		}
	}

	protected Animation getWindowOpenAnimation() {
		return null;
	}

	protected Animation getWindowCloseAnimation() {
		return null;
	}
	
	public void addCallbacks (Callbacks callbacks) {
		Logger.debug("l = %s", callbacks);
		if (mCallbacks != null) {
			mCallbacks.add(callbacks);
		}
	}

	public void removeCallbacks (Callbacks callbacks) {
		Logger.debug("l = %s", callbacks);
		if (mCallbacks != null) {
			mCallbacks.remove(callbacks);
		}
	}

	public void clearCallbacks () {
		if (mCallbacks != null) {
			mCallbacks.clear();
		}
	}
	
	public void setTitle(int titleResId) {
		if (mTitle == null) {
			return;
		}
		
		mTitle.setText(titleResId);
	}
	
	public void setTitle(CharSequence title) {
		if (mTitle == null) {
			return;
		}
		
		mTitle.setText(title);
	}
	
	public void setTitleColor(int color) {
		if (mTitle == null) {
			return;
		}
		
		mTitle.setTextColor(color);
	}
	
	public void setTitleColorResource(int resid) {
		if (mTitle == null) {
			return;
		}
		
		final Context context = getContext();
		if (context == null) {
			return;
		}
		
		final Resources res = context.getResources();
		if (res == null) {
			return;
		}
		
		mTitle.setTextColor(res.getColor(resid));
	}
	
	public void setIcon(int iconResId) {
		if (mTitleIcon == null) {
			return;
		}
		
		if (iconResId <= 0) {
			mTitleIcon.setVisibility(GONE);
		} else {
			mTitleIcon.setVisibility(VISIBLE);
			mTitleIcon.setImageResource(iconResId);
		}
	}
	
	public void setIcon(Drawable drawable) {
		if (mTitleIcon == null) {
			return;
		}
		
		if (drawable == null) {
			mTitleIcon.setVisibility(GONE);
		} else {
			mTitleIcon.setVisibility(VISIBLE);
			mTitleIcon.setImageDrawable(drawable);
		}
	}
	
	public void setContentView(int layoutResID) {
		if (mContentView == null) {
			return;
		}
		
		LayoutInflater.from(getContext()).inflate(layoutResID, mContentView);
	}
	
	public void setContentView(View view) {
		if (mContentView == null) {
			return;
		}
		
		mContentView.addView(view);
	}
	
	public void setContentView(View view, LayoutParams params) {
		if (mContentView == null) {
			return;
		}
		
		mContentView.addView(view, params);
	}

	abstract public boolean isShown();
	abstract protected void onOpenWindow();
	abstract protected void onCloseWindow();
	abstract protected void onCreateWindow();
	
	abstract protected ViewGroup findContent();
	abstract protected View findCloseButton();
	abstract protected TextView findTitle();
	abstract protected ImageView findTitleIcon();

}