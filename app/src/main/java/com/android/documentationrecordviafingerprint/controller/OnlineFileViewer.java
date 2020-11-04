package com.android.documentationrecordviafingerprint.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.FirebaseModel;
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomInputDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class OnlineFileViewer extends AppCompatActivity {
    private static PDFView pdfView;
    private ProgressBar progressBar;
    private static String file_name, file_storage_key;
    private Context context;
    private String file_uri;
    private static String new_file_name;
    private static String file_extension;

    @SuppressLint({"SetJavaScriptEnabled", "StaticFieldLeak"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_viewer);
        context = OnlineFileViewer.this;
        //////////////ToolBar code/////////////
        Toolbar myToolbar = findViewById(R.id.viewer_toolbar);
        setSupportActionBar(myToolbar);
        /////////////ToolBar code/////////////
        Intent it = getIntent();
        UserFile model = (UserFile) it.getSerializableExtra("USER_FILE");
        file_name = model.getFile_name();
        file_storage_key = model.getFile_storage_key();
        file_uri = model.getFile_uri();
        file_extension = model.getFile_extension();
        TextView document_title = findViewById(R.id.document_title);
        document_title.setText(file_name);
        ImageButton back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pdfView = findViewById(R.id.pdf_view);
        progressBar = findViewById(R.id.pbar);
        PhotoView photoView = findViewById(R.id.imageview_viewer);

        switch (file_extension) {
            case "pdf":
                progressBar.setVisibility(View.VISIBLE);
                pdfView.setVisibility(View.VISIBLE);
                pdfView.useBestQuality(true);
                new AsyncTask<Void, Void, Void>() {
                    PDFView.Configurator configurator;

                    @SuppressLint("StaticFieldLeak")
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            InputStream input = new URL(file_uri).openStream();
                            configurator = pdfView.fromStream(input);
                        } catch (IOException e) {
                            Toast.makeText(context, "Error in Load", Toast.LENGTH_SHORT).show();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        configurator.onLoad(new OnLoadCompleteListener() {
                            @Override
                            public void loadComplete(int nbPages) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }).scrollHandle(new DefaultScrollHandle(context)).onRender(new OnRenderListener() {
                            @Override
                            public void onInitiallyRendered(int pages, float pageWidth, float pageHeight) {
                                pdfView.fitToWidth(); // optionally pass page number
                            }
                        }).load();
                    }
                }.execute();
                break;
            case "jpeg":
            case "jpg":
            case "png":
            case "bmp":
                Glide.with(photoView.getContext()).load(file_uri).into(photoView);
                photoView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_viewer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new CustomMsgDialog(context, "Permissions Granted", "Now you can Download File");
                } else {
                    new CustomMsgDialog(context, "Permissions Denied", "Please Grant Permissions to Download File");
                }
            }
        }
    }

    public void startDownload(String url) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle("Download");
            request.setDescription("Downloading file...");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file_name);
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.rename_item:
                    renameFile();
                    break;
                case R.id.download_file_item:
                    downloadFile();
                    break;
                case R.id.delete_file_item:
                    deleteFile();
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(OnlineFileViewer.this, "Error", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void renameFile() {
        final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Rename");
        customInputDialog.setOkBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customInputDialog.dismissDialog();
                new_file_name = customInputDialog.getInputText();
                new_file_name += "." + file_extension;
                if (StringOperations.isEmpty(new_file_name)) {
                    Toast.makeText(context, "Can't Set Empty File Name", Toast.LENGTH_LONG).show();
                    return;
                }
                String old_file_id = StringOperations.createFileIdentifier(file_name);
                String new_file_id = StringOperations.createFileIdentifier(new_file_name);
                if (old_file_id.equalsIgnoreCase(new_file_id)) {
                    Toast.makeText(context, "Please enter different file name", Toast.LENGTH_LONG).show();
                } else {
                    if (CheckInternetConnectivity.isInternetConnected(context)) {
                        FirebaseModel.renameFile(OnlineFileViewer.this, new_file_name, new_file_id, old_file_id);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void downloadFile() {
        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, getResources().getString(R.string.download_msg));
        customConfirmDialog.setPositiveBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        startDownload(file_uri);
                    }
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG).show();
                }
                customConfirmDialog.dismissDialog();
            }
        });
        customConfirmDialog.setPosBtnText("Download");
    }

    private void deleteFile() {
        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context,getResources().getString(R.string.delete_msg));
        customConfirmDialog.setPositiveBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    String file_id = StringOperations.createFileIdentifier(file_name);
                    FirebaseModel.deleteFile(OnlineFileViewer.this, file_storage_key, file_id);
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG).show();
                }
                customConfirmDialog.dismissDialog();
            }
        });
    }
}