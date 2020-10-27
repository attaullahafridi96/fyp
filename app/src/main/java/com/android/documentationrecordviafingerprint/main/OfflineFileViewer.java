package com.android.documentationrecordviafingerprint.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.documentationrecordviafingerprint.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

public class OfflineFileViewer extends AppCompatActivity {
    private PDFView pdfView;
    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files_viewer);
        Intent it = getIntent();
        String file_name = it.getStringExtra("FILE_NAME");
        final String file_uri = it.getStringExtra("URI");
        String file_extension = it.getStringExtra("FILE_EXTENSION");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(file_name);

        pdfView = findViewById(R.id.pdf_view);
        progressBar = findViewById(R.id.pbar);
        imageView = findViewById(R.id.imageview_viewer);

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
                imageView.setImageURI(Uri.parse(file_uri));
                imageView.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}