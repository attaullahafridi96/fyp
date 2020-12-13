package com.android.documentationrecordviafingerprint.model;

public final class UserUploads extends UserFile implements IUserUploads {
    private String fileIconUri, fileExtension, fileUri, fileStorageId;

    public UserUploads() {     //required constructor for firebase
        super("", "", "", "", "");
    }

    public UserUploads(String fileIconUri, String file_name, String fileExtension, String file_type, String fileUri, String file_size, String file_key, String fileStorageId, String upload_date) {
        super(file_name, file_type, file_key, file_size, upload_date);
        this.fileIconUri = fileIconUri;
        this.fileExtension = fileExtension;
        this.fileUri = fileUri;
        this.fileStorageId = fileStorageId;
    }

    @Override
    public String getFileIconUri() {
        return fileIconUri;
    }

    @Override
    public String getFileStorageId() {
        return fileStorageId;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public String getFileUri() {
        return fileUri;
    }

    @Override
    public void setFileIconUri(String fileIconUri) {
        this.fileIconUri = fileIconUri;
    }

    @Override
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    @Override
    public void setFileStorageId(String fileStorageId) {
        this.fileStorageId = fileStorageId;
    }
}
