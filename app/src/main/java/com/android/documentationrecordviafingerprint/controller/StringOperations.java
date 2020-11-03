package com.android.documentationrecordviafingerprint.controller;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.NonNull;

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
}
