package com.android.documentationrecordviafingerprint.uihelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.documentationrecordviafingerprint.R;

public class CustomProgressbar extends AlertDialog {
    private final Button pauseBtn;
    private final Button cancelBtn;
    private final ProgressBar progressBar;
    private final AlertDialog alertDialog;

    public CustomProgressbar(@NonNull Context context) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_progressbar, null);
        builder.setView(view);
        progressBar = view.findViewById(R.id.horizontal_pbar);
        pauseBtn = view.findViewById(R.id.btnPause);
        cancelBtn = view.findViewById(R.id.btnCancel);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public CustomProgressbar setPauseBtn(View.OnClickListener listener) {
        pauseBtn.setOnClickListener(listener);
        return this;
    }

    @SuppressLint("SetTextI18n")
    public void setResumeText() {
        pauseBtn.setText("Resume");
    }

    @SuppressLint("SetTextI18n")
    public void setPauseText() {
        pauseBtn.setText("Pause");
    }

    public CustomProgressbar setCancelBtn(View.OnClickListener listener) {
        cancelBtn.setOnClickListener(listener);
        return this;
    }

    public void dismissDialog() {
        alertDialog.dismiss();
    }
}