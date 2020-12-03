package com.android.documentationrecordviafingerprint.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.IMyConstants;
import com.android.documentationrecordviafingerprint.model.UserNotes;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomProgressDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomToast;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;

public class NotesEditorActivity extends AppCompatActivity implements IMyConstants {
    private EditText note_data_ed, notes_title_ed;
    private TextView editor_title;
    private UserNotes model;
    private Context context;
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
            editor_title.setText(model.getName());
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
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editor_save_menu_item:
                saveNotes();
                break;
            case R.id.editor_rename_menu_item:
                renameNotes();
                break;
            case R.id.editor_download_menu_item:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    downloadNotes();
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

    private void saveNotes() {
        if (CheckInternetConnectivity.isInternetConnected(context)) {
            String file_name = getNotesTitle().toLowerCase().trim();
            if (file_name.isEmpty()) {
                new CustomMsgDialog(context, "Notes title Empty!", "Can not leave Notes title empty");
            } else {
                String file_id = StringOperations.createFileIdentifier(file_name);
                String notes_data = getNotesData(); //No need of trim, notes may be empty
                String file_size = android.text.format.Formatter.formatShortFileSize(context, notes_data.getBytes().length);
                String upload_date = simpleDateFormat.format(System.currentTimeMillis());
                final String encrypted_notes = encryptNotesData(notes_data);
                UserNotes userNotes = new UserNotes(file_name, encrypted_notes, file_id, file_size, upload_date, "");
                MyFirebaseDatabase.uploadNotes(context, userNotes);
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

    private void renameNotes() {

    }

    private void downloadNotes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save File As")
                .setSingleChoiceItems(new String[]{"Text file", "PDF file", "Word file"}, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selected_item) {
                        switch (selected_item) {
                            case 0:
                                saveFileOnDevice(FILE_EXTENSION_TEXT);
                                break;
                            case 1:
                                saveFileOnDevice(FILE_EXTENSION_PDF);
                                break;
                            case 2:
                                saveFileOnDevice(FILE_EXTENSION_WORD);
                        }
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void saveFileOnDevice(final String file_type) {
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
            if (!dir.exists())
                dir.mkdirs();
            String full_file_name = getNotesTitle() + file_type;
            String file_data = getNotesData();
            File file = new File(dir, full_file_name);
            switch (file_type) {
                case FILE_EXTENSION_TEXT:
                    FileWriter writer = new FileWriter(file);
                    writer.append(file_data);
                    writer.flush();
                    writer.close();
                    new CustomMsgDialog(context, "File Saved",
                            "Saved as " + full_file_name + " in Downloads folder");
                    break;
                case FILE_EXTENSION_PDF:
                    saveAsPdf(file, file_data);
                    break;
                case FILE_EXTENSION_WORD:
                    saveAsMsWord(dir, full_file_name, file_data);
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show();
        }
    }

    private void saveAsPdf(final File file, final String data) {
        try {
            com.itextpdf.text.Document mDoc = new com.itextpdf.text.Document();
            PdfWriter.getInstance(mDoc, new FileOutputStream(file));
            mDoc.open();
            mDoc.add(new Paragraph(data));
            mDoc.close();
            new CustomMsgDialog(context, "File Saved",
                    "Saved as " + file.getName() + " in Downloads folder");
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void saveAsMsWord(final File dir, final String full_file_name, final String file_data) {
        new AsyncTask<Void, Void, Boolean>() {
            private final CustomProgressDialog customProgressDialog = new CustomProgressDialog(context, "Processing . . .");

            @Override
            protected void onPreExecute() {
                customProgressDialog.showDialog();
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    com.aspose.words.Document doc = new Document();
                    DocumentBuilder documentBuilder = new DocumentBuilder(doc);
                    documentBuilder.write(file_data);
                    doc.save(dir + "/" + full_file_name);
                    return true;
                } catch (final Exception e) {
                    CustomToast.makeToast(context, "Error: " + e);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                customProgressDialog.dismissDialog();
                if (result) {
                    new CustomMsgDialog(context, "File Saved",
                            "Saved as " + full_file_name + " in Downloads folder");
                } else {
                    new CustomMsgDialog(context, "Saved Failed", "Unable to Save file");
                }
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new CustomMsgDialog(context, "Permissions Granted", "Now you can Save File");
            } else {
                new CustomMsgDialog(context, "Permissions Denied", "Please Grant Permissions to Save File");
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