package com.android.documentationrecordviafingerprint.model;

public final class UserFile extends FileData {
    private String file_icon_uri, file_extension, file_uri, file_storage_id;

    public UserFile() {     //required constructor for firebase
        super("", "", "", "", "");
    }

    public UserFile(String file_icon_uri, String file_name, String file_extension, String file_type, String file_uri, String file_size, String file_key, String file_storage_id, String upload_date) {
        super(file_name, file_type, file_key, file_size, upload_date);
        this.file_icon_uri = file_icon_uri;
        this.file_extension = file_extension;
        this.file_uri = file_uri;
        this.file_storage_id = file_storage_id;
    }

    public String getFile_icon_uri() {
        return file_icon_uri;
    }

    public String getFile_storage_id() {
        return file_storage_id;
    }

    public String getFile_extension() {
        return file_extension;
    }

    public String getFile_uri() {
        return file_uri;
    }
}
