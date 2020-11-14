package com.android.documentationrecordviafingerprint.model;

public final class UserNote extends FileData {
    private String note_title, note_details;

    public UserNote() {     //required constructor for firebase
        super("", "", "", "", "");
    }

    public UserNote(String note_details, String image_uri, String file_name, String file_type, String file_key, String upload_date) {
        super(image_uri, file_name, file_type, file_key, upload_date);
        this.note_details = note_details;
    }

}
