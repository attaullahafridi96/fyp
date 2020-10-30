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
import com.android.documentationrecordviafingerprint.controller.FirebaseController;
import com.android.documentationrecordviafingerprint.controller.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.User;

public class RegistrationForm extends AppCompatActivity {
    private String enteredFirstName, enteredLastName, enteredEmailAddress, enteredPassword, enteredConfirmPassword;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_form);
        context = getApplicationContext();
        final EditText firstname = findViewById(R.id.first_name);
        final EditText lastname = findViewById(R.id.last_name);
        final EditText email = findViewById(R.id.email);
        final EditText pass = findViewById(R.id.password);
        final EditText confirmpass = findViewById(R.id.confirmpassword);

        Button create_account_btn = findViewById(R.id.create_account_btn);
        Button backToLogin_btn = findViewById(R.id.backToLogin_btn);

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
                if (enteredFirstName.length() < 3) {
                    firstname.setError("Use at least 3 characters");
                    firstname.requestFocus();
                    return;
                }
                if (!StringOperations.isValidEmail(enteredEmailAddress)) {
                    email.setError("Invalid Email");
                    email.requestFocus();
                    Toast.makeText(context, "Invalid Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (enteredPassword.length() < 3) {
                    pass.setError("Password must be 3 characters or longer");
                    pass.requestFocus();
                    return;
                }
                if (!enteredPassword.equals(enteredConfirmPassword)) {
                    Toast.makeText(context, "Password not Matched", Toast.LENGTH_SHORT).show();
                } else {
                    if (CheckInternetConnectivity.isInternetConnected(RegistrationForm.this)) {
                        User user = new User(enteredFirstName, enteredLastName, enteredEmailAddress, enteredPassword);
                        new FirebaseController(RegistrationForm.this).createNewUserAccount(user, RegistrationForm.this);
                    } else {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        backToLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationForm.this, Login.class));
                finish();
            }
        });
    }
}