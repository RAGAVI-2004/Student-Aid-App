package com.example.studentaid;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyRequestActivity extends AppCompatActivity {

    private static final String TAG = "MyRequestActivity";

    private ListView listView;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private List<String> myRequests;
    private List<Integer> requestIds;  // ✅ Track IDs for deletion

    private String loggedInUserEmail = "";
    private int loggedInUserId = -1;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        listView = findViewById(R.id.listViewMyRequests);
        Button btnDelete = findViewById(R.id.btnDeleteRequest);
        dbHelper = new DatabaseHelper(this);

        myRequests = new ArrayList<>();
        requestIds = new ArrayList<>();

        // ✅ Get email from intent
        loggedInUserEmail = getIntent().getStringExtra("email");
        if (loggedInUserEmail == null || loggedInUserEmail.isEmpty()) {
            Toast.makeText(this, "⚠️ User not logged in properly", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No email received!");
            finish();
            return;
        }

        // ✅ Convert email to userId
        loggedInUserId = dbHelper.getUserIdByEmail(loggedInUserEmail);
        if (loggedInUserId <= 0) {
            Toast.makeText(this, "⚠️ User not found in database", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid userId for email: " + loggedInUserEmail);
            finish();
            return;
        }

        Log.d(TAG, "✅ Logged in as: " + loggedInUserEmail + " (UserID: " + loggedInUserId + ")");

        // Load user's requests
        loadMyRequests();

        // Handle list item selection
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                Log.d(TAG, "Selected request at position: " + position);
            }
        });

        // Handle delete button click
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition < 0 || selectedPosition >= requestIds.size()) {
                    Toast.makeText(MyRequestActivity.this, "Please select a request to delete", Toast.LENGTH_SHORT).show();
                    return;
                }

                int requestId = requestIds.get(selectedPosition);
                boolean deleted = deleteRequestById(requestId);

                if (deleted) {
                    Toast.makeText(MyRequestActivity.this, "✅ Request Deleted", Toast.LENGTH_SHORT).show();
                    selectedPosition = -1;
                    loadMyRequests();
                } else {
                    Toast.makeText(MyRequestActivity.this, "❌ Failed to delete request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Load user's roommate requests from database
     */
    private void loadMyRequests() {
        myRequests.clear();
        requestIds.clear();

        // ✅ Use Cursor to read database results
        Cursor cursor = dbHelper.getMyRequests(loggedInUserId);

        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No requests found", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
            return;
        }

        // ✅ Read all columns
        int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_REQ_ID);
        int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_STUDENT_NAME);
        int genderIndex = cursor.getColumnIndex(DatabaseHelper.COL_GENDER);
        int classIndex = cursor.getColumnIndex(DatabaseHelper.COL_CLASS_SECTION);
        int roomTypeIndex = cursor.getColumnIndex(DatabaseHelper.COL_ROOM_TYPE);
        int membersIndex = cursor.getColumnIndex(DatabaseHelper.COL_MEMBERS_NEEDED);
        int contactIndex = cursor.getColumnIndex(DatabaseHelper.COL_CONTACT);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIndex);
            String name = cursor.getString(nameIndex);
            String gender = cursor.getString(genderIndex);
            String classSection = cursor.getString(classIndex);
            String roomType = cursor.getString(roomTypeIndex);
            int members = cursor.getInt(membersIndex);
            String contact = cursor.getString(contactIndex);

            // ✅ Format display string
            String displayText = name + " | " + gender + " | " + classSection +
                    " | " + roomType + " | " + members + " members" +
                    "\n📧 " + contact;

            myRequests.add(displayText);
            requestIds.add(id);

            Log.d(TAG, "Loaded request ID: " + id + " - " + name);
        }

        cursor.close();

        // ✅ Update ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myRequests);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        Log.d(TAG, "Total requests loaded: " + myRequests.size());
    }

    /**
     * Delete roommate request by ID
     */
    private boolean deleteRequestById(int requestId) {
        try {
            int rowsDeleted = dbHelper.getWritableDatabase().delete(
                    DatabaseHelper.TABLE_ROOMMATE,
                    DatabaseHelper.COL_REQ_ID + "=?",
                    new String[]{String.valueOf(requestId)}
            );
            Log.d(TAG, "Deleted request ID: " + requestId + ", Rows affected: " + rowsDeleted);
            return rowsDeleted > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting request", e);
            return false;
        }
    }
}

