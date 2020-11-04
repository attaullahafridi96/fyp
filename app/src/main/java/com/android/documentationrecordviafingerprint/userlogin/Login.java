package com.android.documentationrecordviafingerprint.userlogin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.DashboardActivity;
import com.android.documentationrecordviafingerprint.controller.SessionManagement;
import com.android.documentationrecordviafingerprint.controller.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.internetchecking.NoInternetScreen;
import com.android.documentationrecordviafingerprint.model.MyFirebaseDatabase;

public class Login extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = Login.this;
        if (new SessionManagement(context).isSessionActive()) {
            startActivity(new Intent(context, DashboardActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_user_login);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
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
                    MyFirebaseDatabase.verifyLoginCredentials(context, getEnteredEmail, StringOperations.toMD5String(getEnteredPassword), Login.this);
                } else {
                    startActivity(new Intent(context, NoInternetScreen.class));
                    finish();
                }
            }
        });

        Button new_account_btn = findViewById(R.id.new_account_btn);
        new_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegistrationForm.class));
                finish();
            }
        });
    }

}