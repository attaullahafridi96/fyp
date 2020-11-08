package com.android.documentationrecordviafingerprint.model;

import java.io.Serializable;

public class FileData implements Serializable {
    protected String image_uri, file_name, file_type, file_key, upload_date;

    public FileData(String image_uri, String file_name, String file_type, String file_key, String upload_date) {
        this.image_uri = image_uri;
        this.file_name = file_name;
        this.file_type = file_type;
        this.file_key = file_key;
        this.upload_date = upload_date;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setFile_key(String file_key) {
        this.file_key = file_key;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public String getFile_name() {
        return file_name;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getFile_key() {
        return file_key;
    }

    public String getUpload_date() {
        return upload_date;
    }

}
