package com.android.documentationrecordviafingerprint.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;

public class AppSettingsActivity extends AppCompatActivity {
    private Context context;

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
                        MyFirebaseDatabase.deleteAllUserData(AppSettingsActivity.this);
                        break;
                    case 4:
                        MyFirebaseDatabase.deleteUserAccount(AppSettingsActivity.this);
                        break;
                    default:
                        break;
                }
            }
        });
    }
}