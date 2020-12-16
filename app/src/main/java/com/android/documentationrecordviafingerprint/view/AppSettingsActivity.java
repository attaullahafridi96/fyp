package com.android.documentationrecordviafingerprint.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomInputDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomToast;
import com.google.android.material.snackbar.Snackbar;

public class AppSettingsActivity extends AppCompatActivity implements IMyConstants {
    private Context context;
    private CustomConfirmDialog customConfirmDialog;
    private String newInput;
    private static final int MIN_PASSWORD_CHARS = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        context = AppSettingsActivity.this;
        ListView listView = findViewById(R.id.settings_list_view);
        final String[] array = getResources().getStringArray(R.array.settings_items);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.settings_item_design, array);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        changeFirstName();
                        break;
                    case 1:
                        changeLastName();
                        break;
                    case 2:
                        changePassword();
                        break;
                    case 3:
                        removeAllData();
                        break;
                    case 4:
                        deleteAccount();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void changeFirstName() {
        final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Change First Name");
        customInputDialog.setInputHint("Enter first name")
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customInputDialog.dismissDialog();
                        newInput = customInputDialog.getInputText();
                        if (StringOperations.isEmpty(newInput)) {
                            new CustomMsgDialog(context, "Alert", "Can't Set Empty Name.");
                            return;
                        }
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            newInput = StringOperations.capitalizeString(newInput);
                            MyFirebaseDatabase.changeFirstName(AppSettingsActivity.this, newInput);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void changeLastName() {
        final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Change Last Name");
        customInputDialog.setInputHint("Enter last name")
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customInputDialog.dismissDialog();
                        newInput = customInputDialog.getInputText();
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            newInput = StringOperations.capitalizeString(newInput);
                            MyFirebaseDatabase.changeLastName(AppSettingsActivity.this, newInput);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void changePassword() {
        final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Change Password");
        customInputDialog.setInputHint("Enter a new Password")
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customInputDialog.dismissDialog();
                        newInput = customInputDialog.getInputText();
                        if (StringOperations.isEmpty(newInput)) {
                            new CustomMsgDialog(context, "Alert", "Can't Set Empty Password.");
                            return;
                        }
                        if (newInput.length() < MIN_PASSWORD_CHARS) {
                            CustomToast.makeToast(context, "Password must be " + MIN_PASSWORD_CHARS + " characters or longer", Toast.LENGTH_LONG);
                            return;
                        }
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            newInput = StringOperations.toMD5String(newInput);
                            MyFirebaseDatabase.changePassword(AppSettingsActivity.this, newInput);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void removeAllData() {
        customConfirmDialog = new CustomConfirmDialog(context, "WARNING!!!" +
                "\n\nAll your data will be deleted and can not be recovered!" +
                "\n\nAre you sure to delete all your data?");
        customConfirmDialog.setOkBtnText("Delete All Data")
                .dangerBtn()
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            MyFirebaseDatabase.deleteUserAllData(AppSettingsActivity.this);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                        }
                        customConfirmDialog.dismissDialog();
                    }
                });
    }

    private void deleteAccount() {
        customConfirmDialog = new CustomConfirmDialog(context, "WARNING!!!" +
                "\n\nAll your data will be deleted and can not be recovered!" +
                "\n\nAre you sure to delete your account?");
        customConfirmDialog.setOkBtnText("Delete Account")
                .dangerBtn()
                .setOkBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            MyFirebaseDatabase.deleteUserAccount(AppSettingsActivity.this);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                        }
                        customConfirmDialog.dismissDialog();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, DashboardActivity.class));
        finish();
    }
}