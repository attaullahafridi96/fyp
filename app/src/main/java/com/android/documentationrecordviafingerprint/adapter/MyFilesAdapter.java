package com.android.documentationrecordviafingerprint.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.UserUploads;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.view.OnlineFileViewerActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public final class MyFilesAdapter
        extends FirebaseRecyclerAdapter<UserUploads, MyFilesAdapter.ViewHolder> implements IMyConstants {
    private final Activity activity;

    private static final Intent activity_opener = new Intent();
    private int lastPosition = -1;

    public MyFilesAdapter(final Activity activity, @NonNull final FirebaseRecyclerOptions<UserUploads> options) {
        super(options);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item_design, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final UserUploads model) {
        String capitalizeFileName = model.getTitle().toUpperCase();
        Glide.with(holder.file_type_icon.getContext()).load(model.getFileIconUri()).into(holder.file_type_icon);
        holder.filename.setText(capitalizeFileName);
        if (!StringOperations.isEmpty(model.getDateModify())) {
            holder.upload_date_text.setText("Date Modified: ");
            holder.upload_date.setText(model.getDateModify());
        } else {
            holder.upload_date.setText(model.getDateUpload());
        }
        holder.file_size.setText(model.getSize());
        if (Build.VERSION.SDK_INT >= 26)
            holder.selected_file.setTooltipText(model.getTitle());
        holder.download_file_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(activity, activity.getResources().getString(R.string.download_msg));
                customConfirmDialog.setBtnText("Download")
                        .setOkBtn(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (CheckInternetConnectivity.isInternetConnected(activity)) {
                                    if (checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    } else {
                                        startDownload(model);
                                    }
                                } else {
                                    Snackbar.make(activity.findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                                }
                                customConfirmDialog.dismissDialog();
                            }
                        });
            }
        });
        holder.delete_file_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(activity, activity.getResources().getString(R.string.delete_msg));
                customConfirmDialog.dangerBtn()
                        .setBtnText("Delete")
                        .setOkBtn(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (CheckInternetConnectivity.isInternetConnected(activity)) {
                                    String file_id = StringOperations.createFileIdentifier(model.getTitle());
                                    MyFirebaseDatabase.deleteFile(activity, model.getFileStorageId(), file_id, false);
                                } else {
                                    Toast.makeText(activity, NO_INTERNET_CONNECTION, Toast.LENGTH_LONG).show();
                                }
                                customConfirmDialog.dismissDialog();
                            }
                        });
            }
        });
        holder.selected_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternetConnectivity.isInternetConnected(activity)) {
                    if (model.getFileUri() != null) {
                        activity_opener.setClass(activity, OnlineFileViewerActivity.class);
                        activity_opener.putExtra(EXTRA_USER_FILE, model);
                        activity.startActivity(activity_opener);
                    }
                } else {
                    Snackbar.make(activity.findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        setAnimation(holder.itemView, position);
    }

    private void startDownload(UserUploads userUploads) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(userUploads.getFileUri()));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setTitle("Download");
            request.setDescription("Downloading file...");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, userUploads.getTitle());
            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } catch (Exception e) {
            Toast.makeText(activity, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
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
        private final TextView upload_date_text;
        private final TextView upload_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selected_file = itemView.findViewById(R.id.selected_file);
            file_type_icon = itemView.findViewById(R.id.file_type_icon);
            filename = itemView.findViewById(R.id.selected_filename);
            file_size = itemView.findViewById(R.id.selected_file_size);
            download_file_btn = itemView.findViewById(R.id.download_file_btn);
            delete_file_btn = itemView.findViewById(R.id.delete_file_btn);
            upload_date_text = itemView.findViewById(R.id.files_upload_date_text);
            upload_date = itemView.findViewById(R.id.files_upload_date);
        }
    }
}
