package com.android.documentationrecordviafingerprint.uihelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.documentationrecordviafingerprint.R;

public class CustomInputDialog extends androidx.appcompat.app.AlertDialog {
    private final Button btnOk;
    private final AlertDialog alertDialog;
    private final EditText custom_input;

    public CustomInputDialog(@NonNull Context context, @NonNull String title) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_input_dialog, null);
        builder.setView(view);
        TextView msgTv = view.findViewById(R.id.custom_input_title);
        msgTv.setText(title);
        custom_input = view.findViewById(R.id.custom_confirm_input);
        Button btnNeg = view.findViewById(R.id.btnNo);
        btnOk = view.findViewById(R.id.btnOk);
        alertDialog = builder.create();
        btnNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        alertDialog.show();
    }

    public String getInputText() {
        return custom_input.getText().toString().trim().toLowerCase();
    }

    public CustomInputDialog setOkBtnListener(View.OnClickListener listener) {
        btnOk.setOnClickListener(listener);
        return this;
    }

    public CustomInputDialog dismissDialog() {
        alertDialog.dismiss();
        return this;
    }
}