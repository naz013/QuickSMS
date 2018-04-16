package com.hexrain.design.quicksms.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Contacts {

    @Nullable
    public static String getContactNameFromNumber(@NonNull Context context, @Nullable String contactNumber) {
        String phoneContactID = null;
        if (contactNumber != null) {
            try {
                String contact = Uri.encode(contactNumber);
                Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                        contact), new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
                        null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        phoneContactID = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    }
                    cursor.close();
                }
            } catch (IllegalArgumentException iae) {
                return phoneContactID;
            }
        }
        return phoneContactID;
    }
}
