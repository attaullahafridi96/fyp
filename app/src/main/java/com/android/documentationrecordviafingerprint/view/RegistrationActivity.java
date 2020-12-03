package com.android.documentationrecordviafingerprint.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.IMyConstants;
import com.android.documentationrecordviafingerprint.model.User;
import com.google.android.material.snackbar.Snackbar;

public class RegistrationActivity extends AppCompatActivity implements IMyConstants {
    private static String enteredFirstName, enteredLastName, enteredEmailAddress, enteredPassword, enteredConfirmPassword;
    private Context context;
    private static final int MIN_FIRST_NAME_CHARS = 3;
    private static final int MIN_PASSWORD_CHARS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);
        context = RegistrationActivity.this;
        final EditText firstname = findViewById(R.id.reg_first_name);
        final EditText lastname = findViewById(R.id.reg_last_name);
        final EditText email = findViewById(R.id.reg_email);
        final EditText pass = findViewById(R.id.reg_password);
        final EditText confirmpass = findViewById(R.id.reg_confirmpassword);

        Button create_account_btn = findViewById(R.id.reg_create_account_btn);
        Button backToLogin_btn = findViewById(R.id.reg_backToLogin_btn);

        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredFirstName = firstname.getText().toString().trim();
                enteredLastName = lastname.getText().toString().trim();
                enteredEmailAddress = email.getText().toString().trim();
                enteredPassword = pass.getText().toString();
                enteredConfirmPassword = confirmpass.getText().toString();

                if (StringOperations.isAnyEditTextEmpty(enteredFirstName, enteredEmailAddress, enteredPassword, enteredConfirmPassword)) {
                    Toast.makeText(context, "Required fields Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (enteredFirstName.length() < MIN_FIRST_NAME_CHARS) {
                    firstname.setError("Use at least " + MIN_FIRST_NAME_CHARS + " characters");
                    firstname.requestFocus();
                    return;
                }
                if (!StringOperations.isValidEmail(enteredEmailAddress)) {
                    email.setError("Invalid Email");
                    email.requestFocus();
                    Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (enteredPassword.length() < MIN_PASSWORD_CHARS) {
                    pass.setError("Password must be " + MIN_PASSWORD_CHARS + " characters or longer");
                    pass.requestFocus();
                    return;
                }
                if (!enteredPassword.equals(enteredConfirmPassword)) {
                    Toast.makeText(context, "Password not Matched", Toast.LENGTH_SHORT).show();
                } else {
                    if (CheckInternetConnectivity.isInternetConnected(RegistrationActivity.this)) {
                        User user = new User(enteredFirstName, enteredLastName, enteredEmailAddress, StringOperations.toMD5String(enteredPassword));
                        MyFirebaseDatabase.createNewUserAccount(context, user, RegistrationActivity.this);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        backToLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}