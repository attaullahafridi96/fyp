package com.android.documentationrecordviafingerprint.model;

import java.io.Serializable;

public class FileData implements Serializable, IMyConstants {
    protected String name, type, id, size, date_upload;

    public FileData(String name, String type, String id, String size, String date_upload) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.size = size;
        this.date_upload = date_upload;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getSize() {
        return size;
    }

    public String getDate_upload() {
        return date_upload;
    }

}
