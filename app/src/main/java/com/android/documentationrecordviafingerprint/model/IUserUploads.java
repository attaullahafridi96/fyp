package com.android.documentationrecordviafingerprint.model;

public interface IUserUploads {
    String getFileIconUri();

    String getFileStorageId();

    String getFileExtension();

    String getFileUri();

    void setFileIconUri(String fileIconUri);

    void setFileExtension(String fileExtension);

    void setFileUri(String fileUri);

    void setFileStorageId(String fileStorageId);
}
