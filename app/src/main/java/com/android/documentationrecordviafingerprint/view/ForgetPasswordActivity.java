package com.android.documentationrecordviafingerprint.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.google.android.material.snackbar.Snackbar;

public class ForgetPasswordActivity extends AppCompatActivity implements IMyConstants, View.OnClickListener {
    private EditText forgetPasswordEmail_ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Button sendPassword_btn = findViewById(R.id.sendPassword_btn);
        sendPassword_btn.setOnClickListener(this);
        forgetPasswordEmail_ed = findViewById(R.id.forgetPasswordEmail_ed);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sendPassword_btn) {
            String recipient_email = forgetPasswordEmail_ed.getText().toString().trim();
            if (StringOperations.isEmpty(recipient_email)) {
                forgetPasswordEmail_ed.setError("Email is required field");
                forgetPasswordEmail_ed.requestFocus();
                Toast.makeText(ForgetPasswordActivity.this, "Email is required field", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!StringOperations.isValidEmail(recipient_email)) {
                forgetPasswordEmail_ed.setError("Invalid Email");
                forgetPasswordEmail_ed.requestFocus();
                Toast.makeText(ForgetPasswordActivity.this, "Enter a Valid Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (CheckInternetConnectivity.isInternetConnected(ForgetPasswordActivity.this)) {
                MyFirebaseDatabase.sendNewPasswordToCorrectEmail(ForgetPasswordActivity.this, recipient_email);
            } else {
                Snackbar.make(v, NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
            }
        }
    }
}