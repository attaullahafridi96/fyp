package com.android.documentationrecordviafingerprint.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionController {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private static final String SHARED_PREF_NAME = "session";
    private final String session_key;

    @SuppressLint("CommitPrefEdits")
    public SessionController(Context context) {
        session_key = "session_user";
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setSession(String session_id) {
        editor.putString(session_key, session_id).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(session_key, null);
    }

    public String getEmailIdentifier() {
        return StringOperations.removeInvalidCharsFromIdentifier(getSession());
    }

    public void destroySession() {
        editor.putString(session_key, null).commit();
    }

    public boolean isSessionActive() {
        return getSession() != null;
    }
}
