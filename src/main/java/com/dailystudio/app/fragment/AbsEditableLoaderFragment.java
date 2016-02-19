package com.dailystudio.app.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.dailystudio.app.fragment.AbsLoaderFragment;
import com.dailystudio.development.Logger;

public abstract class AbsEditableLoaderFragment<T> extends AbsLoaderFragment<T> {
	
	public class CurrencyFormatInputFilter implements InputFilter {

		Pattern mPattern = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?");

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			String result = dest.subSequence(0, dstart) + source.toString()
					+ dest.subSequence(dend, dest.length());

			Matcher matcher = mPattern.matcher(result);

			if (!matcher.matches())
				return dest.subSequence(dstart, dend);

			return null;
		}
	}
	
	private boolean mHasInvalidInput = false;
	
	private Toast mLastErrorToast = null;
	private int mErrorToastResId = 0;
	
	@Override
	public void onPause() {
		super.onPause();

		if (mLastErrorToast != null) {
			mLastErrorToast.cancel();
		}
	}
	
	protected void setFeedbackToastResource(int resId) {
		mErrorToastResId = resId;
	}
	
	protected void showFeedbackToast() {
		if (mErrorToastResId <= 0) {
			return;
		}
		
		showErrorToast(mErrorToastResId);
	}

	protected void showErrorToast(int resId) {
		if (mLastErrorToast != null) {
			mLastErrorToast.cancel();
		}
		
		mLastErrorToast = Toast.makeText(getActivity(), 
				getString(resId),
				Toast.LENGTH_SHORT);
		
		mLastErrorToast.show();
	}
	
	protected void setInvalidInput(boolean invalid) {
		mHasInvalidInput = invalid;
	}
	
	protected boolean hasInvalidInput() {
		return mHasInvalidInput;
	}
	
	protected double dumpAmount(EditText editView) {
		if (editView == null) {
			return .0;
		}
		
		final Editable editable = editView.getEditableText();
		if (editable == null) {
			return .0;
		}
		
		String amountstr = editable.toString();
		if (TextUtils.isEmpty(amountstr)) {
			return .0;
		}
		
		amountstr = amountstr.replace(",", "");
		
		double amount = .0;
		try {
			amount = Double.parseDouble(amountstr);
		} catch (NumberFormatException e) {
			Logger.warnning("could not parse amount from [%s]: %s",
					amountstr, e.toString());
			
			amount = .0;
		}
		
		return amount;
	}
	
	protected String dumpText(EditText editView) {
		if (editView == null) {
			return null;
		}
		
		final Editable editable = editView.getEditableText();
		if (editable == null) {
			return null;
		}
		
		return editable.toString();
	}
	
	
	protected long dumpCount(EditText editView) {
		if (editView == null) {
			return 0l;
		}
		
		final Editable editable = editView.getEditableText();
		if (editable == null) {
			return 0l;
		}
		
		String countstr = editable.toString();
		if (TextUtils.isEmpty(countstr)) {
			return 0l;
		}
		
		long count = 0l;
		try {
			count = Long.parseLong(countstr);
		} catch (NumberFormatException e) {
			Logger.warnning("could not parse count from [%s]: %s",
					countstr, e.toString());
			
			count = 0l;
		}
		
		return count;
	}
	
	protected void showKeyboard(View view) {
		if (view == null) {
			return;
		}
		
		final Context context = getActivity();
		if (context == null) {
			return;
		}
		
        InputMethodManager imm = 
        		(InputMethodManager) context.getSystemService(
        				Context.INPUT_METHOD_SERVICE);
        
        imm.showSoftInput(view, 
        		InputMethodManager.SHOW_IMPLICIT);
	}
	
	protected void placeCursorAtEnd(EditText edit) {
		if (edit == null) {
			return;
		}
		
		Editable editable = edit.getText();
		if (editable == null) {
			return;
		}
		
		edit.setSelection(editable.length());
	}

}
