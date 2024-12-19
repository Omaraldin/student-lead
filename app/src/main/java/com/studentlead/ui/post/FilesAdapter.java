package com.studentlead.ui.post;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.studentlead.R;

import java.util.ArrayList;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {
    private ArrayList<Uri> fileUris;
    private Context context;
    private OnRemoveClickListener onRemoveClickListener;

    // Interface to handle remove clicks
    public interface OnRemoveClickListener {
        void onRemoveClick(Uri uri);
    }

    public FilesAdapter(ArrayList<Uri> fileUris, Context context, OnRemoveClickListener onRemoveClickListener) {
        this.fileUris = fileUris;
        this.context = context;
        this.onRemoveClickListener = onRemoveClickListener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_img, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        Uri fileUri = fileUris.get(position);
        holder.imgIv.setImageURI(fileUri);

        holder.closeImg.setOnClickListener(v -> {
            if (onRemoveClickListener != null) {
                onRemoveClickListener.onRemoveClick(fileUri);
                notifyItemRemoved(position); // Remove only the specific item
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileUris.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgIv;
        ImageButton closeImg;

        public FileViewHolder(View itemView) {
            super(itemView);
            imgIv = itemView.findViewById(R.id.imgIv);
            closeImg = itemView.findViewById(R.id.closeImg);
        }
    }
}


