package com.example.studentaid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * Simplified Teacher Dashboard
 * - Upload Study Materials
 * - Lost & Found (combined report + view)
 * - Student Guidance
 * - Logout button in layout
 */
public class TeacherDashboardActivity extends AppCompatActivity {

    private static final String TAG = "TeacherDashboard";
    private TextView tvWelcome;
    private MaterialCardView cardStudyMaterials, cardLostFound, cardGuidance;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);

        // Get logged in teacher info
        SharedPreferences prefs = getSharedPreferences(LoginActivity.SHARED_PREFS_NAME, MODE_PRIVATE);
        String userEmail = prefs.getString(LoginActivity.KEY_USER_EMAIL, "Teacher");

        // Set title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Teacher Dashboard");
        }

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);
        cardStudyMaterials = findViewById(R.id.cardStudyMaterials);
        cardLostFound = findViewById(R.id.cardLostFound);
        cardGuidance = findViewById(R.id.cardGuidance);
        btnLogout = findViewById(R.id.btnLogout);

        // Set welcome message
        if (tvWelcome != null) {
            tvWelcome.setText("Welcome, " + userEmail + "!");
        }

        // Setup card click listeners
        setupCardListeners();

        // ✅ Setup logout button click listener
        setupLogoutButton();

        // Setup back press handler
        setupBackPressHandler();
    }

    private void setupCardListeners() {
        // Upload Study Materials
        if (cardStudyMaterials != null) {
            cardStudyMaterials.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TeacherDashboardActivity.this,
                            AddStudyMaterialActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.w(TAG, "cardStudyMaterials not found");
        }

        // Lost & Found (Combined: Report + View)
        if (cardLostFound != null) {
            cardLostFound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TeacherDashboardActivity.this,
                            LostFoundActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.w(TAG, "cardLostFound not found");
        }

        // Student Guidance
        if (cardGuidance != null) {
            cardGuidance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(TeacherDashboardActivity.this,
                            StudentGuidanceActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.w(TAG, "cardGuidance not found");
        }
    }

    /**
     * ✅ SETUP LOGOUT BUTTON CLICK LISTENER
     */
    private void setupLogoutButton() {
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLogoutConfirmationDialog();
                }
            });
        } else {
            Log.w(TAG, "btnLogout not found");
        }
    }

    /**
     * ✅ SHOW LOGOUT CONFIRMATION DIALOG
     */
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * ✅ PERFORM LOGOUT
     * Clears SharedPreferences and redirects to UserTypeSelectionActivity
     */
    private void performLogout() {
        // Clear shared preferences
        SharedPreferences prefs = getSharedPreferences("StudentAidPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Log.d(TAG, "Teacher logged out successfully");
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Redirect to UserTypeSelectionActivity
        Intent intent = new Intent(TeacherDashboardActivity.this, UserTypeSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * ✅ MODERN BACK PRESS HANDLING
     * Exit app when back is pressed from dashboard
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
