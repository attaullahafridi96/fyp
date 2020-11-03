package com.android.documentationrecordviafingerprint.main;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.FirebaseController;
import com.android.documentationrecordviafingerprint.controller.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.io.File;

public final class MyFilesAdapter
        extends FirebaseRecyclerAdapter<UserFile, MyFilesAdapter.ViewHolder> {
    private final Context context;
    private static final Intent activity_opener = new Intent();
    private int lastPosition = -1;

    public MyFilesAdapter(Context context, @NonNull FirebaseRecyclerOptions<UserFile> options) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_design, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final UserFile model) {
        Glide.with(holder.file_type_icon.getContext()).load(model.getImage_uri()).into(holder.file_type_icon);
        holder.filename.setText(model.getFile_name());
        holder.file_size.setText(model.getFile_size());
        holder.selected_file.setTooltipText(model.getFile_name());
        holder.download_file_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnectivity.isInternetConnected(context)) {
                    Intent it = new Intent();
                    it.setAction(Intent.ACTION_VIEW);
                    it.setData(Uri.parse(model.getFile_uri()));
                    context.startActivity(Intent.createChooser(it, "Select App to Download File"));
                } else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.delete_file_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(context, "Are you Sure to Delete this File from Cloud?");
                customConfirmDialog.setPositiveBtn(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CheckInternetConnectivity.isInternetConnected(context)) {
                            String file_id = StringOperations.createFileIdentifier(model.getFile_name());
                            FirebaseController.deleteFile(context, model.getFile_storage_key(), file_id);
                            customConfirmDialog.dismissDialog();
                        } else {
                            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                            customConfirmDialog.dismissDialog();
                        }
                    }
                });
            }
        });
        holder.selected_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getFile_uri() != null) {
                    if (model.getFile_extension().equals("doc") || model.getFile_extension().equals("docx") || model.getFile_extension().equals("rtf")) {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        builder.detectFileUriExposure();
                        Uri docUri = FileProvider.getUriForFile(context, "com.android.documentationrecordviafingerprint.provider",
                                new File(model.getFile_uri()));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(docUri, "application/msword");
                        try {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            context.startActivity(Intent.createChooser(intent, "Open With.."));
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, "No application to open file", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        activity_opener.setClass(context, OnlineFileViewer.class);
                        activity_opener.putExtra("USER_FILE", model);
                        context.startActivity(activity_opener);
                    }
                }
            }
        });
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView file_type_icon;
        private final TextView filename;
        private final TextView file_size;
        private final LinearLayout selected_file;
        private final ImageButton download_file_btn;
        private final ImageButton delete_file_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selected_file = itemView.findViewById(R.id.selected_file);
            file_type_icon = itemView.findViewById(R.id.file_type_icon);
            filename = itemView.findViewById(R.id.selected_filename);
            file_size = itemView.findViewById(R.id.selected_file_size);
            download_file_btn = itemView.findViewById(R.id.download_file_btn);
            delete_file_btn = itemView.findViewById(R.id.delete_file_btn);
        }
    }
}
