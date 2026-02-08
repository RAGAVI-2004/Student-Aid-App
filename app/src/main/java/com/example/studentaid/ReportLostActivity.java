package com.example.studentaid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReportLostActivity extends AppCompatActivity {

    private static final String TAG = "ReportLostActivity";

    private EditText etItemName, etDescription, etLocation, etContact;
    private Button btnSaveLost, btnTakePhoto, btnGetLocation, btnViewMap;
    private TextView tvLocation;
    private ImageView ivItemPhoto;
    private DatabaseHelper dbHelper;
    private int currentLoggedInUserId = -1;
    private Bitmap capturedPhoto = null;
    private String savedPhotoPath = null;  // ✅ STORE PHOTO PATH

    // ✅ GPS Variables
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude = 0;
    private double currentLongitude = 0;
    private String currentAddress = "";

    public static final String SHARED_PREFS_NAME = "StudentAidPrefs";
    public static final String KEY_USER_ID = "loggedInUserId";

    // ✅ Location Permission Launcher
    private final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    // ✅ Camera Permission Launcher
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Void> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
                if (bitmap != null) {
                    capturedPhoto = bitmap;
                    ivItemPhoto.setImageBitmap(bitmap);
                    ivItemPhoto.setVisibility(ImageView.VISIBLE);

                    // ✅ SAVE PHOTO TO FILE IMMEDIATELY
                    savedPhotoPath = savePhotoToFile(bitmap);

                    Toast.makeText(this, "Photo captured!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lost);

        // ✅ Initialize GPS
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // ✅ Get logged-in user ID
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        currentLoggedInUserId = sharedPreferences.getInt(KEY_USER_ID, -1);
        Log.d(TAG, "Retrieved UserID from SharedPreferences: " + currentLoggedInUserId);

        // ✅ Initialize views
        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etContact = findViewById(R.id.etContact);
        btnSaveLost = findViewById(R.id.btnSaveLost);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnViewMap = findViewById(R.id.btnViewMap);
        tvLocation = findViewById(R.id.tvLocation);
        ivItemPhoto = findViewById(R.id.ivItemPhoto);

        dbHelper = new DatabaseHelper(this);

        // ✅ Take Photo Button
        if (btnTakePhoto != null) {
            btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndTakePhoto());
        }

        // ✅ Get Location Button
        if (btnGetLocation != null) {
            btnGetLocation.setOnClickListener(v -> checkLocationPermissionAndGetLocation());
        }

        // ✅ View on Map Button
        if (btnViewMap != null) {
            btnViewMap.setOnClickListener(v -> openMapWithLocation());
        }

        // ✅ Save Button
        if (btnSaveLost != null) {
            btnSaveLost.setOnClickListener(v -> saveLostItem());
        } else {
            Log.e(TAG, "Save button (btnSaveLost) not found in layout.");
        }

        if (currentLoggedInUserId == -1) {
            Log.w(TAG, "No logged-in User ID found in SharedPreferences.");
            Toast.makeText(this, "⚠️ User not identified. Please login to report items.", Toast.LENGTH_LONG).show();
            if (btnSaveLost != null) {
                btnSaveLost.setEnabled(false);
            }
        }
    }

    /**
     * ✅ SAVE PHOTO TO INTERNAL STORAGE
     */
    private String savePhotoToFile(Bitmap bitmap) {
        try {
            // Create directory
            File photosDir = new File(getFilesDir(), "lost_photos");
            if (!photosDir.exists()) {
                photosDir.mkdirs();
            }

            // Generate unique filename
            String fileName = "photo_" + System.currentTimeMillis() + ".jpg";
            File photoFile = new File(photosDir, fileName);

            // Save bitmap
            FileOutputStream fos = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            Log.d(TAG, "Photo saved: " + photoFile.getAbsolutePath());
            return photoFile.getAbsolutePath();

        } catch (IOException e) {
            Log.e(TAG, "Error saving photo", e);
            return null;
        }
    }

    /**
     * ✅ Check location permission and get current location
     */
    private void checkLocationPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * ✅ Get current GPS location
     */
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Toast.makeText(this, "Getting location...", Toast.LENGTH_SHORT).show();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();

                        // ✅ SAVE LOCATION AS "lat,lng" FORMAT
                        currentAddress = currentLatitude + "," + currentLongitude;

                        // Get readable address
                        getAddressFromLocation(currentLatitude, currentLongitude);

                        // Enable map button
                        if (btnViewMap != null) {
                            btnViewMap.setEnabled(true);
                        }

                        Toast.makeText(this, "✅ Location captured!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Unable to get location. Try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get location", e);
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * ✅ Convert GPS coordinates to readable address
     */
    private void getAddressFromLocation(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder fullAddress = new StringBuilder();

                // Build readable address
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    fullAddress.append(address.getAddressLine(i));
                    if (i < address.getMaxAddressLineIndex()) {
                        fullAddress.append(", ");
                    }
                }

                String readableAddress = fullAddress.toString();

                // Display in UI
                tvLocation.setText(readableAddress);
                etLocation.setText(readableAddress);

                Log.d(TAG, "Address: " + readableAddress);
                Log.d(TAG, "Coordinates stored: " + currentAddress);
            }
        } catch (IOException e) {
            Log.e(TAG, "Geocoder failed", e);
            // Show coordinates
            String coordsText = "Lat: " + latitude + ", Lng: " + longitude;
            tvLocation.setText(coordsText);
            etLocation.setText(coordsText);
        }
    }

    /**
     * ✅ Open map with current location
     */
    private void openMapWithLocation() {
        if (currentLatitude == 0 && currentLongitude == 0) {
            Toast.makeText(this, "Please get location first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Open Google Maps with marker
        String uri = "geo:" + currentLatitude + "," + currentLongitude + "?q=" + currentLatitude + "," + currentLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Fallback to browser
            String url = "https://www.google.com/maps?q=" + currentLatitude + "," + currentLongitude;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    /**
     * ✅ Check camera permission and take photo
     */
    private void checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * ✅ Open camera to take photo
     */
    private void openCamera() {
        takePictureLauncher.launch(null);
    }

    /**
     * ✅ Save lost item to database WITH PHOTO PATH
     */
    private void saveLostItem() {
        if (etItemName == null || etContact == null || btnSaveLost == null) {
            Log.e(TAG, "Cannot save, one or more required form views are null.");
            Toast.makeText(this, "Error: Form components are not properly loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etItemName.getText().toString().trim();
        String desc = (etDescription != null) ? etDescription.getText().toString().trim() : "";
        String loc = (etLocation != null) ? etLocation.getText().toString().trim() : "";
        String contactValue = etContact.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etItemName.setError("Enter item name");
            return;
        }

        if (TextUtils.isEmpty(loc) && TextUtils.isEmpty(currentAddress)) {
            Toast.makeText(this, "⚠️ Please capture location or enter manually", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(contactValue)) {
            etContact.setError("Enter contact");
            return;
        }

        if (currentLoggedInUserId <= 0) {
            Log.e(TAG, "Attempted to save item with invalid UserID: " + currentLoggedInUserId);
            Toast.makeText(this, "⚠️ User not identified. Please login again to report an item.", Toast.LENGTH_LONG).show();
            return;
        }

        // ✅ USE GPS COORDINATES if available, otherwise manual input
        String finalLocation = !TextUtils.isEmpty(currentAddress) ? currentAddress : loc;

        Log.d(TAG, "Attempting to insert lost item for UserID: " + currentLoggedInUserId);

        // ✅ Insert into database WITH PHOTO PATH
        boolean inserted = dbHelper.insertLostItem(
                currentLoggedInUserId,
                name,
                desc,
                finalLocation,  // Stores "lat,lng" or manual text
                contactValue,
                savedPhotoPath  // ✅ PHOTO PATH SAVED
        );

        if (inserted) {
            Log.i(TAG, "Lost item reported successfully for UserID: " + currentLoggedInUserId);
            Toast.makeText(this, "✅ Lost item reported successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
            finish();
        } else {
            Log.e(TAG, "Failed to save lost item for UserID: " + currentLoggedInUserId + ". Check DB logs.");
            Toast.makeText(this, "❌ Error saving item. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ✅ Clear all input fields
     */
    private void clearFields() {
        if (etItemName != null) etItemName.setText("");
        if (etDescription != null) etDescription.setText("");
        if (etLocation != null) etLocation.setText("");
        if (etContact != null) etContact.setText("");
        if (tvLocation != null) tvLocation.setText("Not captured yet");
        if (ivItemPhoto != null) {
            ivItemPhoto.setImageBitmap(null);
            ivItemPhoto.setVisibility(ImageView.GONE);
        }
        if (btnViewMap != null) btnViewMap.setEnabled(false);

        capturedPhoto = null;
        savedPhotoPath = null;
        currentLatitude = 0;
        currentLongitude = 0;
        currentAddress = "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
