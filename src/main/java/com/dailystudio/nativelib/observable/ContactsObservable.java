package com.dailystudio.nativelib.observable;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.ContactsContract;

import com.dailystudio.development.Logger;

/**
 * Created by nan on 2015/1/11.
 */
public class ContactsObservable extends NativeObservable {

    public ContactsObservable(Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        final ContentResolver cr = mContext.getContentResolver();

        cr.registerContentObserver(ContactsContract.Contacts.CONTENT_URI,
                true, mContactsObserver);
    }

    @Override
    protected void onDestroy() {
        final ContentResolver cr = mContext.getContentResolver();

        cr.unregisterContentObserver(mContactsObserver);
    }

    @Override
    protected void onPause() {

    }

    @Override
    protected void onResume() {

    }

    private Handler mHandler = new Handler();

    private ContentObserver mContactsObserver = new ContentObserver(mHandler) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            notifyObservers();
        }

    };

}
