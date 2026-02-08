package com.example.studentaid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RoommateFinderDashboardActivity extends AppCompatActivity {

    Button btnSearchRoommates, btnMyRequests;
    String loggedInEmail; // ✅ Store logged in user's email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommate_finder_dashboard);

        // ✅ Retrieve the email from the intent safely
        loggedInEmail = getIntent().getStringExtra("email");

        if (loggedInEmail == null || loggedInEmail.isEmpty()) {
            Log.e("RoommateFinder", "⚠️ No email received from previous activity!");
        } else {
            Log.d("RoommateFinder", "✅ Logged in user: " + loggedInEmail);
        }

        btnSearchRoommates = findViewById(R.id.btnSearchRoommates);
        btnMyRequests = findViewById(R.id.btnMyRequests);

        // ✅ Go to AddRequestActivity
        btnSearchRoommates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoommateFinderDashboardActivity.this, AddRequestActivity.class);
                if (loggedInEmail != null) {
                    intent.putExtra("email", loggedInEmail); // Pass email
                }
                startActivity(intent);
            }
        });

        // ✅ Go to MyRequestActivity
        btnMyRequests.setOnClickListener(v -> {
            Intent intent = new Intent(RoommateFinderDashboardActivity.this, MyRequestActivity.class);
            if (loggedInEmail != null) {
                intent.putExtra("email", loggedInEmail);  // Pass email
            }
            startActivity(intent);
        });
    }
}
