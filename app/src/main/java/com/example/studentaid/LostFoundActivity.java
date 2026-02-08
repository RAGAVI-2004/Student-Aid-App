package com.example.studentaid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Good to have for debugging
import android.widget.Button;
import android.widget.Toast; // For handling cases where buttons might be null
import androidx.appcompat.app.AppCompatActivity;

public class LostFoundActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure your XML file is named activity_lost_found.xml and is in res/layout
        setContentView(R.layout.activity_lost_found);
        Log.d("LostFoundActivity", "onCreate started");

        // Ensure IDs in activity_lost_found.xml are btnReportLost and btnViewLost
        Button btnReportLost = findViewById(R.id.btnReportLost);
        Button btnViewLost = findViewById(R.id.btnViewLost);

        if (btnReportLost != null) {
            btnReportLost.setOnClickListener(v -> {
                Log.d("LostFoundActivity", "Report Lost button clicked. Navigating to ReportLostActivity.");
                // Ensure ReportLostActivity.class exists
                startActivity(new Intent(LostFoundActivity.this, ReportLostActivity.class));
            });
        } else {
            Log.e("LostFoundActivity", "Button btnReportLost not found in layout activity_lost_found.xml");
            Toast.makeText(this, "Error: Report Lost button is missing.", Toast.LENGTH_SHORT).show();
        }

        if (btnViewLost != null) {
            btnViewLost.setOnClickListener(v -> {
                Log.d("LostFoundActivity", "View Lost button clicked. Navigating to ViewLostItemsActivity.");
                // Ensure ViewLostItemsActivity.class exists
                startActivity(new Intent(LostFoundActivity.this, ViewLostItemsActivity.class));
            });
        } else {
            Log.e("LostFoundActivity", "Button btnViewLost not found in layout activity_lost_found.xml");
            Toast.makeText(this, "Error: View Lost button is missing.", Toast.LENGTH_SHORT).show();
        }
        Log.d("LostFoundActivity", "onCreate finished");
    }
}
