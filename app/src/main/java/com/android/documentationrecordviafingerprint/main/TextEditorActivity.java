package com.android.documentationrecordviafingerprint.main;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.documentationrecordviafingerprint.R;
import com.google.android.material.snackbar.Snackbar;

public class TextEditorActivity extends AppCompatActivity {
    private EditText text_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);
        //////////////ToolBar code/////////////
        Toolbar myToolbar = findViewById(R.id.editor_activity_toolbar);
        setSupportActionBar(myToolbar);
        /////////////ToolBar code/////////////
        text_editor = findViewById(R.id.noteDetails);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.editor_save_item:

                    break;
            }
        } catch (Exception e) {
            Toast.makeText(TextEditorActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private static boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }
        doubleBackToExitPressedOnce = true;
        Snackbar.make(findViewById(android.R.id.content), "Press back again to exit", Snackbar.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);
    }
}