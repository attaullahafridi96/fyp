package com.android.documentationrecordviafingerprint.model;

public final class UserFile extends FileData {
    private String file_extension, file_uri, file_size, file_storage_key;

    public UserFile() {     //required constructor for firebase
        super("", "", "", "", "");
    }

    public UserFile(String image_uri, String file_name, String file_extension, String file_type, String file_uri, String file_size, String file_key, String file_storage_key, String upload_date) {
        super(image_uri, file_name, file_type, file_key, upload_date);
        this.image_uri = image_uri;
        this.file_name = file_name;
        this.file_extension = file_extension;
        this.file_type = file_type;
        this.file_uri = file_uri;
        this.file_size = file_size;
        this.file_key = file_key;
        this.file_storage_key = file_storage_key;
        this.upload_date = upload_date;
    }

    public String getFile_storage_key() {
        return file_storage_key;
    }

    public String getFile_extension() {
        return file_extension;
    }

    public String getFile_size() {
        return file_size;
    }

    public String getFile_uri() {
        return file_uri;
    }
}
