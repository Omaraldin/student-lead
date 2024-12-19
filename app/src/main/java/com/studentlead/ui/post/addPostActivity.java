package com.studentlead.ui.post;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.studentlead.R;
import com.studentlead.databinding.ActivityAddPostBinding;
import com.studentlead.ui.home.view.HomeActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class addPostActivity extends AppCompatActivity {

    private ActivityAddPostBinding binding;
    private static final String TAG = "AD_CREATE_TAG";
    private ProgressDialog progressDialog;
    private ArrayList<Uri> fileUris;
    private FilesAdapter filesAdapter;
    private boolean isEditMode = false;
    private String adIdForEditing = "";
    private static final int MAX_IMAGES = 6;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(addPostActivity.this);
        alert.setTitle("Discard Post");
        alert.setMessage("If you discard this post, your data will be lost");
        alert.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        fileUris = new ArrayList<>();
        setupRecyclerView(); // Set up adapter for file display

        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        if (isEditMode) {
            adIdForEditing = intent.getStringExtra("adId");
            binding.addPostButton.setText("Update Ad");
        } else {
            binding.addPostButton.setText("Publish");
        }

        String[] subjects = new String[]{
                "Java Programming II", "Data Communication", "Advanced C", "Microprocessor", "Computer Architecture", "Computer Graphics"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subjects);
        binding.subId.setAdapter(adapter);

        binding.fileId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFiles();
            }
        });

        binding.backPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void setupRecyclerView() {
        filesAdapter = new FilesAdapter(fileUris, this, new FilesAdapter.OnRemoveClickListener() {
            @Override
            public void onRemoveClick(Uri uri) {
                fileUris.remove(uri); // Remove the clicked file from the list
                filesAdapter.notifyDataSetChanged();  // Notify adapter to refresh the RecyclerView
            }
        });

        RecyclerView recyclerView = binding.numPho; // Your RecyclerView in layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(filesAdapter);
    }

    private void selectFiles() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    if (fileUris.size() < MAX_IMAGES) {
                        fileUris.add(fileUri);
                    }
                }
            } else if (data.getData() != null) {
                Uri fileUri = data.getData();
                if (fileUris.size() < MAX_IMAGES) {
                    fileUris.add(fileUri);
                }
            }

            filesAdapter.notifyDataSetChanged();  // Refresh RecyclerView after adding files
        }
    }

    private void validateData() {
        String description = binding.descriptionId.getText().toString().trim();
        String subjects = binding.subId.getText().toString().trim();

        if (description.length() < 10) {
            binding.descriptionText.setError("Description must be at least 10 characters");
            binding.descriptionId.requestFocus();
        } else if (subjects.isEmpty()) {
            binding.subAuto.setError("Please select a subject.");
        } else {
            for (Uri fileUri : fileUris) {
                uploadFileToCloudinary(fileUri, subjects, description);
            }
        }
    }

    private void uploadFileToCloudinary(Uri fileUri, String subjects, String description) {
        // Cloudinary setup
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "studentlead",
                "api_key", "254494622922497",
                "api_secret", "5i1OLumXrwMmSewadM7ms0aQ1rA"
        );
        Cloudinary cloudinary = new Cloudinary(config);

        File tempFile = createTempFileFromUri(this, fileUri);
        if (tempFile == null) {
            Toast.makeText(this, "File conversion failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                // Upload file to Cloudinary
                Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.emptyMap());

                String downloadUrl = (String) uploadResult.get("secure_url");

                runOnUiThread(() -> postAd(tempFile.getName(), downloadUrl, subjects, description));
            } catch (Exception e) {
                Log.e(TAG, "Upload failed", e);
                runOnUiThread(() -> Toast.makeText(addPostActivity.this,
                        "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                deleteTempFile(tempFile);
            }
        }).start();
    }

    private File createTempFileFromUri(Context context, Uri uri) {
        File tempFile = null;
        String extension = getFileExtension(context, uri);

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream == null) {
                throw new IOException("Failed to open InputStream for URI: " + uri);
            }

            String fileName = "temp_file" + (extension.isEmpty() ? "" : "." + extension);
            tempFile = new File(context.getCacheDir(), fileName);

            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating temp file from URI: " + uri, e);
        }

        return tempFile;
    }

    private String getFileExtension(Context context, Uri uri) {
        String extension = "";

        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType != null) {
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        if (extension == null || extension.isEmpty()) {
            String path = uri.getPath();
            if (path != null && path.contains(".")) {
                extension = path.substring(path.lastIndexOf(".") + 1);
            }
        }
        return extension != null ? extension : "";
    }

    private void deleteTempFile(File tempFile) {
        if (tempFile.exists()) {
            boolean deleted = tempFile.delete();
            if (!deleted) {
                Log.w(TAG, "Failed to delete temp file: " + tempFile.getAbsolutePath());
            } else {
                Log.d(TAG, "Temporary file deleted successfully: " + tempFile.getAbsolutePath());
            }
        }
    }

    private void postAd(String fileName, String downloadUrl, String subjects, String description) {
        progressDialog.setMessage("Publishing Ad");
        progressDialog.show();
        String time = new SimpleDateFormat("dd/MM hh:mm a", Locale.getDefault()).format(new Date());

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("files");
        String keyId = dbRef.push().getKey();

        HashMap<String, String> fileData = new HashMap<>();
        fileData.put("id", keyId);
        fileData.put("fileName", fileName);
        fileData.put("fileUrl", downloadUrl);
        fileData.put("subject", subjects);
        fileData.put("description", description);
        fileData.put("time", time);

        dbRef.child(keyId).setValue(fileData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                navigateToNextActivity();
                Toast.makeText(addPostActivity.this, "File data saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(addPostActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToNextActivity() {
        Intent intent = new Intent(addPostActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}