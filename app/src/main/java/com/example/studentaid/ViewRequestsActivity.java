package com.example.studentaid;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewRequestsActivity extends AppCompatActivity {

    ListView listView;
    DatabaseHelper dbHelper;
    ArrayList<String> requestList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        listView = findViewById(R.id.listViewRequests);
        dbHelper = new DatabaseHelper(this);

        loadRequests();
    }

    private void loadRequests() {
        requestList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllRequests();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No requests found", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            String request =
                    "Name: " + cursor.getString(2) + "\n" +   // student_name
                            "Gender: " + cursor.getString(3) + "\n" + // gender
                            "Class: " + cursor.getString(4) + "\n" +  // class_section
                            "Room: " + cursor.getString(5) + "\n" +   // room_type
                            "Members: " + cursor.getInt(6) + "\n" +   // members_needed
                            "Email: " + cursor.getString(8) + "\n" +  // contact
                            "Desc: " + cursor.getString(7);           // description

            requestList.add(request);
        }

        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requestList);
        listView.setAdapter(adapter);
    }
}
