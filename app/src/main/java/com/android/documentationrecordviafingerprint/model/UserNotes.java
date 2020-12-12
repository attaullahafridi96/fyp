package com.android.documentationrecordviafingerprint.model;

import com.android.documentationrecordviafingerprint.helper.IMyConstants;

public final class UserNotes extends UserFile implements IMyConstants {
    private String notes_data;

    public UserNotes() {     //required constructor for firebase
        super("", "", "", "", "");
    }

    public UserNotes(String file_name, String notes_data, String file_key, String file_size, String upload_date) {
        super(file_name, FILE_TYPE_NOTES, file_key, file_size, upload_date);
        this.notes_data = notes_data;
    }

    public String getNotes_data() {
        return notes_data;
    }

    public void setNotes_data(String notes_data) {
        this.notes_data = notes_data;
    }
}
