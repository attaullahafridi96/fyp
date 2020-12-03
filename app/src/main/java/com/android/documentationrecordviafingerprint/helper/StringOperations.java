package com.android.documentationrecordviafingerprint.helper;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringOperations {
    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    public static boolean isAnyEditTextEmpty(String... str) {
        for (String item : str) {
            if (item == null || item.equals("")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static CharSequence toAlphaNumeric(CharSequence source) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static String capitalizeString(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }
        return capMatcher.appendTail(capBuffer).toString();
    }

    public static String removeInvalidCharsFromIdentifier(@NonNull String str) {
        if (isEmpty(str)) {
            return null;
        }
        String validString = "";
        char[] type = {'-', '#', '$', '[', ']', '@', '.'};
        for (char c : type) {
            validString = str.replace(c, '_');
        }
        return validString;
    }

    public static String createFileIdentifier(@NonNull String file_name) {
        if (isEmpty(file_name)) {
            return null;
        }
        String tmp_file_id = file_name.replace(" ", "");
        return removeInvalidCharsFromIdentifier(tmp_file_id);
    }

    public static String toMD5String(String string) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(StandardCharsets.UTF_8.encode(string));
            return String.format("%032x", new BigInteger(1, md5.digest()));
        } catch (Exception ignored) { }
        return null;
    }
}
