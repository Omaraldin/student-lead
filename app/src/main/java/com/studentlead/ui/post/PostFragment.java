package com.studentlead.ui.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.studentlead.databinding.FragmentPostBinding;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    private PostAdapter postAdapter;
    private DatabaseReference postRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout using ViewBinding
        FragmentPostBinding binding = FragmentPostBinding.inflate(inflater, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = binding.adsRv;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the adapter with an empty list initially
        postAdapter = new PostAdapter(new ArrayList<>());
        recyclerView.setAdapter(postAdapter);

        // Initialize Firebase Database reference
        postRef = FirebaseDatabase.getInstance().getReference("files");

        // Load data from Firebase Realtime Database
        loadData();

        return binding.getRoot();
    }

    private void loadData() {
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post != null) {
                        posts.add(post);
                    }
                }

                // Update the adapter with the new list of posts
                postAdapter.updatePostList(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle Firebase error
            }
        });
    }
}
