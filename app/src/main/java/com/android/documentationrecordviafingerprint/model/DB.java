package com.android.documentationrecordviafingerprint.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class DB {
    private static FirebaseDatabase database;

    private DB() {
        //Constructor Locked
    }

    public static DatabaseReference getFirstNodeReference() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        return database.getReference("User");
    }
}
