package com.example.studentaid;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * View and open study materials with uploader info
 */
public class ViewStudyMaterialsActivity extends AppCompatActivity {

    private static final String TAG = "ViewStudyMaterials";

    private ListView listViewMaterials;
    private DatabaseHelper dbHelper;
    private StudyMaterialAdapter adapter;
    private List<StudyMaterial> materialsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_study_materials);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("📚 Study Materials");
        }

        dbHelper = new DatabaseHelper(this);
        listViewMaterials = findViewById(R.id.listViewMaterials);
        materialsList = new ArrayList<>();

        loadMaterials();

        // Click to open file
        listViewMaterials.setOnItemClickListener((parent, view, position, id) -> {
            StudyMaterial item = materialsList.get(position);
            openFile(item.getFilePath());
        });
    }

    /**
     * Load all study materials from database
     */
    private void loadMaterials() {
        materialsList.clear();
        Cursor cursor = dbHelper.getAllStudyMaterials();

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No study materials found", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        // Read cursor and create StudyMaterial objects
        while (cursor.moveToNext()) {
            StudyMaterial material = new StudyMaterial();

            material.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MATERIAL_ID)));
            material.setUserId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)));
            material.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TITLE)));
            material.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DESCRIPTION)));
            material.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SUBJECT)));
            material.setType(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TYPE)));
            material.setFilePath(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FILE_PATH)));

            // ✅ CORRECTED COLUMN NAMES
            material.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UPLOAD_DATE)));
            String uploadedBy = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_UPLOADER_NAME));

            // Create User object for uploader
            User uploader = new User();
            uploader.setName(uploadedBy);
            material.setUser(uploader);

            materialsList.add(material);
        }

        cursor.close();

        // Set adapter
        adapter = new StudyMaterialAdapter(this, materialsList);
        listViewMaterials.setAdapter(adapter);

        Log.d(TAG, "Loaded " + materialsList.size() + " study materials");
    }

    /**
     * Open file using appropriate app
     */
    private void openFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(filePath);

        if (!file.exists()) {
            Toast.makeText(this, "File has been deleted or moved", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "File not found: " + filePath);
            return;
        }

        try {
            // Use FileProvider for Android 7.0+
            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, getMimeType(filePath));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Open file with"));

        } catch (Exception e) {
            Log.e(TAG, "Failed to open file", e);
            Toast.makeText(this, "Cannot open this file type", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get MIME type from file extension
     */
    private String getMimeType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "xls":
            case "xlsx":
                return "application/vnd.ms-excel";
            case "ppt":
            case "pptx":
                return "application/vnd.ms-powerpoint";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "txt":
                return "text/plain";
            default:
                return "*/*";
        }
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
