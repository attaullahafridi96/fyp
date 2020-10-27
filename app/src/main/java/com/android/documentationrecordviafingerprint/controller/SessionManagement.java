package com.android.documentationrecordviafingerprint.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private final String shared_pref_name;
    private final String session_key;

    @SuppressLint("CommitPrefEdits")
    public SessionManagement(Context context) {
        shared_pref_name = "session";
        session_key = "session_user";
        sharedPreferences = context.getSharedPreferences(shared_pref_name, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setSession(String session_id) {
        editor.putString(session_key, session_id).commit();
    }

    public String getSession() {
        return sharedPreferences.getString(session_key, null);
    }

    public String getEmailIdentifier() {
        String email_identifier = "";
        char[] type = {'-', '#', '$', '[', ']', '@', '.'};
        for (char c : type) {
            email_identifier = getSession().replace(c, '_');
        }
        return email_identifier;
    }

    public void destroySession() {
        editor.putString(session_key, null).commit();
    }

    public boolean isSessionActive() {
        return getSession() != null;
    }
}
