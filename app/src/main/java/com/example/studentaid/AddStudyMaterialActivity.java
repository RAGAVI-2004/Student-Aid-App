package com.example.studentaid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddStudyMaterialActivity extends AppCompatActivity {

    private static final String TAG = "AddStudyMaterial";

    private EditText etTitle, etDescription;
    private Spinner spinnerSubject, spinnerType;
    private Button btnSelectFile, btnSubmit, btnViewMaterials;
    private TextView tvSelectedFile;
    private Uri fileUri = null;

    private DatabaseHelper dbHelper;
    private int currentUserId;
    private String currentUserName;

    // Modern ActivityResultLauncher for file picker
    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    fileUri = result.getData().getData();
                    if (fileUri != null) {
                        String fileName = getFileNameFromUri(fileUri);
                        tvSelectedFile.setText("Selected: " + fileName);
                        tvSelectedFile.setVisibility(TextView.VISIBLE);
                        Toast.makeText(this, "✅ File Selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_study_material);

        dbHelper = new DatabaseHelper(this);

        // Get current user info
        SharedPreferences prefs = getSharedPreferences("StudentAidPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("loggedInUserId", -1);
        currentUserName = dbHelper.getUserNameById(currentUserId);

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        spinnerType = findViewById(R.id.spinnerType);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnViewMaterials = findViewById(R.id.btnViewMaterials);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);

        // Populate spinners
        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(
                this, R.array.subjects_array, android.R.layout.simple_spinner_item);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.material_types, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // File select button
        btnSelectFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            filePickerLauncher.launch(Intent.createChooser(intent, "Select File"));
        });

        // Submit button
        btnSubmit.setOnClickListener(v -> uploadMaterial());

        // ✅ VIEW MATERIALS BUTTON
        btnViewMaterials.setOnClickListener(v -> {
            Intent intent = new Intent(AddStudyMaterialActivity.this, ViewStudyMaterialsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Upload material to database
     */
    private void uploadMaterial() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String subject = spinnerSubject.getSelectedItem().toString();
        String type = spinnerType.getSelectedItem().toString();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Enter title");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Enter description");
            return;
        }

        if (fileUri == null) {
            Toast.makeText(this, "Please select a file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Copy file to app storage
        String savedFilePath = saveFileToStorage(fileUri);

        if (savedFilePath == null) {
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to database with uploader name
        boolean success = dbHelper.insertStudyMaterial(
                currentUserId,
                title,
                description,
                subject,
                type,
                savedFilePath,
                currentUserName  // ✅ USER NAME ADDED
        );

        if (success) {
            Toast.makeText(this, "✅ Material uploaded successfully!", Toast.LENGTH_SHORT).show();
            // Clear form
            etTitle.setText("");
            etDescription.setText("");
            tvSelectedFile.setVisibility(TextView.GONE);
            fileUri = null;
        } else {
            Toast.makeText(this, "❌ Failed to upload material", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save file to app's internal storage
     */
    private String saveFileToStorage(Uri uri) {
        try {
            // Create directory
            File studyMaterialsDir = new File(getFilesDir(), "study_materials");
            if (!studyMaterialsDir.exists()) {
                studyMaterialsDir.mkdirs();
            }

            // Generate unique filename
            String fileName = System.currentTimeMillis() + "_" + getFileNameFromUri(uri);
            File destFile = new File(studyMaterialsDir, fileName);

            // Copy file
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            Log.d(TAG, "File saved: " + destFile.getAbsolutePath());
            return destFile.getAbsolutePath();

        } catch (Exception e) {
            Log.e(TAG, "Error saving file", e);
            return null;
        }
    }

    /**
     * Get filename from URI
     */
    private String getFileNameFromUri(Uri uri) {
        String path = uri.getLastPathSegment();
        if (path != null && path.contains("/")) {
            return path.substring(path.lastIndexOf("/") + 1);
        }
        return "file_" + System.currentTimeMillis();
    }
}
