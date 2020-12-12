package com.android.documentationrecordviafingerprint.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.helper.CryptUtil;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.NotesDownloader;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.UserNotes;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomInputDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;

public class NotesEditorActivity extends AppCompatActivity implements IMyConstants {
    private EditText note_data_ed, notes_title_ed;
    private TextView editor_title;
    private UserNotes model;
    private Context context;
    private Menu toolbar_menu;
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_editor);
        context = NotesEditorActivity.this;
        //////////////ToolBar code/////////////
        Toolbar myToolbar = findViewById(R.id.editor_activity_toolbar);
        setSupportActionBar(myToolbar);
        /////////////ToolBar code/////////////
        Intent it = getIntent();
        model = (UserNotes) it.getSerializableExtra(EXTRA_USER_NOTES);

        notes_title_ed = findViewById(R.id.notes_title);
        note_data_ed = findViewById(R.id.notes_data);
        editor_title = findViewById(R.id.editor_title);

        if (model != null) {
            editor_title.setText(StringOperations.capitalizeString(model.getName()));
            notes_title_ed.setEnabled(false);
            notes_title_ed.setText(model.getName());
            note_data_ed.setText(decryptNotesData(model.getNotes_data()));
        }
    }

    private String getNotesTitle() {
        return notes_title_ed.getText().toString();
    }

    private String getNotesData() {
        return note_data_ed.getText().toString();
    }

    private String encryptNotesData(final String data) {
        return CryptUtil.encrypt(data);
    }

    private String decryptNotesData(final String data) {
        return CryptUtil.decrypt(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_editor_menu, menu);
        if (model == null) {
            menu.findItem(R.id.editor_rename_menu_item).setVisible(false);
            menu.findItem(R.id.editor_delete_menu_item).setVisible(false);
        }
        toolbar_menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editor_upload_menu_item:
                uploadNotes();
                break;
            case R.id.editor_rename_menu_item:
                renameNotes();
                break;
            case R.id.editor_save_menu_item:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    NotesDownloader.saveNotes(context, getNotesTitle().toLowerCase().trim(), getNotesData());
                }
                break;
            case R.id.editor_delete_menu_item:
                deleteNotes();
                break;
            default:
                return false;
        }
        return true;
    }

    private void uploadNotes() {
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            String notes_name = getNotesTitle().toLowerCase().trim();
            if (notes_name.isEmpty()) {
                new CustomMsgDialog(context, "Notes title Empty!", "Can not leave Notes title empty");
            } else {
                String file_id = StringOperations.createFileIdentifier(notes_name);
                String notes_data = getNotesData(); //No need of trim, notes may be empty
                String file_size = android.text.format.Formatter.formatShortFileSize(context, notes_data.getBytes().length);
                String upload_date = simpleDateFormat.format(System.currentTimeMillis());
                final String encrypted_notes = encryptNotesData(notes_data);
                UserNotes userNotes = new UserNotes(notes_name, encrypted_notes, file_id, file_size, upload_date);
                MyFirebaseDatabase.uploadNotes(context, userNotes, toolbar_menu, notes_title_ed, editor_title);
            }
        } else {
            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
        }
    }

    private void deleteNotes() {
        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, getResources().getString(R.string.notes_delete_msg));
        customConfirmDialog.setBtnText("Delete")
                .dangerBtn()
                .setOkBtn(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            String file_name = getNotesTitle().toLowerCase().trim();
                            String file_id = StringOperations.createFileIdentifier(file_name);
                            MyFirebaseDatabase.deleteNotes(NotesEditorActivity.this, file_id, true);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                        }
                        customConfirmDialog.dismissDialog();
                    }
                });
    }

    private String new_notes_name;

    private void renameNotes() {
        try {
            final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Rename");
            customInputDialog.setOkBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customInputDialog.dismissDialog();
                    new_notes_name = customInputDialog.getInputText();
                    if (StringOperations.isEmpty(new_notes_name)) {
                        new CustomMsgDialog(context, "Alert", "Can't Set Empty Notes Name.");
                        return;
                    }
                    String old_notes_id = model.getId();
                    String new_notes_id = StringOperations.createFileIdentifier(new_notes_name);
                    if (old_notes_id.equalsIgnoreCase(new_notes_id)) {
                        Toast.makeText(context, "Please enter different notes name", Toast.LENGTH_LONG).show();
                    } else {
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            MyFirebaseDatabase.renameNotesOnCloud(NotesEditorActivity.this, new_notes_name, new_notes_id, model, true);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new CustomMsgDialog(context, "Permissions Granted", "Now you can Download Notes");
            } else {
                new CustomMsgDialog(context, "Permissions Denied", "READ|WRITE PERMISSION REQUIRED!\n\nThis permission is required for saving notes on your device, Please grant Permissions to Download Notes");
            }
        }
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