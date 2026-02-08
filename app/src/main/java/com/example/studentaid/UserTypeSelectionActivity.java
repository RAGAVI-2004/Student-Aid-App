package com.example.studentaid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

/**
 * User Type Selection Activity
 * Allows user to choose between Student and Teacher roles
 */
public class UserTypeSelectionActivity extends AppCompatActivity {

    private static final String TAG = "UserTypeSelection";
    private MaterialCardView cardStudent, cardTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_type_selection);

        // Apply window insets
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize cards
        cardStudent = findViewById(R.id.cardStudent);
        cardTeacher = findViewById(R.id.cardTeacher);

        // Student card click listener
        if (cardStudent != null) {
            cardStudent.setOnClickListener(v -> {
                Log.d(TAG, "✅ Student card clicked");
                Toast.makeText(this, "Opening Student Login...", Toast.LENGTH_SHORT).show();

                // ✅ Navigate to LoginActivity with STUDENT role
                Intent intent = new Intent(UserTypeSelectionActivity.this, LoginActivity.class);
                intent.putExtra("USER_TYPE", "Student"); // ✅ FIXED: Changed from USER_ROLE to USER_TYPE
                startActivity(intent);
            });
        }

        // Teacher card click listener
        if (cardTeacher != null) {
            cardTeacher.setOnClickListener(v -> {
                Log.d(TAG, "✅ Teacher card clicked");
                Toast.makeText(this, "Opening Teacher Login...", Toast.LENGTH_SHORT).show();

                // ✅ Navigate to LoginActivity with TEACHER role
                Intent intent = new Intent(UserTypeSelectionActivity.this, LoginActivity.class);
                intent.putExtra("USER_TYPE", "Teacher"); // ✅ FIXED: Changed from USER_ROLE to USER_TYPE
                startActivity(intent);
            });
        }

        // ✅ Handle back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Exit app when back pressed from user type selection
                finishAffinity();
            }
        });
    }
}
