package com.android.documentationrecordviafingerprint.model;

public class UserFile {
    private String image_uri,file_name, file_type, file_uri,file_size;

    public UserFile(String image_uri, String file_name, String file_type, String file_uri, String file_size) {
        this.image_uri = image_uri;
        this.file_name = file_name;
        this.file_type = file_type;
        this.file_uri = file_uri;
        this.file_size = file_size;
    }

    public UserFile() {
        //required constructor for firebase
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
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
