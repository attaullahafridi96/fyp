package com.android.documentationrecordviafingerprint.uihelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.documentationrecordviafingerprint.R;

public final class CcustomMsgDialog extends AlertDialog {

    public CcustomMsgDialog(final Context context, String title, int msg_id) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_msg_dialog, null);
        builder.setView(view);
        TextView titleTV = view.findViewById(R.id.custom_title);
        titleTV.setText(title);
        TextView msg = view.findViewById(R.id.custom_msg_tv);
        msg.setText(msg_id);
        builder.create().show();
    }

    public CcustomMsgDialog(final Context context, String title, String msg) {
        super(context);
        Builder builder = new Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_msg_dialog, null);
        builder.setView(view);
        TextView titleTV = view.findViewById(R.id.custom_title);
        titleTV.setText(title);
        TextView msgtv = view.findViewById(R.id.custom_msg_tv);
        msgtv.setText(msg);
        builder.create().show();
    }
}
