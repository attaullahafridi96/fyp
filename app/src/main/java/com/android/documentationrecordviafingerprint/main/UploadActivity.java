package com.android.documentationrecordviafingerprint.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.FirebaseController;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.uihelper.ConfirmationDialog;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class UploadActivity extends AppCompatActivity {

    private Context context;
    private String file_identifier;
    private Uri file_uri;
    private String file_extension;
    private String file_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        context = UploadActivity.this;
        selected_file = findViewById(R.id.selected_file);
        findViewById(R.id.file_chooser_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        ///////////////////////////////////alert dialog
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 99);
                    }
                } else {
                    selectFile();
                }
            }
        });
        findViewById(R.id.upload_file_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file_uri != null) {
                    final ConfirmationDialog confirmationDialog = new ConfirmationDialog(context, "Do you want to Upload this File to Cloud?");
                    confirmationDialog.setPositiveBtn(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CheckInternetConnectivity.isInternetConnected(context)) {
                                new FirebaseController(context).uploadFile(file_icon_uri, file_name, file_extension, file_uri, file_identifier, formatted_file_size);
                                confirmationDialog.dismissAlertDialog();
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG).show();
                                confirmationDialog.dismissAlertDialog();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 99 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFile();
        } else {
            Toast.makeText(this, "Permission Denied, Please Grant Storage Access Permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimetypes = {"application/pdf", "text/*", "image/*", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 89);
    }

    private String file_path;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 89 && resultCode == RESULT_OK && data != null) {
            file_uri = data.getData();
            file_path = data.getData().getPath();
            @SuppressLint("Recycle")
            Cursor cursor =
                    getContentResolver().query(file_uri, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            cursor.moveToFirst();
            file_name = cursor.getString(nameIndex);
            long filesize = cursor.getLong(sizeIndex);
            formatted_file_size = android.text.format.Formatter.formatShortFileSize(context, filesize);
            createFilename_TypeAndIndentifier(file_name);
            drawSelectedFileInfo(file_extension);
        } else {
            clearData();
            Toast.makeText(this, "No file Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void createFilename_TypeAndIndentifier(String file_name) {
        String[] file_name_type = file_name.split("\\.");
        file_extension = file_name_type[1];
        file_identifier = "";
        String[] splitfilename = file_name_type[0].split("");
        for (String ch : splitfilename) {
            if (!ch.equals(" "))
                file_identifier += ch;
        }
    }

    private LinearLayout selected_file;
    private String formatted_file_size;
    private String file_icon_uri;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    private void drawSelectedFileInfo(final String file_type) {
        int imageNo;
        switch (file_type) {
            case "pdf":
                imageNo = R.drawable.pdf_96px;
                file_icon_uri = getResources().getString(R.string.pdf_icon);
                break;
            case "rtf":
                imageNo = R.drawable.microsoft_word_2019_96px;
                file_icon_uri = getResources().getString(R.string.rtf_icon);
                break;
            case "doc":
            case "docx":
                imageNo = R.drawable.microsoft_word_96px;
                file_icon_uri = getResources().getString(R.string.word_icon);
                break;
            case "jpeg":
            case "jpg":
                imageNo = R.drawable.jpg_96px;
                file_icon_uri = getResources().getString(R.string.jgep_icon);
                break;
            case "png":
                imageNo = R.drawable.png_96px;
                file_icon_uri = getResources().getString(R.string.png_icon);
                break;
            case "bmp":
                imageNo = R.drawable.image_96px;
                file_icon_uri = getResources().getString(R.string.image_icon);
                break;
            default:
                imageNo = R.drawable.note_96px;
                file_icon_uri = getResources().getString(R.string.notes_icon);
                break;
        }
        ImageView file_type_icon = findViewById(R.id.file_type_icon);
        file_type_icon.setImageDrawable(getDrawable(imageNo));
        TextView selected_filename_tv = findViewById(R.id.selected_filename);
        selected_filename_tv.setText(file_name);
        TextView selected_file_size = findViewById(R.id.selected_file_size);
        selected_file_size.setText(formatted_file_size);
        selected_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file_uri != null) {
                    if (file_extension.equals("doc") || file_extension.equals("docx") || file_extension.equals("rtf")) {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        builder.detectFileUriExposure();

                        Uri docUri = FileProvider.getUriForFile(getApplicationContext(),
                                "com.android.documentationrecordviafingerprint.provider",
                                new File(file_path)); // same as defined in Manifest file in android:authorities="com.sample.example.provider"
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(docUri, "application/msword");
                        try {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            Intent chooser = Intent.createChooser(intent, "Open With..");
                            startActivity(chooser);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(UploadActivity.this, "No application to open file", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent it = new Intent();
                        it.setClass(context, OfflineFileViewer.class);
                        it.putExtra("FILE_NAME", file_name);
                        it.putExtra("URI", file_uri.toString());
                        it.putExtra("FILE_EXTENSION", file_extension);
                        it.putExtra("FILE_PATH", file_path);
                        startActivity(it);
                    }
                }
            }
        });
        selected_file.setTooltipText(file_name);
        selected_file.setVisibility(View.VISIBLE);
    }

    private void clearData() {
        file_name = null;
        file_extension = null;
        file_uri = null;
        selected_file.setVisibility(View.GONE);
    }
}