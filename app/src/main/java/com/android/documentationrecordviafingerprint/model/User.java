package com.android.documentationrecordviafingerprint.model;

public class User {
    private byte[] user_image;
    private String first_name, last_name, email, password;
    private UserFile files;

    public User() {
    }

    public User(String first_name, String last_name, String email, String password, UserFile files) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.files = files;
    }

    public User(String first_name, String last_name, String email, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }


    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserFile getFiles() {
        return files;
    }

    public void setFiles(UserFile file) {
        this.files = file;
    }
}
