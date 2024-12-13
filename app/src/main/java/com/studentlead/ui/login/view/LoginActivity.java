package com.studentlead.ui.login.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.studentlead.R;
import com.studentlead.databinding.ActivityLoginBinding;
import com.studentlead.ui.home.view.HomeActivity;
import com.studentlead.ui.login.viewmodel.LoginViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private final String TAG = "LoginActivity";

    private LoginViewModel viewModel;

    private MaterialAlertDialogBuilder identifierAlertBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setContentView(binding.getRoot());

        setupViews();
        observeViewModel();
    }

    private void setupViews() {
        identifierAlertBuilder = new MaterialAlertDialogBuilder(this);
        identifierAlertBuilder.setTitle(R.string.what_is_identifier);
        identifierAlertBuilder.setMessage(R.string.identifier_description);
        identifierAlertBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        binding.whatIsIdentifier.setOnClickListener(view -> identifierAlertBuilder.create().show());

        binding.signInButton.setOnClickListener(v -> {
            String identifier = binding.identifier.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            viewModel.login(identifier, password);
        });
    }

    private void observeViewModel() {
        viewModel.getLoginState().observe(this, state -> {
            binding.progressBar.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
            binding.signInButton.setEnabled(!state.isLoading());

            if (state.getError() != null) {
                Toast.makeText(this, state.getError(), Toast.LENGTH_LONG).show();
            }

            if (state.getErrorResource() != -1) {
                Toast.makeText(this, getString(state.getErrorResource()), Toast.LENGTH_LONG).show();
            }

            if (state.isSuccess()) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });
    }
}