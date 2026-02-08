package com.example.studentaid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddRequestActivity extends AppCompatActivity {

    EditText etName, etClassSection, etEmail, etDesc;
    RadioGroup rgGender, rgRoomType;
    Spinner spinnerMembers;
    Button btnSubmit, btnViewRequests;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);

        // Initialize Views
        etName = findViewById(R.id.etName);
        etClassSection = findViewById(R.id.etClassSection);
        etEmail = findViewById(R.id.etEmail);
        etDesc = findViewById(R.id.etDesc);

        rgGender = findViewById(R.id.rgGender);
        rgRoomType = findViewById(R.id.rgRoomType);
        spinnerMembers = findViewById(R.id.spinnerMembers);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnViewRequests = findViewById(R.id.btnViewRequests);

        dbHelper = new DatabaseHelper(this);

        // Populate Spinner with numbers 1–6
        String[] membersOptions = {"Select Members", "1", "2", "3", "4", "5", "6"};
        ArrayAdapter<String> membersAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, membersOptions);
        membersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMembers.setAdapter(membersAdapter);

        // Submit Button Logic
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });

        // View Requests Button Logic
        btnViewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddRequestActivity.this, ViewRequestsActivity.class));
            }
        });
    }

    /**
     * ✅ SUBMIT REQUEST METHOD
     * Gets all form data and inserts into database
     */
    private void submitRequest() {
        // Get form data
        String name = etName.getText().toString().trim();
        String classSection = etClassSection.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        // Get Gender
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId != -1) {
            RadioButton rbGender = findViewById(selectedGenderId);
            gender = rbGender.getText().toString();
        }

        // Get Room Type
        int selectedRoomId = rgRoomType.getCheckedRadioButtonId();
        String roomType = "";
        if (selectedRoomId != -1) {
            RadioButton rbRoom = findViewById(selectedRoomId);
            roomType = rbRoom.getText().toString();
        }

        // Get Members
        String members = spinnerMembers.getSelectedItem().toString();

        // ✅ VALIDATION
        if (name.isEmpty() || classSection.isEmpty() || email.isEmpty() || desc.isEmpty()
                || gender.isEmpty() || roomType.isEmpty()
                || members.equals("Select Members")) {
            Toast.makeText(this, "⚠️ Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ GET USER ID FROM SHARED PREFERENCES
        SharedPreferences prefs = getSharedPreferences("StudentAidPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("loggedInUserId", -1);

        if (userId == -1) {
            Toast.makeText(this, "❌ Please login first", Toast.LENGTH_SHORT).show();
            // Redirect to login
            Intent intent = new Intent(AddRequestActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // ✅ CONVERT MEMBERS TO INTEGER
        int membersNeeded = 0;
        try {
            membersNeeded = Integer.parseInt(members);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "❌ Invalid members count", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ INSERT INTO DATABASE USING CORRECT METHOD
        boolean inserted = dbHelper.insertRoommateRequest(
                userId,          // user_id
                name,            // student_name
                gender,          // gender
                classSection,    // class_section
                roomType,        // room_type
                membersNeeded,   // members_needed (int)
                desc,            // description
                email            // contact_email
        );

        if (inserted) {
            Toast.makeText(this, "✅ Request Submitted Successfully!", Toast.LENGTH_LONG).show();

            // Clear all fields
            clearForm();
        } else {
            Toast.makeText(this, "❌ Failed to save request. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ✅ CLEAR FORM AFTER SUCCESSFUL SUBMISSION
     */
    private void clearForm() {
        etName.setText("");
        etClassSection.setText("");
        etEmail.setText("");
        etDesc.setText("");
        rgGender.clearCheck();
        rgRoomType.clearCheck();
        spinnerMembers.setSelection(0);
    }
}
