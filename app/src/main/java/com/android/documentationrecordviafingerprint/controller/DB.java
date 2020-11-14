package com.android.documentationrecordviafingerprint.controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public final class DB {
    private static FirebaseDatabase database;
    private static FirebaseStorage storage;

    private DB() {
        //Constructor Locked
    }

    public static DatabaseReference getDBFirstNodeReference() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        return database.getReference("User");
    }

    public static StorageReference getStorageReference() {
        if (storage == null) {
            storage = FirebaseStorage.getInstance();
        }
        return storage.getReference("Uploads");
    }
}
