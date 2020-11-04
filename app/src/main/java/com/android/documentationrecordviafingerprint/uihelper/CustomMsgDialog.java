package com.android.documentationrecordviafingerprint.uihelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.documentationrecordviafingerprint.R;

public final class CustomMsgDialog extends AlertDialog {
    private final AlertDialog alertDialog;

    public CustomMsgDialog(final Context context, String title, String msg) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_msg_dialog, null);
        builder.setView(view);
        TextView titleTV = view.findViewById(R.id.custom_msg_title);
        titleTV.setText(title);
        TextView msg_tv = view.findViewById(R.id.custom_msg_tv);
        msg_tv.setText(msg);
        alertDialog = builder.create();
        Button btn = view.findViewById(R.id.msgbtnOK);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public CustomMsgDialog(final Context context, String title, int msg_id) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_msg_dialog, null);
        builder.setView(view);
        TextView titleTV = view.findViewById(R.id.custom_msg_title);
        titleTV.setText(title);
        TextView msg = view.findViewById(R.id.custom_msg_tv);
        msg.setText(msg_id);
        alertDialog = builder.create();
        Button btn = view.findViewById(R.id.msgbtnOK);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

}
