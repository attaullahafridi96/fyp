package com.android.documentationrecordviafingerprint.model;

import java.io.Serializable;

public class UserFile implements Serializable, IUserFile {
    private String title, type, id, size, dateUpload, dateModify;

    public UserFile(String title, String type, String id, String size, String dateUpload) {
        this.title = title;
        this.type = type;
        this.id = id;
        this.size = size;
        this.dateUpload = dateUpload;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSize() {
        return size;
    }

    @Override
    public String getDateUpload() {
        return dateUpload;
    }

    @Override
    public String getDateModify() {
        return dateModify;
    }

    @Override
    public void setDateModify(String dateModify) {
        this.dateModify = dateModify;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public void setDateUpload(String dateUpload) {
        this.dateUpload = dateUpload;
    }
}
