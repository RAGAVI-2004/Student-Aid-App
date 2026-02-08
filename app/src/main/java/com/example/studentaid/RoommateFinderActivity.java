package com.example.studentaid;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RoommateFinderActivity extends AppCompatActivity {

    EditText etName, etClassSection, etEmail, etDesc;
    RadioGroup rgGender, rgRoomType;
    RadioButton rbGenderSelected, rbRoomSelected;
    Spinner spinnerMembers;
    Button btnSubmit, btnViewRequests;

    String selectedMembers = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request);

        // Initialize views
        etName = findViewById(R.id.etName);
        etClassSection = findViewById(R.id.etClassSection);
        etEmail = findViewById(R.id.etEmail);
        etDesc = findViewById(R.id.etDesc);

        rgGender = findViewById(R.id.rgGender);
        rgRoomType = findViewById(R.id.rgRoomType);

        spinnerMembers = findViewById(R.id.spinnerMembers);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnViewRequests = findViewById(R.id.btnViewRequests);

        // Spinner values
        String[] members = {"1", "2", "3", "4", "5+"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, members);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMembers.setAdapter(adapter);

        spinnerMembers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMembers = members[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedMembers = "";
            }
        });

        // Submit Button Click
        btnSubmit.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String classSection = etClassSection.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            // Gender selection
            int genderId = rgGender.getCheckedRadioButtonId();
            if (genderId != -1) {
                rbGenderSelected = findViewById(genderId);
            }

            // Room type selection
            int roomId = rgRoomType.getCheckedRadioButtonId();
            if (roomId != -1) {
                rbRoomSelected = findViewById(roomId);
            }

            if (name.isEmpty() || classSection.isEmpty() || email.isEmpty() || genderId == -1 || roomId == -1) {
                Toast.makeText(RoommateFinderActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show data (you can save to DB later)
            String result = "Name: " + name +
                    "\nGender: " + rbGenderSelected.getText().toString() +
                    "\nClass: " + classSection +
                    "\nRoom: " + rbRoomSelected.getText().toString() +
                    "\nMembers: " + selectedMembers +
                    "\nEmail: " + email +
                    "\nDesc: " + desc;

            Toast.makeText(RoommateFinderActivity.this, result, Toast.LENGTH_LONG).show();
        });

        // View Requests Button Click
        btnViewRequests.setOnClickListener(v -> {
            // Open another activity to display saved requests
            Toast.makeText(RoommateFinderActivity.this, "Navigate to Requests Page", Toast.LENGTH_SHORT).show();
            // Example: startActivity(new Intent(AddRequestActivity.this, ViewRequestsActivity.class));
        });
    }
}
