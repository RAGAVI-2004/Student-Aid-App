package com.example.studentaid;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * MainActivity - Simple redirect to UserTypeSelectionActivity
 * This is just a launcher that immediately forwards to user selection
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Immediately redirect to UserTypeSelectionActivity
        Intent intent = new Intent(MainActivity.this, UserTypeSelectionActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity so user can't go back to it
    }
}
