package com.android.documentationrecordviafingerprint.view;

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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.UserUploads;
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

public class OnlineFileViewerActivity extends AppCompatActivity implements IMyConstants {
    private static PDFView pdfView;
    private ProgressBar progressBar;
    private static String file_name, file_storage_key;
    private Context context;
    private String file_uri;
    private static String new_file_name;
    private static String file_extension;
    private static UserUploads model;


    @SuppressLint({"StaticFieldLeak", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_viewer);
        context = OnlineFileViewerActivity.this;
        //////////////ToolBar code/////////////
        Toolbar myToolbar = findViewById(R.id.viewer_toolbar);
        setSupportActionBar(myToolbar);
        /////////////ToolBar code/////////////
        Intent it = getIntent();
        model = (UserUploads) it.getSerializableExtra(EXTRA_USER_FILE);
        file_name = model.getName();
        file_storage_key = model.getFile_storage_id();
        file_uri = model.getFile_uri();
        file_extension = model.getFile_extension();
        TextView document_title = findViewById(R.id.document_title);
        document_title.setText(file_name);
        document_title.setSelected(true);
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
        WebView webView = findViewById(R.id.webview);
        TextView nothingShow = findViewById(R.id.nothingShow);
        switch (file_extension) {
            case "pdf":
                progressBar.setVisibility(View.VISIBLE);
                pdfView.setVisibility(View.VISIBLE);
                pdfView.useBestQuality(true);
                new AsyncTask<Void, Void, Void>() {
                    private PDFView.Configurator configurator;

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
            case "gif":
                progressBar.setVisibility(View.VISIBLE);
                webView.setVisibility(View.VISIBLE);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setDisplayZoomControls(false);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.loadUrl(file_uri);
                break;
            default:
                nothingShow.setVisibility(View.VISIBLE);
                new CustomMsgDialog(context, "Can't open this type of file", getResources().getString(R.string.canNotOpenMsg));
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
        switch (item.getItemId()) {
            case R.id.rename_item:
                renameFile();
                break;
            case R.id.download_file_item:
                downloadFile();
                break;
            case R.id.delete_file_item:
                deleteFileAndCloseActivity();
                break;
        }
        return true;
    }

    private void renameFile() {
        try {
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
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            MyFirebaseDatabase.renameFileOnCloud(OnlineFileViewerActivity.this, new_file_name, new_file_id, model);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(OnlineFileViewerActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile() {
        try {
            final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, getResources().getString(R.string.download_msg));
            customConfirmDialog.setBtnText("Download")
                    .setOkBtn(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CheckInternetConnectivity.isInternetConnected(context)) {
                                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                } else {
                                    startDownload(file_uri);
                                }
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                            }
                            customConfirmDialog.dismissDialog();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(OnlineFileViewerActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFileAndCloseActivity() {
        try {
            final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, getResources().getString(R.string.delete_msg));
            customConfirmDialog.dangerBtn()
                    .setBtnText("Delete")
                    .setOkBtn(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CheckInternetConnectivity.isInternetConnected(context)) {
                                String file_id = StringOperations.createFileIdentifier(file_name);
                                MyFirebaseDatabase.deleteFile(OnlineFileViewerActivity.this, file_storage_key, file_id,true);
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                            }
                            customConfirmDialog.dismissDialog();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(OnlineFileViewerActivity.this, "Error", Toast.LENGTH_SHORT).show();
        }
    }
}
