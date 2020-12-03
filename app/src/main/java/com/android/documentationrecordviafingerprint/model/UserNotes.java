package com.android.documentationrecordviafingerprint.model;

public final class UserNotes extends FileData {
    private String notes_data, date_modify;

    public UserNotes() {     //required constructor for firebase
        super("", "", "", "", "");
    }

    public UserNotes(String file_name, String notes_data, String file_key, String file_size, String upload_date, String date_modify) {
        super(file_name, FILE_TYPE_NOTES, file_key, file_size, upload_date);
        this.notes_data = notes_data;
        this.date_modify = date_modify;
    }

    public String getNotes_data() {
        return notes_data;
    }

    public String getDate_modify() {
        return date_modify;
    }

    public void setDate_modify(String date_modify) {
        this.date_modify = date_modify;
    }
}
