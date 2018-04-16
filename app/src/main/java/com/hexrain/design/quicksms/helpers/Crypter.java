package com.hexrain.design.quicksms.helpers;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class Crypter {

    @Nullable
    public static String decrypt(@Nullable String string) {
        if (string == null) return null;
        String result = "";
        byte[] byte_string = Base64.decode(string, Base64.DEFAULT);
        try {
            result = new String(byte_string, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return result;
    }

    @Nullable
    public static String encrypt(@Nullable String string) {
        if (string == null) return null;
        byte[] string_byted = null;
        try {
            string_byted = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(string_byted, Base64.DEFAULT).trim();
    }
}
