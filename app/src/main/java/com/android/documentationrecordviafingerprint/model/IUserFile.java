package com.android.documentationrecordviafingerprint.model;

public interface IUserFile {
    void setTitle(String title);

    void setId(String id);

    String getTitle();

    String getType();

    String getId();

    String getSize();

    String getDateUpload();

    String getDateModify();

    void setDateModify(String dateModify);

    void setType(String type);

    void setSize(String size);

    void setDateUpload(String dateUpload);
}
