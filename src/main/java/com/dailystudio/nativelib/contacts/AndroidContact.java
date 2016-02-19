package com.dailystudio.nativelib.contacts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;

import com.dailystudio.development.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nan on 2015/1/10.
 */
public class AndroidContact {

    private long mContactId;
    private String mDisplayName;
    private Bitmap mContactPhoto;

    private AndroidContact() {
    }

    public AndroidContact(long contactId) {
        mContactId = contactId;
    }

    public void resolveDetails(Context context) {
        if (context == null) {
            return;
        }

        mDisplayName = resolveDisplayName(context);
        mContactPhoto = resolveDisplayPhoto(context);
    }

    protected String resolveDisplayName(Context context) {
        return getDisplayNameByContactId(context, mContactId);
    }

    protected Bitmap resolveDisplayPhoto(Context context) {
        return getDisplayPhotoByContactId(context, mContactId);
    }

    public long getContactId() {
        return mContactId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public Bitmap getContactPhoto() {
        return mContactPhoto;
    }

    public Uri getContactUri() {
        return Uri.withAppendedPath(
                ContactsContract.Contacts.CONTENT_URI,
                String.valueOf(mContactId));
    }

    @Override
    public String toString() {
        return String.format("%s(0x%08x): contact info(id = %d, displayName: %s, photo: %s(%-3dx%-3d), uri = %s)",
                getClass().getSimpleName(),
                hashCode(),
                mContactId,
                mDisplayName,
                mContactPhoto,
                (mContactPhoto == null ? 0: mContactPhoto.getWidth()),
                (mContactPhoto == null ? 0: mContactPhoto.getHeight()),
                getContactUri());
    }

    public static AndroidContact getContactByUri(Context context, Uri contactUri) {
        if (context == null || contactUri == null) {
            return null;
        }

        final ContentResolver cr = context.getContentResolver();
        if (cr == null) {
            return null;
        }

        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID
        };

        AndroidContact contact = null;

        Cursor c = cr.query(contactUri, projection, null, null, null);
        Logger.debug("c = %s(size = %d)", c, (c == null ? 0 : c.getCount()));
        try {
            if (c != null) {
                if (c.moveToFirst()) {
                    contact = new AndroidContact();

                    contact.mContactId = c.getLong(
                            c.getColumnIndex(
                                    ContactsContract.PhoneLookup._ID));
                    contact.mDisplayName = c.getString(
                            c.getColumnIndex(
                                    ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
            }
        } catch (Exception e) {
            Logger.warnning("get contact for uri(%s) failed: %s",
                    contactUri, e.toString());
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return contact;
    }

    public static AndroidContact getContactByNumber(Context context, String givenNumber) {
        AndroidContact[] contacts = getContactsByNumber(context, givenNumber);
        if (context == null || contacts.length <= 0) {
            return null;
        }

        return contacts[0];
    }

    public static AndroidContact[] getContactsByNumber(Context context, String givenNumber){
        if (context == null || TextUtils.isEmpty(givenNumber)) {
            return null;
        }

        final ContentResolver cr = context.getContentResolver();
        if (cr == null) {
            return null;
        }

        List<AndroidContact> contacts = new ArrayList<AndroidContact>();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(givenNumber));

        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID
        };

        Cursor c = cr.query(uri, projection, null, null, null);
        try {
            if (c != null) {
                AndroidContact contact;

                while (c.moveToNext()) {
                    contact = new AndroidContact();

                    contact.mContactId = c.getLong(
                            c.getColumnIndex(
                                    ContactsContract.PhoneLookup._ID));
                    contact.mDisplayName = c.getString(
                            c.getColumnIndex(
                                    ContactsContract.PhoneLookup.DISPLAY_NAME));

                    contacts.add(contact);
                }
            }
        } catch (Exception e) {
            Logger.warnning("query contacts for phone number(%s) failed: %s",
                    givenNumber, e.toString());
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return contacts.toArray(new AndroidContact[0]);
    }

    public static String getDisplayNameByContactId(Context context, long contactId) {
        if (context == null) {
            return null;
        }

        final ContentResolver cr = context.getContentResolver();
        if (cr == null) {
            return null;
        }

        String displayName = null;

        String[] projection = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME
        };

        Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI,
                projection,
                ContactsContract.Contacts._ID + "=?",
                new String[] { String.valueOf(contactId) },
                null);
        try {
            if (c != null) {
//                DatabaseUtils.dumpCursor(c);
                while (c.moveToNext()) {
                    displayName = c.getString(c.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));
                }
            }
        } catch (Exception e) {
            Logger.warnning("retrieve display name for contact(%d) failed: %s",
                    contactId, e.toString());

            displayName = null;
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return displayName;
    }

    public static Bitmap getDisplayPhotoByContactId(Context context, long contactId) {
        if (context == null) {
            return null;
        }

        final ContentResolver cr = context.getContentResolver();
        if (cr == null) {
            return null;
        }

        Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
                String.valueOf(contactId));

        InputStream photoInputStream = openContactPhotoStream(cr, contactUri);
//        Logger.warnning("photoInputStream for contact(%d) = %s",
//                contactId, photoInputStream);
        if (photoInputStream == null) {
            return null;
        }

        Bitmap photoBitmap = null;
        try {
            photoBitmap = BitmapFactory.decodeStream(
                    new BufferedInputStream(photoInputStream));
        } catch (OutOfMemoryError e) {
            Logger.warnning("decode photo for contact(%d) failed: %s",
                    contactId, e.toString());
            photoBitmap = null;
        } finally {
            try {
                photoInputStream.close();
            } catch (IOException e) {
                Logger.warnning("close photo stream for contact(%d) failed: %s",
                        contactId, e.toString());
            }
        }

        return photoBitmap;
    }

