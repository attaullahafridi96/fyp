package com.android.documentationrecordviafingerprint.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.model.UserNote;
import com.google.android.material.snackbar.Snackbar;

public class TextEditorActivity extends AppCompatActivity {
    private EditText note_details;
    private UserNote model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);
        //////////////ToolBar code/////////////
        Toolbar myToolbar = findViewById(R.id.editor_activity_toolbar);
        setSupportActionBar(myToolbar);
        /////////////ToolBar code/////////////
        Intent it = getIntent();
        model = (UserNote) it.getSerializableExtra("USER_NOTE");
        note_details = findViewById(R.id.noteDetails);
    }

    private MenuItem editMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_editor_menu, menu);
        editMenuItem = menu.findItem(R.id.editor_rename_item);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.editor_save_item:
                    saveNotes();
                    break;
                case R.id.editor_rename_item:
                    renameNotes();
                    break;
                case R.id.editor_delete_item:
                    deleteNotes();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(TextEditorActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void saveNotes() {
    }

    private void deleteNotes() {
    }

    private void renameNotes() {
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