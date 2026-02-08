package com.example.studentaid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewLostItemsActivity extends AppCompatActivity {

    private static final String TAG = "ViewLostItems";
    public static final String SHARED_PREFS_NAME = "StudentAidPrefs";
    public static final String KEY_USER_ID = "loggedInUserId";

    private ListView listView;
    private DatabaseHelper dbHelper;
    private List<LostItem> itemsList;
    private LostItemAdapter adapter;
    private int currentLoggedInUserId = -1;  // ✅ CURRENT USER ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lost_items);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lost Items");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // ✅ GET LOGGED-IN USER ID
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        currentLoggedInUserId = sharedPreferences.getInt(KEY_USER_ID, -1);
        Log.d(TAG, "Current logged-in user ID: " + currentLoggedInUserId);

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listViewLostItems);
        itemsList = new ArrayList<>();

        loadItems();
    }

    private void loadItems() {
        itemsList.clear();
        Cursor cursor = dbHelper.getAllLostItems();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                LostItem item = new LostItem();

                // ✅ Get item ID
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LF_ID)));

                // ✅ GET USER ID (owner of the post)
                int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COL_USER_ID);
                if (userIdIndex != -1) {
                    item.setUserId(cursor.getInt(userIdIndex));
                }

                item.setItemName(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ITEM_NAME)));

                // Get description
                int descIndex = cursor.getColumnIndex(DatabaseHelper.COL_ITEM_DESC);
                if (descIndex != -1) {
                    item.setDescription(cursor.getString(descIndex));
                }

                item.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LOCATION)));

                // Get contact
                int contactIndex = cursor.getColumnIndex(DatabaseHelper.COL_LF_CONTACT);
                if (contactIndex != -1) {
                    item.setContact(cursor.getString(contactIndex));
                }

                // Get photo path
                int photoPathIndex = cursor.getColumnIndex(DatabaseHelper.COL_PHOTO_PATH);
                if (photoPathIndex != -1) {
                    item.setPhotoPath(cursor.getString(photoPathIndex));
                }

                itemsList.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new LostItemAdapter();
        listView.setAdapter(adapter);

        if (itemsList.isEmpty()) {
            Toast.makeText(this, "No lost items found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ✅ CUSTOM ADAPTER WITH PHOTO, MAP & DELETE BUTTON
     */
    private class LostItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return itemsList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ViewLostItemsActivity.this)
                        .inflate(R.layout.item_lost_found, parent, false);
            }

            LostItem item = itemsList.get(position);

            // Find views
            ImageView ivPhoto = convertView.findViewById(R.id.ivItemPhoto);
            TextView tvName = convertView.findViewById(R.id.tvItemName);
            TextView tvDesc = convertView.findViewById(R.id.tvDescription);
            TextView tvLocation = convertView.findViewById(R.id.tvLocation);
            TextView tvContact = convertView.findViewById(R.id.tvContact);
            Button btnViewMap = convertView.findViewById(R.id.btnViewMap);
            Button btnDelete = convertView.findViewById(R.id.btnDelete);  // ✅ DELETE BUTTON

            // Set text data
            tvName.setText(item.getItemName());

            // Set description
            String description = item.getDescription();
            if (description != null && !description.isEmpty()) {
                tvDesc.setText(description);
                tvDesc.setVisibility(View.VISIBLE);
            } else {
                tvDesc.setText("No description provided");
                tvDesc.setVisibility(View.VISIBLE);
            }

            // ✅ PARSE LOCATION (check if it's lat,lng or text)
            String location = item.getLocation();
            if (location != null && location.contains(",")) {
                try {
                    String[] parts = location.split(",");
                    if (parts.length == 2) {
                        double lat = Double.parseDouble(parts[0].trim());
                        double lng = Double.parseDouble(parts[1].trim());

                        tvLocation.setText("📍 GPS: " + String.format("%.4f, %.4f", lat, lng));

                        // Enable map button
                        btnViewMap.setEnabled(true);
                        btnViewMap.setVisibility(View.VISIBLE);
                        btnViewMap.setOnClickListener(v -> openMap(lat, lng));
                    } else {
                        tvLocation.setText("📍 " + location);
                        btnViewMap.setEnabled(false);
                        btnViewMap.setVisibility(View.GONE);
                    }
                } catch (NumberFormatException e) {
                    tvLocation.setText("📍 " + location);
                    btnViewMap.setEnabled(false);
                    btnViewMap.setVisibility(View.GONE);
                }
            } else {
                tvLocation.setText("📍 " + (location != null ? location : "Not specified"));
                btnViewMap.setEnabled(false);
                btnViewMap.setVisibility(View.GONE);
            }

            // Set contact
            String contact = item.getContact();
            if (contact != null && !contact.isEmpty()) {
                tvContact.setText("📞 " + contact);
            } else {
                tvContact.setText("📞 Not provided");
            }

            // ✅ LOAD PHOTO
            String photoPath = item.getPhotoPath();
            if (photoPath != null && !photoPath.isEmpty()) {
                File photoFile = new File(photoPath);
                if (photoFile.exists()) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                        if (bitmap != null) {
                            ivPhoto.setImageBitmap(bitmap);
                            ivPhoto.setVisibility(View.VISIBLE);
                        } else {
                            ivPhoto.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading photo", e);
                        ivPhoto.setVisibility(View.GONE);
                    }
                } else {
                    ivPhoto.setVisibility(View.GONE);
                }
            } else {
                ivPhoto.setVisibility(View.GONE);
            }

            // ✅ SHOW DELETE BUTTON ONLY FOR OWNER
            if (item.getUserId() == currentLoggedInUserId && currentLoggedInUserId > 0) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> confirmDelete(item, position));
            } else {
                btnDelete.setVisibility(View.GONE);
            }

            return convertView;
        }
    }

    /**
     * ✅ CONFIRM DELETE DIALOG
     */
    private void confirmDelete(LostItem item, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete \"" + item.getItemName() + "\"?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(item, position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * ✅ DELETE ITEM FROM DATABASE
     */
    private void deleteItem(LostItem item, int position) {
        boolean deleted = dbHelper.deleteLostItem(item.getId());

        if (deleted) {
            // ✅ DELETE PHOTO FILE IF EXISTS
            String photoPath = item.getPhotoPath();
            if (photoPath != null && !photoPath.isEmpty()) {
                File photoFile = new File(photoPath);
                if (photoFile.exists()) {
                    boolean photoDeleted = photoFile.delete();
                    Log.d(TAG, "Photo file deleted: " + photoDeleted);
                }
            }

            // Remove from list and update UI
            itemsList.remove(position);
            adapter.notifyDataSetChanged();

            Toast.makeText(this, "✅ Item deleted successfully", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Item deleted: ID=" + item.getId());
        } else {
            Toast.makeText(this, "❌ Failed to delete item", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to delete item: ID=" + item.getId());
        }
    }

    /**
     * ✅ OPEN MAP WITH COORDINATES
     */
    private void openMap(double lat, double lng) {
        try {
            String uri = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback to any map app
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    // Fallback to browser
                    String url = "https://www.google.com/maps?q=" + lat + "," + lng;
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening map", e);
            Toast.makeText(this, "Unable to open map", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }
}