    private static InputStream openContactPhotoStream(ContentResolver cr, Uri contactUri) {
        InputStream photoInputStream = null;

        if (Build.VERSION.SDK_INT > 14) {
            try {
                Method method = ContactsContract.Contacts.class.getMethod(
                        "openContactPhotoInputStream ",
                        ContentResolver.class,
                        Uri.class,
                        boolean.class);
                photoInputStream =
                        (InputStream) method.invoke(null, cr, contactUri, true);
            } catch (Exception e) {
                Logger.warnning("openContactPhotoInputStream() failure: %s", e.toString());

                photoInputStream = null;
            }

        }

        if (photoInputStream == null) {
            photoInputStream = ContactsContract.Contacts.openContactPhotoInputStream(
                    cr, contactUri);
        }

        return photoInputStream;
    }

    public static String getPhoneNumberByContactId(Context context, long contactId) {
        if (context == null) {
            return null;
        }

        final ContentResolver cr = context.getContentResolver();
        if (cr == null) {
            return null;
        }

        String phoneNumber = null;

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[] { String.valueOf(contactId) },
                null);
        try {
            if (c != null) {
//                DatabaseUtils.dumpCursor(c);
                while (c.moveToNext()) {
                    phoneNumber = c.getString(c.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
            }
        } catch (Exception e) {
            Logger.warnning("retrieve phone number for contact(%d) failed: %s",
                    contactId, e.toString());

            phoneNumber = null;
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return phoneNumber;
    }

    public static long getContactId(Context context, long rawContactId) {
        return getContactId(context, Uri.withAppendedPath(
                ContactsContract.RawContacts.CONTENT_URI,
                String.valueOf(rawContactId)));
    }

    public static long getContactId(Context context, Uri rawContactUri) {
        if (context == null || rawContactUri == null) {
            return -1;
        }

        final ContentResolver cr = context.getContentResolver();
        if (cr == null) {
            return -1;
        }


        String[] projection = new String[] {
                ContactsContract.RawContacts.CONTACT_ID
        };

        long contactId = 0;

        Cursor c = cr.query(rawContactUri,
                projection,
                null, null,
                null);
        try {
            if (c != null) {
                DatabaseUtils.dumpCursor(c);
                while (c.moveToNext()) {
                    contactId = c.getLong(c.getColumnIndex(
                            ContactsContract.RawContacts.CONTACT_ID));
                }
            }
        } catch (Exception e) {
            Logger.warnning("retrieve phone number for contact(%d) failed: %s",
                    contactId, e.toString());

            contactId = 0;
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return contactId;
    }

}
