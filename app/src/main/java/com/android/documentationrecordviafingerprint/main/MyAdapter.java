package com.android.documentationrecordviafingerprint.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.documentationrecordviafingerprint.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final RecyclerView recyclerView;
    private final Context context;
    ArrayList<String> items;
    ArrayList<String> urls;
    ArrayList<String> fileSizes;

    public MyAdapter(RecyclerView recyclerView, Context context) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = new ArrayList<>();
        this.urls = new ArrayList<>();
        this.fileSizes = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listview_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        holder.selected_filename.setText(items.get(position));
        holder.selected_file_size.setText(fileSizes.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void update(String name,String url,String fileSize)
    {
        this.items.add(name);
        this.urls.add(url);
        this.fileSizes.add(fileSize);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView file_type_icon;
        private TextView selected_filename;
        private TextView selected_file_size;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            file_type_icon = itemView.findViewById(R.id.file_type_icon);
            selected_filename = itemView.findViewById(R.id.selected_filename);
            selected_file_size = itemView.findViewById(R.id.selected_file_size);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildLayoutPosition(v);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(urls.get(position)), "application/pdf");
                    context.startActivity(intent);
                }
            });
        }
    }
}
