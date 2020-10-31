package com.android.documentationrecordviafingerprint.model;

public class UserDocument {
    private String image_uri, file_name, file_extension, file_type, file_uri, file_size, file_key;

    public UserDocument(String image_uri, String file_name, String file_extension, String file_type, String file_uri, String file_size, String file_key) {
        this.image_uri = image_uri;
        this.file_name = file_name;
        this.file_extension = file_extension;
        this.file_type = file_type;
        this.file_uri = file_uri;
        this.file_size = file_size;
        this.file_key = file_key;
    }

    public UserDocument() {
        //required constructor for firebase
    }

    public String getFile_key() {
        return file_key;
    }

    public String getFile_extension() {
        return file_extension;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public String getFile_size() {
        return file_size;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getFile_uri() {
        return file_uri;
    }
}
