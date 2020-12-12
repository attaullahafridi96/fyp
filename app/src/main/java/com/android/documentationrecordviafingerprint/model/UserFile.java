package com.android.documentationrecordviafingerprint.model;

import java.io.Serializable;

public class UserFile implements Serializable {
    protected String name, type, id, size, dateUpload, dateModify;

    public UserFile(String name, String type, String id, String size, String dateUpload) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.size = size;
        this.dateUpload = dateUpload;
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

    public String getDateUpload() {
        return dateUpload;
    }

    public String getDateModify() {
        return dateModify;
    }

    public void setDateModify(String dateModify) {
        this.dateModify = dateModify;
    }
}
