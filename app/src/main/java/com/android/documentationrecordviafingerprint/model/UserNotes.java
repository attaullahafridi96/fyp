package com.android.documentationrecordviafingerprint.model;

import com.android.documentationrecordviafingerprint.helper.IMyConstants;

public final class UserNotes extends UserFile implements IUserNotes, IMyConstants {
    private String notesData;

    public UserNotes() {     //required constructor for firebase
        super("", "", "", "", "");
    }

    public UserNotes(String file_name, String notesData, String file_key, String file_size, String upload_date) {
        super(file_name, FILE_TYPE_NOTES, file_key, file_size, upload_date);
        this.notesData = notesData;
    }

    @Override
    public String getNotesData() {
        return notesData;
    }

    @Override
    public void setNotesData(String notesData) {
        this.notesData = notesData;
    }
}
