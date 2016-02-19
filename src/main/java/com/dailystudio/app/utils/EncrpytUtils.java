package com.dailystudio.app.utils;

import android.text.TextUtils;

import com.dailystudio.development.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by nan on 2015/1/14.
 */
public class EncrpytUtils {

    private final static char[] HEX_TABLE = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f' };

    private static String byteArrayToHex(byte[] array) {
        String s = "";
        for (int i = 0; i < array.length; ++i) {
            int di = (array[i] + 256) & 0xFF; // Make it unsigned
            s = s + HEX_TABLE[(di >> 4) & 0xF] + HEX_TABLE[di & 0xF];
        }
        return s;
    }

    private static String digest(String s, String algorithm) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            Logger.warnning("create digest [s: %s, alg: %s] failed: %s",
                    s, algorithm, e.toString());
            return s;
        }

        m.update(s.getBytes(), 0, s.length());

        return byteArrayToHex(m.digest());
    }

    public static String md5(String s) {
        return digest(s, "MD5");
    }

    public static String md5(String[] strings) {
        if (strings == null || strings.length <= 0) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (TextUtils.isEmpty(strings[i])) {
                continue;
            }

            builder.append(strings[i]);
        }

        return md5(builder.toString());
    }

}
