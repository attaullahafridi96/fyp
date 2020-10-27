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
import com.android.documentationrecordviafingerprint.internetchecking.CheckInternetConnectivity;
import com.android.documentationrecordviafingerprint.model.UserFile;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.io.File;

public class MyFilesAdapter extends FirebaseRecyclerAdapter<UserFile, MyFilesAdapter.ViewHolder> {
    private final Context context;

    public MyFilesAdapter(Context context, @NonNull FirebaseRecyclerOptions<UserFile> options) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_design, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final UserFile model) {
        Glide.with(holder.file_type_icon.getContext()).load(model.getImage_uri()).into(holder.file_type_icon);
        holder.selected_filename.setText(model.getFile_name());
        holder.selected_file_size.setText(model.getFile_size());
        holder.selected_file.setTooltipText(model.getFile_name());
        holder.download_file_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckInternetConnectivity.isInternetConnected(context)){
                    Intent it = new Intent();
                    it.setAction(Intent.ACTION_VIEW);
                    it.setData(Uri.parse(model.getFile_uri()));
                    context.startActivity(it);
                }else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.selected_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getFile_uri() != null) {
                    if (model.getFile_type().equals("doc") || model.getFile_type().equals("docx") || model.getFile_type().equals("rtf")) {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        StrictMode.setVmPolicy(builder.build());
                        builder.detectFileUriExposure();
                        Uri docUri = FileProvider.getUriForFile(context, "com.android.documentationrecordviafingerprint.provider",
                                new File(model.getFile_uri()));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(docUri, "application/msword");
                        try {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            Intent chooser = Intent.createChooser(intent, "Open With..");
                            context.startActivity(chooser);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(context, "No application to open file", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent it = new Intent();
                        it.setClass(context, OnlineFileViewer.class);
                        it.putExtra("FILE_NAME", model.getFile_name());
                        it.putExtra("URI", model.getFile_uri());
                        it.putExtra("FILE_EXTENSION", model.getFile_type());
                        context.startActivity(it);
                    }
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView file_type_icon;
        private final TextView selected_filename;
        private final TextView selected_file_size;
        private final LinearLayout selected_file;
        private ImageButton download_file_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selected_file = itemView.findViewById(R.id.selected_file);
            file_type_icon = itemView.findViewById(R.id.file_type_icon);
            selected_filename = itemView.findViewById(R.id.selected_filename);
            selected_file_size = itemView.findViewById(R.id.selected_file_size);
            download_file_btn = itemView.findViewById(R.id.download_file_btn);
        }
    }
}
