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
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.SessionManagement;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity implements IMyConstants {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = LoginActivity.this;
        if (new SessionManagement(context).isSessionActive()) {
            startActivity(new Intent(context, DashboardActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_user_login);
        final EditText email = findViewById(R.id.login_email);
        final EditText password = findViewById(R.id.login_password);
        Button login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEnteredEmail = email.getText().toString().trim();
                String getEnteredPassword = password.getText().toString();
                if (StringOperations.isEmpty(getEnteredEmail)) {
                    email.setError("Email is required field");
                    email.requestFocus();
                    Toast.makeText(context, "Email is required field", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!StringOperations.isValidEmail(getEnteredEmail)) {
                    email.setError("Invalid Email");
                    email.requestFocus();
                    Toast.makeText(context, "Enter a Valid Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringOperations.isEmpty(getEnteredPassword)) {
                    password.setError("Password is required field");
                    password.requestFocus();
                    Toast.makeText(context, "Password is required field", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    MyFirebaseDatabase.verifyLoginCredentials(context, getEnteredEmail, StringOperations.toMD5String(getEnteredPassword), LoginActivity.this);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        Button new_account_btn = findViewById(R.id.login_new_account_btn);
        new_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegistrationActivity.class));
                finish();
            }
        });
    }

}