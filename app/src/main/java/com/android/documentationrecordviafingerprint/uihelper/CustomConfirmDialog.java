package com.android.documentationrecordviafingerprint.uihelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.documentationrecordviafingerprint.R;

public final class CustomConfirmDialog extends AlertDialog {
    private final Button btn;
    private final AlertDialog alertDialog;

    public CustomConfirmDialog(@NonNull Context context, String msg) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_confirmation_dialog, null);
        builder.setView(view);
        TextView msgTv = view.findViewById(R.id.custom_confirm_msg_tv);
        msgTv.setText(msg);
        Button btnNeg = view.findViewById(R.id.btnNeg);
        btn = view.findViewById(R.id.btnPos);
        alertDialog = builder.create();
        btnNeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        alertDialog.show();
    }

    public CustomConfirmDialog setBtnText(String text) {
        btn.setText(text);
        return this;
    }

    public CustomConfirmDialog dangerBtn() {
        btn.setBackgroundResource(R.drawable.red_btn_design);
        return this;
    }

    public void setPositiveBtn(View.OnClickListener listener) {
        btn.setOnClickListener(listener);
    }

    public void dismissDialog() {
        alertDialog.dismiss();
    }
}
