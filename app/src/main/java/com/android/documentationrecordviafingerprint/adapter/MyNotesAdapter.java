package com.android.documentationrecordviafingerprint.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentationrecordviafingerprint.R;
import com.android.documentationrecordviafingerprint.controller.MyFirebaseDatabase;
import com.android.documentationrecordviafingerprint.helper.CryptUtil;
import com.android.documentationrecordviafingerprint.helper.IMyConstants;
import com.android.documentationrecordviafingerprint.helper.NotesDownloader;
import com.android.documentationrecordviafingerprint.helper.StringOperations;
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.UserNotes;
import com.android.documentationrecordviafingerprint.uihelper.CustomConfirmDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomInputDialog;
import com.android.documentationrecordviafingerprint.uihelper.CustomMsgDialog;
import com.android.documentationrecordviafingerprint.view.NotesEditorActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public final class MyNotesAdapter extends FirebaseRecyclerAdapter<UserNotes, MyNotesAdapter.ViewHolder> implements IMyConstants {
    private final Activity activity;

    private static final Intent activity_opener = new Intent();
    private int lastPosition = -1;

    public MyNotesAdapter(final Activity activity, @NonNull final FirebaseRecyclerOptions<UserNotes> options) {
        super(options);
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyNotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_item_design, parent, false);
        return new MyNotesAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull final MyNotesAdapter.ViewHolder holder, int position, @NonNull final UserNotes model) {
        final String FileNameInLarge = model.getTitle().toUpperCase();
        holder.filename.setText(FileNameInLarge);
        if (!StringOperations.isEmpty(model.getDateModify())) {
            holder.upload_date_text.setText("Date Modified: ");
            holder.upload_date.setText(model.getDateModify());
        } else {
            holder.upload_date.setText(model.getDateUpload());
        }
        holder.file_size.setText(model.getSize());
        if (Build.VERSION.SDK_INT >= 26)
            holder.selected_notes.setTooltipText(model.getTitle());
        holder.selected_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_opener.setClass(activity, NotesEditorActivity.class);
                activity_opener.putExtra(EXTRA_USER_NOTES, model);
                activity.startActivity(activity_opener);
            }
        });
        holder.selected_notes.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle(FileNameInLarge);
                menu.add("Download").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (CheckInternetConnectivity.isInternetConnected(activity)) {
                            if (checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            } else {
                                NotesDownloader.saveNotes(activity, model.getTitle().toLowerCase().trim(), CryptUtil.decrypt(model.getNotesData()));
                            }
                        } else {
                            Snackbar.make(activity.findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                        }
                        return true;
                    }
                });
                menu.add("Rename").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        renameNotes(model);
                        return true;
                    }
                });
                menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        deleteNotes(model.getTitle());
                        return true;
                    }
                });
            }
        });
        setAnimation(holder.itemView, position);
    }

    private String new_notes_name;

    private void renameNotes(final UserNotes model) {
        try {
            final CustomInputDialog customInputDialog = new CustomInputDialog(activity, "Rename");
            customInputDialog.setOkBtnListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customInputDialog.dismissDialog();
                    new_notes_name = customInputDialog.getInputText();
                    if (StringOperations.isEmpty(new_notes_name)) {
                        new CustomMsgDialog(activity, "Alert", "Can't Set Empty Notes Name.");
                        return;
                    }
                    String old_notes_id = model.getId();
                    String new_notes_id = StringOperations.createFileIdentifier(new_notes_name);
                    if (old_notes_id.equalsIgnoreCase(new_notes_id)) {
                        Toast.makeText(activity, "Please enter different notes name", Toast.LENGTH_LONG).show();
                    } else {
                        if (CheckInternetConnectivity.isInternetConnected(activity)) {
                            MyFirebaseDatabase.renameNotesOnCloud(activity, new_notes_name, new_notes_id, model, false);
                        } else {
                            Snackbar.make(activity.findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteNotes(final String notes_title) {
        final CustomConfirmDialog customConfirmDialog = new CustomConfirmDialog(activity, activity.getResources().getString(R.string.notes_delete_msg));
        customConfirmDialog.setBtnText("Delete")
                .dangerBtn()
                .setOkBtn(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CheckInternetConnectivity.isInternetConnected(activity)) {
                            String title = notes_title.toLowerCase().trim();
                            String notes_id = StringOperations.createFileIdentifier(title);
                            MyFirebaseDatabase.deleteNotes(activity, notes_id, false);
                        } else {
                            Snackbar.make(activity.findViewById(android.R.id.content), NO_INTERNET_CONNECTION, Snackbar.LENGTH_LONG).show();
                        }
                        customConfirmDialog.dismissDialog();
                    }
                });
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView filename;
        private final TextView file_size;
        private final LinearLayout selected_notes;
        private final TextView upload_date_text;
        private final TextView upload_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selected_notes = itemView.findViewById(R.id.selected_notes);
            filename = itemView.findViewById(R.id.selected_notes_name);
            file_size = itemView.findViewById(R.id.selected_file_size);
            upload_date = itemView.findViewById(R.id.notes_upload_date);
            upload_date_text = itemView.findViewById(R.id.notes_upload_date_text);
        }

    }
}

