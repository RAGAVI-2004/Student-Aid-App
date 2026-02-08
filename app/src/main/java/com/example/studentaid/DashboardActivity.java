package com.example.studentaid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * Student Dashboard Activity
 * Main dashboard for students with access to all features
 */
public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    // ✅ Only existing features
    private MaterialCardView cardStudyMaterials, cardRoommateFinder, cardLostFound, cardPeerGuidance;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_fragment);

        // Apply window insets for edge-to-edge display
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        } else {
            Log.e(TAG, "Root view with ID 'main' not found in activity_dashboard_fragment.xml");
        }

        // Set action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Dashboard");
        }

        // Initialize views
        initializeViews();

        // Set click listeners
        setupClickListeners();

        // Setup logout button
        setupLogoutButton();

        // Handle back press
        setupBackPressHandler();
    }

    private void initializeViews() {
        cardStudyMaterials = findViewById(R.id.cardStudyMaterials);
        cardRoommateFinder = findViewById(R.id.cardRoommateFinder);
        cardLostFound = findViewById(R.id.cardLostFound);
        cardPeerGuidance = findViewById(R.id.cardPeerGuidance);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupClickListeners() {
        // ✅ Study Materials
        if (cardStudyMaterials != null) {
            cardStudyMaterials.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to AddStudyMaterialActivity");
                startActivity(new Intent(DashboardActivity.this, AddStudyMaterialActivity.class));
            });
        } else {
            Log.w(TAG, "cardStudyMaterials not found");
        }

        // ✅ Roommate Finder
        if (cardRoommateFinder != null) {
            cardRoommateFinder.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to AddRequestActivity");
                startActivity(new Intent(DashboardActivity.this, AddRequestActivity.class));
            });
        } else {
            Log.w(TAG, "cardRoommateFinder not found");
        }

        // ✅ Lost & Found
        if (cardLostFound != null) {
            cardLostFound.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to LostFoundActivity");
                startActivity(new Intent(DashboardActivity.this, LostFoundActivity.class));
            });
        } else {
            Log.w(TAG, "cardLostFound not found");
        }

        // ✅ Peer Guidance / Student-Teacher Chat
        if (cardPeerGuidance != null) {
            cardPeerGuidance.setOnClickListener(v -> {
                Log.d(TAG, "Navigating to StudentGuidanceActivity");
                startActivity(new Intent(DashboardActivity.this, StudentGuidanceActivity.class));
            });
        } else {
            Log.w(TAG, "cardPeerGuidance not found");
        }
    }

    /**
     * ✅ LOGOUT BUTTON HANDLER
     */
    private void setupLogoutButton() {
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        } else {
            Log.w(TAG, "btnLogout not found");
        }
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        // Clear shared preferences
        SharedPreferences prefs = getSharedPreferences("StudentAidPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Log.d(TAG, "User logged out successfully");
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to UserTypeSelectionActivity
        Intent intent = new Intent(DashboardActivity.this, UserTypeSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * ✅ MODERN BACK PRESS HANDLING using OnBackPressedDispatcher
     */
    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Exit app when back is pressed from dashboard
                finishAffinity();
            }
        });
    }
}
