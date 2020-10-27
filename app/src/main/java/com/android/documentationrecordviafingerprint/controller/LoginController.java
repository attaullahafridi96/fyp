package com.android.documentationrecordviafingerprint.controller;

import android.text.TextUtils;
import android.util.Patterns;

public final class LoginController {
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
}
