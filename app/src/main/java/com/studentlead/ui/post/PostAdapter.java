package com.studentlead.ui.post;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.studentlead.databinding.RowPostBinding;

import java.util.Collections;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    // Constructor
    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create binding for RowPost layout
        RowPostBinding binding = RowPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Bind data to views inside RowPost layout
        holder.binding.subjectName.setText(post.getSubject());
        holder.binding.taskDescription.setText(post.getDescription());
        holder.binding.postDate.setText(post.getTime());

        // Load image if URL is available (using Glide for images)
        if (post.getFileUrl() != null && !post.getFileUrl().isEmpty()) {
            Glide.with(holder.binding.fileImg.getContext())
                    .load(post.getFileUrl())
                    .into(holder.binding.fileImg);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // Method to update the post list with reversing the data order
    public void updatePostList(List<Post> newPostList) {
        // Reverse the list to show the latest post at the top
        Collections.reverse(newPostList);
        postList.clear();
        postList.addAll(newPostList);
        notifyDataSetChanged(); // Notify adapter of data change
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        RowPostBinding binding;

        public PostViewHolder(RowPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
