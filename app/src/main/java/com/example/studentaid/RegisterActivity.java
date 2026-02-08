package com.example.studentaid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 * Registration Activity - Handles user registration for both Students and Teachers
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private MaterialButton btnRegister, btnLogin;
    private ProgressBar progressBar;
    private ScrollView registerForm;
    private TextView tvTitle, tvSubtitle;

    private DatabaseHelper dbHelper;
    private String userRole = "Student"; // Default role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ✅ GET USER TYPE FROM INTENT (CONSISTENT WITH OTHER ACTIVITIES)
        userRole = getIntent().getStringExtra("USER_TYPE");
        if (userRole == null || userRole.isEmpty()) {
            userRole = "Student"; // Default to Student
        }

        Log.d(TAG, "RegisterActivity started for role: " + userRole);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        registerForm = findViewById(R.id.registerForm);
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);

        dbHelper = new DatabaseHelper(this);

        // ✅ UPDATE TITLE BASED ON ROLE
        if (tvTitle != null) {
            tvTitle.setText(userRole + " Registration");
        }

        if (tvSubtitle != null) {
            tvSubtitle.setText("Create your " + userRole + " account");
        }

        // ✅ UPDATE ACTION BAR TITLE
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(userRole + " Registration");
        }

        // Register button click listener
        btnRegister.setOnClickListener(view -> attemptRegistration());

        // Login button click listener
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra("USER_TYPE", userRole); // ✅ FIXED: Changed from USER_ROLE
            startActivity(intent);
            finish();
        });

        // ✅ MODERN BACK PRESS HANDLING
        setupBackPressHandler();
    }

    /**
     * ✅ ATTEMPT REGISTRATION - Validate all fields
     */
    private void attemptRegistration() {
        // Reset errors
        etName.setError(null);
        etEmail.setError(null);
        etPhone.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);

        // Get input values
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // ✅ VALIDATE NAME
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            focusView = etName;
            cancel = true;
        } else if (name.length() < 3) {
            etName.setError("Name must be at least 3 characters");
            focusView = etName;
            cancel = true;
        }

        // ✅ VALIDATE PHONE
        if (!TextUtils.isEmpty(phone) && phone.length() < 10) {
            etPhone.setError("Enter a valid 10-digit phone number");
            focusView = etPhone;
            cancel = true;
        }

        // ✅ VALIDATE EMAIL
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            focusView = etEmail;
            cancel = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email address");
            focusView = etEmail;
            cancel = true;
        }

        // ✅ VALIDATE PASSWORD
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            focusView = etPassword;
            cancel = true;
        } else if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            focusView = etPassword;
            cancel = true;
        }

        // ✅ VALIDATE CONFIRM PASSWORD
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Please confirm your password");
            focusView = etConfirmPassword;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            focusView = etConfirmPassword;
            cancel = true;
        }

        if (cancel) {
            // Focus on the first error field
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // All validations passed - proceed with registration
            showProgress(true);
            performRegistration(name, email, phone, password);
        }
    }

    /**
     * ✅ PERFORM REGISTRATION - Insert user into database
     */
    private void performRegistration(String name, String email, String phone, String password) {
        Log.d(TAG, "Attempting registration for: " + email + " as " + userRole);

        // ✅ REGISTER USER WITH ROLE
        boolean registered = dbHelper.registerUserWithDetails(
                name,
                email,
                phone,
                password,
                userRole  // ✅ Pass the role (Student or Teacher)
        );

        showProgress(false);

        if (registered) {
            Toast.makeText(this,
                    "✅ Registration successful as " + userRole + "! Please login.",
                    Toast.LENGTH_LONG).show();

            Log.i(TAG, "Registration successful for " + email + " as " + userRole);

            // Navigate to login with same user role
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("USER_TYPE", userRole); // ✅ FIXED: Changed from USER_ROLE
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this,
                    "❌ Registration failed. Email might already be registered.",
                    Toast.LENGTH_LONG).show();

            Log.w(TAG, "Registration failed for " + email);

            // Focus on email field
            etEmail.setError("This email is already registered");
            etEmail.requestFocus();
        }
    }

    /**
     * ✅ SHOW/HIDE PROGRESS
     */
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        registerForm.setVisibility(show ? View.GONE : View.VISIBLE);
        btnRegister.setEnabled(!show);
        btnLogin.setEnabled(!show);
    }

    /**
     * ✅ MODERN BACK PRESS HANDLING using OnBackPressedDispatcher
     * Go back to login with same user role
     */
    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed - returning to login");
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("USER_TYPE", userRole); // ✅ FIXED: Changed from USER_ROLE
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
