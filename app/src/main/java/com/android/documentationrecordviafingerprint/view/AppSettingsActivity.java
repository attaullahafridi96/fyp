package com.android.documentationrecordviafingerprint.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.google.android.material.snackbar.Snackbar;

public class AppSettingsActivity extends AppCompatActivity implements IMyConstants {
    private Context context;
    private CustomConfirmDialog customConfirmDialog;

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

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:
                        customConfirmDialog = new CustomConfirmDialog(context, "WARNING!!!" +
                                "\n\nAll your data will be deleted and can not be recovered!" +
                                "\n\nAre you sure to delete all your data?");
                        customConfirmDialog.setBtnText("Delete All Data")
                                .dangerBtn()
                                .setOkBtn(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                                            MyFirebaseDatabase.deleteUserAllData(AppSettingsActivity.this);
                                        } else {
                                            Snackbar.make(v, NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                                        }
                                        customConfirmDialog.dismissDialog();
                                    }
                                });
                        break;
                    case 4:
                        customConfirmDialog = new CustomConfirmDialog(context, "WARNING!!!" +
                                "\n\nAll your data will be deleted and can not be recovered!" +
                                "\n\nAre you sure to delete your account?");
                        customConfirmDialog.setBtnText("Delete Account")
                                .dangerBtn()
                                .setOkBtn(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                                            MyFirebaseDatabase.deleteUserAccount(AppSettingsActivity.this);
                                        } else {
                                            Snackbar.make(v, NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                                        }
                                        customConfirmDialog.dismissDialog();
                                    }
                                });
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, DashboardActivity.class));
        finish();
    }
}