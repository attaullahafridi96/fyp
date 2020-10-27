package com.android.documentationrecordviafingerprint.fingerprint;

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
import com.android.documentationrecordviafingerprint.userlogin.Login;

import java.util.concurrent.Executor;

public class FingerprintAuthentication extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_authentication);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkBiometricAuthentication();
            }
        }, 800);
    }

    private void checkBiometricAuthentication() {
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(FingerprintAuthentication.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                switch (errorCode) {
                    case BiometricConstants.ERROR_LOCKOUT:
                        Toast.makeText(FingerprintAuthentication.this, "Too many attempts, Try again later after 30 seconds.", Toast.LENGTH_LONG).show();
                        break;
                    case BiometricConstants.ERROR_LOCKOUT_PERMANENT:
                        Toast.makeText(FingerprintAuthentication.this, errString + " Now lock your device and unlock it with Pin, Pattern or Password", Toast.LENGTH_LONG).show();
                        break;
                }
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                finish();
                startActivity(new Intent(FingerprintAuthentication.this, Login.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
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
        AlertDialog.Builder builder = new AlertDialog.Builder(FingerprintAuthentication.this);
        builder.setTitle("WARNING");
        builder.setMessage(message);
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    private void showSecuritySettingsMsgDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FingerprintAuthentication.this);
        builder.setTitle("Enroll Fingerprint");
        builder.setMessage(message);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
                System.exit(0);
            }
        });
        builder.setPositiveButton("Goto ENROLLMENT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }
}