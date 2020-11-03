package com.android.documentationrecordviafingerprint.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.FirebaseController;
import com.android.documentationrecordviafingerprint.controller.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.android.documentationrecordviafingerprint.uihelper.ConfirmationDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomInputDialog;
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
        String file_extension = model.getFile_extension();
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
            /*case "rtf":
            case "doc":
            case "docx":*/
            default:
                /*progressBar.setVisibility(View.VISIBLE);
                WebView webView = findViewById(R.id.webviewer);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setAllowFileAccess(true);
                webView.getSettings().setAllowFileAccessFromFileURLs(true);
                webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
                webView.getSettings().setAllowContentAccess(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                webView.loadUrl(file_uri);*/
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_viewer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.rename_item:
                    final CustomInputDialog customInputDialog = new CustomInputDialog(context, "Rename");
                    customInputDialog.setOkBtn(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new_file_name = customInputDialog.getInputText();
                            if (StringOperations.isEmpty(new_file_name)) {
                                Toast.makeText(context, "Can't Set Empty File Name", Toast.LENGTH_LONG).show();
                                return;
                            }
                            String old_file_id = StringOperations.createFileIdentifier(file_name);
                            String new_file_id = StringOperations.createFileIdentifier(new_file_name);
                            if (old_file_id.equalsIgnoreCase(new_file_id)) {
                                Toast.makeText(context, "Please enter different file name", Toast.LENGTH_LONG).show();
                            } else {
                                FirebaseController.renameFile(OnlineFileViewer.this, new_file_name, new_file_id, old_file_id);
                            }
                        }
                    });
                    break;
                case R.id.download_file_item:
                    if (CheckInternetConnectivity.isInternetConnected(context)) {
                        Intent it = new Intent();
                        it.setAction(Intent.ACTION_VIEW);
                        it.setData(Uri.parse(file_uri));
                        context.startActivity(Intent.createChooser(it, "Select App to Download File"));
                    } else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.delete_file_item:
                    final ConfirmationDialog confirmationDialog = new ConfirmationDialog(context, "Are you Sure to Delete this File from Cloud?");
                    confirmationDialog.setPositiveBtn(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CheckInternetConnectivity.isInternetConnected(context)) {
                                String file_id = StringOperations.createFileIdentifier(file_name);
                                FirebaseController.deleteFile(OnlineFileViewer.this, file_storage_key, file_id);
                                confirmationDialog.dismissDialog();
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_LONG).show();
                                confirmationDialog.dismissDialog();
                            }
                        }
                    });
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(OnlineFileViewer.this, "Error", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
