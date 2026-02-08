package com.example.studentaid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    TextInputEditText etEmail, etPassword;
    MaterialButton btnLogin, btnRegister;
    ProgressBar progressBar;
    TextView tvUserType;
    DatabaseHelper dbHelper;

    private String selectedUserType = "Student"; // Default

    public static final String SHARED_PREFS_NAME = "StudentAidPrefs";
    public static final String KEY_USER_ID = "loggedInUserId";
    public static final String KEY_USER_EMAIL = "loggedInUserEmail";
    public static final String KEY_USER_ROLE = "loggedInUserRole"; // ✅ CONSISTENT KEY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ✅ GET USER TYPE FROM INTENT (handle multiple possible keys)
        selectedUserType = getIntent().getStringExtra("USER_TYPE");
        if (selectedUserType == null) {
            selectedUserType = getIntent().getStringExtra("userRole");
        }
        if (selectedUserType == null) {
            selectedUserType = "Student"; // Fallback default
        }

        Log.d(TAG, "Selected User Type: " + selectedUserType);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvUserType = findViewById(R.id.tvUserType);

        dbHelper = new DatabaseHelper(this);

        // Display user type
        if (tvUserType != null) {
            tvUserType.setText(selectedUserType + " Login");
        }

        // Set action bar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(selectedUserType + " Login");
        }

        btnLogin.setOnClickListener(v -> loginUser());

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("USER_TYPE", selectedUserType); // Pass user type
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Check credentials with role
        boolean isValid = dbHelper.checkUser(email, password, selectedUserType);

        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);

        if (isValid) {
            // ✅ Save login session WITH ROLE
            int userId = dbHelper.getUserIdByEmail(email);

            SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putBoolean("isLoggedIn", true);
            editor.putInt(KEY_USER_ID, userId);
            editor.putString(KEY_USER_EMAIL, email);
            editor.putString(KEY_USER_ROLE, selectedUserType); // ✅ SAVE ROLE HERE!

            editor.apply();

            Toast.makeText(this, "✅ Login Successful!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "✅ User logged in: " + email + " as " + selectedUserType);

            // Navigate to appropriate dashboard
            Intent intent;
            if (selectedUserType.equals("Teacher")) {
                intent = new Intent(LoginActivity.this, TeacherDashboardActivity.class);
                Log.d(TAG, "→ Navigating to TeacherDashboardActivity");
            } else {
                intent = new Intent(LoginActivity.this, DashboardActivity.class);
                Log.d(TAG, "→ Navigating to DashboardActivity (Student)");
            }

            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "❌ Invalid credentials or wrong user type", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "❌ Login failed for " + email + " as " + selectedUserType);
        }
    }
}
