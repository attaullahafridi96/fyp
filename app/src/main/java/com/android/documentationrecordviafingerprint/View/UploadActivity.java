package com.android.documentationrecordviafingerprint.View;

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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.controller.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.google.android.material.snackbar.Snackbar;

public class UploadActivity extends AppCompatActivity {

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
                    final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, getResources().getString(R.string.upload_msg));
                    customConfirmDialog.setPositiveBtn(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CheckInternetConnectivity.isInternetConnected(context)) {
                                MyFirebaseDatabase.requestFileUpload(context, file_icon_uri, file_name.toLowerCase(), file_extension, file_type, file_uri, file_identifier.toLowerCase(), formatted_file_size);
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG).show();
                            }
                            customConfirmDialog.dismissDialog();
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
        String[] mimetypes = {"application/pdf", "text/*", "image/*",".csv", "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 89);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
            Toast.makeText(this, "No file Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void createFileExtension(Uri file_uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        file_extension = mimeTypeMap.getExtensionFromMimeType(cr.getType(file_uri));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    private void drawSelectedFileInfo(final String file_extn) {
        int imageNo;
        switch (file_extn) {
            case "pdf":
                imageNo = R.drawable.pdf_96px;
                file_type = "doc";
                file_icon_uri = getResources().getString(R.string.pdf_icon);
                break;
            case "rtf":
                file_type = "doc";
                imageNo = R.drawable.microsoft_word_2019_96px;
                file_icon_uri = getResources().getString(R.string.rtf_icon);
                break;
            case "doc":
            case "docx":
                file_type = "doc";
                imageNo = R.drawable.microsoft_word_96px;
                file_icon_uri = getResources().getString(R.string.word_icon);
                break;
            case "pptx":
            case "ppt":
                file_type = "doc";
                imageNo = R.drawable.powerpoint_96px;
                file_icon_uri = getResources().getString(R.string.powerpoint_icon);
                break;
            case "xls":
            case "xlsx":
            case "csv":
                file_type = "doc";
                imageNo = R.drawable.excel_96px;
                file_icon_uri = getResources().getString(R.string.excel_icon);
                break;
            case "jpeg":
            case "jpg":
                file_type = "image";
                imageNo = R.drawable.jpg_96px;
                file_icon_uri = getResources().getString(R.string.jgep_icon);
                break;
            case "png":
                file_type = "image";
                imageNo = R.drawable.png_96px;
                file_icon_uri = getResources().getString(R.string.png_icon);
                break;
            case "gif":
                file_type = "image";
                imageNo = R.drawable.gif_96px;
                file_icon_uri = getResources().getString(R.string.gif_icon);
                break;
            case "bmp":
                file_type = "image";
                imageNo = R.drawable.image_96px;
                file_icon_uri = getResources().getString(R.string.image_icon);
                break;
            default:
                file_type = "doc";
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
                        /*StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
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
                        }*/
                        new CustomMsgDialog(context, "Can't open this type of file", getResources().getString(R.string.canNotOpenMsg));
                    } else {
                        activity_opener.setClass(context, OfflineFileViewerActivity.class);
                        activity_opener.putExtra("FILE_NAME", file_name);
                        activity_opener.putExtra("URI", file_uri.toString());
                        activity_opener.putExtra("FILE_EXTENSION", file_extension);
                        activity_opener.putExtra("FILE_PATH", file_path);
                        startActivity(activity_opener);
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