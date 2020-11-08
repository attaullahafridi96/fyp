package com.android.documentationrecordviafingerprint.uihelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.documentationrecordviafingerprint.R;

public class CustomProgressDialog extends AlertDialog {

    private final AlertDialog alertDialog;

    public CustomProgressDialog(@NonNull Context context,String msg) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_progressdialog, null);
        builder.setView(view);
        TextView process_msg = view.findViewById(R.id.process_msg);
        process_msg.setText(msg);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissDialog() {
        alertDialog.dismiss();
    }

    public void showDialog() {
        alertDialog.show();
    }
}
