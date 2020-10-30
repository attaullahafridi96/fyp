package com.android.documentationrecordviafingerprint.controller;

import android.text.TextUtils;
import android.util.Patterns;

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

    public static String toOriginalChars() {
        return null;
    }

    public static String removeInvalidCharsFromIdentifier(String str) {
        String validString = "";
        char[] type = {'-', '#', '$', '[', ']', '@', '.'};
        for (char c : type) {
            validString = str.replace(c, '_');
        }
        return validString;
    }
    public static String createIdentifier(String file_name) {
        String[] file_name_type = file_name.split("\\.");
        String tmp_file_id = file_name_type[0].replace(" ", "");
        return removeInvalidCharsFromIdentifier(tmp_file_id);
    }
}
