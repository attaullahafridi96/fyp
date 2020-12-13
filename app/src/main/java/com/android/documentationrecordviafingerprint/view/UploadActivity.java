package com.android.documentationrecordviafingerprint.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomInputDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.google.android.material.snackbar.Snackbar;

public class UploadActivity extends AppCompatActivity implements IMyConstants {

    private Context context;
    private static Uri file_uri;
    private LinearLayout selected_file;
    private static String file_type, file_identifier, file_extension, file_name, formatted_file_size, file_icon_uri, file_path;
    private static final Intent activity_opener = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        context = UploadActivity.this;
        selected_file = findViewById(R.id.selected_file);
        /*findViewById(R.id.take_capture_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Not Ready Yet", Toast.LENGTH_SHORT).show();
            }
        });*/
        findViewById(R.id.file_chooser_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 99);
                } else {
                    selectFile();
                }
            }
        });
        findViewById(R.id.upload_file_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file_uri != null) {
                    final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, getResources().getString(R.string.file_upload_msg));
                    customConfirmDialog.setOkBtn(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CheckInternetConnectivity.isInternetConnected(context)) {
                                MyFirebaseDatabase.requestFileUpload(UploadActivity.this,
                                        file_icon_uri, file_name.toLowerCase(),
                                        file_extension, file_type, file_uri,
                                        file_identifier.toLowerCase(), formatted_file_size);
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                            }
                            customConfirmDialog.dismissDialog();
                        }
                    });
                } else {
                    new CustomMsgDialog(context, "Unsupported file", "Can't upload this type of file");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 99 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new CustomMsgDialog(context, "Permission Granted", "Now you can Upload Files");
        } else {
            new CustomMsgDialog(context, "Permission Denied", "READ PERMISSION REQUIRED!\n\nThis permission is required for getting files from your device, Please grant storage access Permission");
        }
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimetypes = {"application/pdf", "text/*", "image/*", ".csv", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 89);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 89 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            file_uri = data.getData();
            file_path = data.getData().getPath();
            createFileExtension(file_uri);
            @SuppressLint("Recycle")
            Cursor cursor =
                    getContentResolver().query(file_uri, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            cursor.moveToFirst();
            file_name = cursor.getString(nameIndex);
            long filesize = cursor.getLong(sizeIndex);
            formatted_file_size = android.text.format.Formatter.formatShortFileSize(context, filesize);
            file_identifier = StringOperations.createFileIdentifier(file_name);
            drawSelectedFileInfo(file_extension);
        } else {
            clearData();
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void createFileExtension(Uri file_uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        file_extension = mimeTypeMap.getExtensionFromMimeType(cr.getType(file_uri));
    }

    private TextView selected_filename_tv;

    @SuppressLint("UseCompatLoadingForDrawables")
    private void drawSelectedFileInfo(final String file_extn) {
        int imageNo;
        switch (file_extn) {
            case "pdf":
                imageNo = R.drawable.pdf_96px;
                file_type = FILE_TYPE_DOCS;
                file_icon_uri = FILE_ICON_URI_PDF;
                break;
            case "rtf":
                file_type = FILE_TYPE_DOCS;
                imageNo = R.drawable.microsoft_word_2019_96px;
                file_icon_uri = FILE_ICON_URI_RTF;
                break;
            case "doc":
            case "docx":
                file_type = FILE_TYPE_DOCS;
                imageNo = R.drawable.microsoft_word_96px;
                file_icon_uri = FILE_ICON_URI_WORD;
                break;
            case "pptx":
            case "ppt":
                file_type = FILE_TYPE_DOCS;
                imageNo = R.drawable.powerpoint_96px;
                file_icon_uri = FILE_ICON_URI_POWERPOINT;
                break;
            case "xls":
            case "xlsx":
            case "csv":
                file_type = FILE_TYPE_DOCS;
                imageNo = R.drawable.excel_96px;
                file_icon_uri = FILE_ICON_URI_EXCEL;
                break;
            case "txt":
                file_type = FILE_TYPE_DOCS;
                imageNo = R.drawable.txt_96px;
                file_icon_uri = FILE_ICON_URI_TXT;
                break;
            case "jpeg":
            case "jpg":
                file_type = FILE_TYPE_IMAGE;
                imageNo = R.drawable.jpg_96px;
                file_icon_uri = FILE_ICON_URI_JPEG;
                break;
            case "png":
                file_type = FILE_TYPE_IMAGE;
                imageNo = R.drawable.png_96px;
                file_icon_uri = FILE_ICON_URI_PNG;
                break;
            case "gif":
                file_type = FILE_TYPE_IMAGE;
                imageNo = R.drawable.gif_96px;
                file_icon_uri = FILE_ICON_URI_GIF;
                break;
            case "bmp":
                file_type = FILE_TYPE_IMAGE;
                imageNo = R.drawable.image_96px;
                file_icon_uri = FILE_ICON_URI_BMP;
                break;
            default:
                file_uri = null;
                imageNo = R.drawable.file_96px;
                break;
        }
        ImageView file_type_icon = findViewById(R.id.file_type_icon);
        file_type_icon.setImageDrawable(getDrawable(imageNo));
        selected_filename_tv = findViewById(R.id.selected_filename);
        selected_filename_tv.setText(file_name);
        TextView selected_file_size = findViewById(R.id.selected_file_size);
        selected_file_size.setText(formatted_file_size);
        selected_file.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(file_name);
                menu.add("Rename File").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        renameFile();
                        return true;
                    }
                });
            }
        });
        selected_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file_uri != null) {
                    activity_opener.setClass(context, OfflineFileViewerActivity.class);
                    activity_opener.putExtra("FILE_NAME", file_name);
                    activity_opener.putExtra("URI", file_uri.toString());
                    activity_opener.putExtra("FILE_EXTENSION", file_extension);
                    //activity_opener.putExtra("FILE_PATH", file_path);
                    startActivity(activity_opener);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 26)
            selected_file.setTooltipText(file_name);
        selected_file.setVisibility(View.VISIBLE);
    }

    private static String new_file_name;

    private void renameFile() {
        final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Rename");
        customInputDialog.setOkBtnListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customInputDialog.dismissDialog();
                new_file_name = customInputDialog.getInputText();
                if (StringOperations.isEmpty(new_file_name)) {
                    new CustomMsgDialog(context, "Alert", "Can't Set Empty File Name.");
                    return;
                }
                new_file_name += "." + file_extension;
                String old_file_id = StringOperations.createFileIdentifier(file_name);
                String new_file_id = StringOperations.createFileIdentifier(new_file_name);
                if (old_file_id.equalsIgnoreCase(new_file_id)) {
                    Toast.makeText(context, "Please enter different file name", Toast.LENGTH_LONG).show();
                } else {
                    file_name = new_file_name;
                    file_identifier = new_file_id;
                    selected_filename_tv.setText(file_name);
                }
            }
        });
    }

    private void clearData() {
        file_name = null;
        file_extension = null;
        file_uri = null;
        selected_file.setVisibility(View.GONE);
    }
}