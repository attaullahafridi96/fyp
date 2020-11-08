package com.android.documentationrecordviafingerprint.model;

public final class User {
    private byte[] user_image;
    private String first_name, last_name, email, password;

    public User() {
        //Required Constructor for Firebase
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
