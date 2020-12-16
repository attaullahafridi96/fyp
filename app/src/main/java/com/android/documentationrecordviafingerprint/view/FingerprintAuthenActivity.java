package com.android.documentationrecordviafingerprint.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricConstants;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.android.documentationrecordviafingerprint.R;

import java.util.concurrent.Executor;

public class FingerprintAuthenActivity extends AppCompatActivity {
    private static final Intent activity_opener = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_authentication);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkBiometricAuthentication();
            }
        }, 800);
    }

    private void checkBiometricAuthentication() {
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(FingerprintAuthenActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                switch (errorCode) {
                    case BiometricConstants.ERROR_LOCKOUT:
                        Toast.makeText(FingerprintAuthenActivity.this, "Too many attempts, Try again later after 30 seconds.", Toast.LENGTH_LONG).show();
                        break;
                    case BiometricConstants.ERROR_LOCKOUT_PERMANENT:
                        Toast.makeText(FingerprintAuthenActivity.this, errString + " Now lock your device and unlock it with Pin, Pattern or Password", Toast.LENGTH_LONG).show();
                        break;
                }
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                finish();
                startActivity(activity_opener.setClass(FingerprintAuthenActivity.this, LoginActivity.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Fingerprint Authentication")
                .setSubtitle("Verify your Biometric Credential")
                .setNegativeButtonText("Exit")
                .build();

        BiometricManager biometricManager = BiometricManager.from(this);
        //This switch check fingerprint sensor availability in device
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                showMsgDialog("No fingerprint features available on this device");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                showMsgDialog("Fingerprint features are currently unavailable");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                showSecuritySettingsMsgDialog("The user hasn't set any fingerprint credentials in this device." +
                        "\n\nPlease setup device fingerprint credentials to use this application.");
                break;
        }
    }

    private void showMsgDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FingerprintAuthenActivity.this ,R.style.MyDailogTheme);
        builder.setTitle("WARNING");
        builder.setMessage(message);
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });
        builder.create().show();
    }

    private void showSecuritySettingsMsgDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FingerprintAuthenActivity.this, R.style.MyDailogTheme);
        builder.setTitle("Enroll Fingerprint");
        builder.setMessage(message);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });
        builder.setPositiveButton("Goto ENROLLMENT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(activity_opener.setAction(Settings.ACTION_SECURITY_SETTINGS));
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }
}