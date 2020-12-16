package com.android.documentationrecordviafingerprint.uihelper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.documentationrecordviafingerprint.R;

public final class CustomConfirmDialog extends AlertDialog {
    private final Button ok_btn, neg_btn;
    private final AlertDialog alertDialog;

    public CustomConfirmDialog(@NonNull Context context, String msg) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_confirmation_dialog, null);
        builder.setView(view);
        TextView msgTv = view.findViewById(R.id.custom_confirm_msg_tv);
        msgTv.setText(msg);
        neg_btn = view.findViewById(R.id.btnNeg);
        ok_btn = view.findViewById(R.id.btnPos);
        alertDialog = builder.create();
        neg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        alertDialog.show();
    }

    public CustomConfirmDialog setOkBtnText(String text) {
        ok_btn.setText(text);
        return this;
    }

    public CustomConfirmDialog dangerBtn() {
        ok_btn.setBackgroundResource(R.drawable.danger_btn_design);
        return this;
    }

    public void setOkBtnListener(View.OnClickListener listener) {
        ok_btn.setOnClickListener(listener);
    }

    public void setNegBtnListener(View.OnClickListener listener) {
        neg_btn.setOnClickListener(listener);
    }

    public void dismissDialog() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        });
    }
}
