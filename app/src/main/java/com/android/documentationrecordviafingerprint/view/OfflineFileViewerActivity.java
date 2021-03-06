package com.android.documentationrecordviafingerprint.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.chrisbanes.photoview.PhotoView;

public class OfflineFileViewerActivity extends AppCompatActivity {
    private PDFView pdfView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_viewer);
        Intent it = getIntent();
        String file_name = it.getStringExtra("FILE_NAME");
        final String file_uri = it.getStringExtra("URI");
        String file_extension = it.getStringExtra("FILE_EXTENSION");
        TextView document_title = findViewById(R.id.document_title);
        document_title.setText(StringOperations.capitalizeString(file_name));
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
                PDFView.Configurator configurator = pdfView.fromUri(Uri.parse(file_uri));
                configurator.onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        progressBar.setVisibility(View.GONE);
                    }
                }).scrollHandle(new DefaultScrollHandle(this)).onRender(new OnRenderListener() {
                    @Override
                    public void onInitiallyRendered(int pages, float pageWidth, float pageHeight) {
                        pdfView.fitToWidth(); // optionally pass page number
                    }
                }).load();
                break;
            case "jpeg":
            case "jpg":
            case "png":
            case "bmp":
                photoView.setImageURI(Uri.parse(file_uri));
                photoView.setVisibility(View.VISIBLE);
                break;
            case "gif":
                webView.setVisibility(View.VISIBLE);
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadUrl(file_uri);
                break;
            default:
                nothingShow.setVisibility(View.VISIBLE);
                break;
        }
    }
}