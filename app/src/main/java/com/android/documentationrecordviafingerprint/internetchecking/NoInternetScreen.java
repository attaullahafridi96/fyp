package com.android.documentationrecordviafingerprint.internetchecking;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.View.LoginActivity;

public class NoInternetScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet_screen);

        Button close_btn = findViewById(R.id.close_btn);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);     //Stop App
            }
        });

        new Thread() {
            /*This thread will continuously check for an active connection*/
            @Override
            public void run() {
                boolean isInternetConnected;
                while (true) { //when it true loop will end
                    isInternetConnected = CheckInternetConnectivity.isInternetConnected(NoInternetScreen.this);
                    if (isInternetConnected) {
                        startActivity(new Intent(NoInternetScreen.this, LoginActivity.class));
                        finish();
                        return;
                    }
                    SystemClock.sleep(3000);
                }
            }
        }.start();
    }
}