package com.studentlead.ui.home.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.studentlead.R;
import com.studentlead.databinding.ActivityHomeBinding;
import com.studentlead.ui.post.PostFragment;
import com.studentlead.ui.post.addPostActivity;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());

        //show post if done
        showPost();

        binding.addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextActivity();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(binding.getRoot());
    }

    private void NextActivity() {
        Intent intent = new Intent(HomeActivity.this, addPostActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish current activity if not needed anymore
    }

    private void showPost() {
        PostFragment fragment = new PostFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.framePost.getId(), fragment, "Home Post");
        fragmentTransaction.commit();
    }

}