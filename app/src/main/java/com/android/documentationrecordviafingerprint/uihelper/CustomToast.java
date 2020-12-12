package com.android.documentationrecordviafingerprint.uihelper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public final class CustomToast {
    public static void makeToast(final Context context, final String msg, final int toast_timing) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, toast_timing).show();
            }
        });
    }
}
