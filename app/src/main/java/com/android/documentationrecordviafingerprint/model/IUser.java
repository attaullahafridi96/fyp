package com.android.documentationrecordviafingerprint.model;

public interface IUser {
    String getFirstName();

    String getLastName();

    void setFirstName(String firstName);

    void setLastName(String lastName);

    String getEmail();

    String getPassword();

    void setEmail(String email);

    void setPassword(String password);
}
