package com.example.studentaid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StudentAid.db";
    private static final int DATABASE_VERSION = 18;  // ✅ INCREMENTED FOR NEW COLUMN
    private static final String TAG = "DatabaseHelper";

    // ========== USERS TABLE ==========
    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_NAME = "name";
    public static final String COL_PHONE = "phone";
    public static final String COL_USER_ROLE = "userRole";

    // ========== ROOMMATE REQUESTS TABLE ==========
    public static final String TABLE_ROOMMATE = "roommate_requests";
    public static final String COL_REQ_ID = "req_id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_STUDENT_NAME = "student_name";
    public static final String COL_GENDER = "gender";
    public static final String COL_CLASS_SECTION = "class_section";
    public static final String COL_ROOM_TYPE = "room_type";
    public static final String COL_MEMBERS_NEEDED = "members_needed";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_CONTACT = "contact";

    // ========== LOST & FOUND TABLE ==========
    public static final String TABLE_LOST_FOUND = "lost_found";
    public static final String COL_LF_ID = "lf_id";
    public static final String COL_ITEM_NAME = "item_name";
    public static final String COL_ITEM_DESC = "item_desc";
    public static final String COL_CATEGORY = "category";
    public static final String COL_LOCATION = "location";
    public static final String COL_DATE = "date";
    public static final String COL_LF_CONTACT = "lf_contact";
    public static final String COL_PHOTO_PATH = "photo_path";  // ✅ NEW COLUMN

    // ========== GUIDANCE MESSAGES TABLE ==========
    public static final String TABLE_GUIDANCE = "guidance_messages";
    public static final String COL_MSG_ID = "msg_id";
    public static final String COL_SENDER_ID = "sender_id";
    public static final String COL_SENDER_NAME = "sender_name";
    public static final String COL_SENDER_ROLE = "sender_role";
    public static final String COL_MESSAGE = "message";
    public static final String COL_TIMESTAMP = "timestamp";

    // ========== STUDY MATERIALS TABLE ==========
    public static final String TABLE_STUDY_MATERIALS = "study_materials";
    public static final String COL_MATERIAL_ID = "material_id";
    public static final String COL_TITLE = "title";
    public static final String COL_SUBJECT = "subject";
    public static final String COL_TYPE = "type";
    public static final String COL_FILE_PATH = "file_path";
    public static final String COL_UPLOADER_NAME = "uploader_name";
    public static final String COL_UPLOAD_DATE = "upload_date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating tables...");

        // ✅ CREATE USERS TABLE
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_USER_ROLE + " TEXT DEFAULT 'Student')");

        // ✅ CREATE ROOMMATE REQUESTS TABLE
        db.execSQL("CREATE TABLE " + TABLE_ROOMMATE + " (" +
                COL_REQ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_STUDENT_NAME + " TEXT, " +
                COL_GENDER + " TEXT, " +
                COL_CLASS_SECTION + " TEXT, " +
                COL_ROOM_TYPE + " TEXT, " +
                COL_MEMBERS_NEEDED + " INTEGER, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_CONTACT + " TEXT, " +
                "FOREIGN KEY (" + COL_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE)");

        // ✅ CREATE LOST & FOUND TABLE WITH PHOTO PATH
        db.execSQL("CREATE TABLE " + TABLE_LOST_FOUND + " (" +
                COL_LF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_ITEM_NAME + " TEXT NOT NULL, " +
                COL_ITEM_DESC + " TEXT, " +
                COL_CATEGORY + " TEXT NOT NULL, " +
                COL_LOCATION + " TEXT, " +
                COL_DATE + " TEXT, " +
                COL_LF_CONTACT + " TEXT NOT NULL, " +
                COL_PHOTO_PATH + " TEXT, " +  // ✅ NEW COLUMN
                "FOREIGN KEY (" + COL_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE)");

        // ✅ CREATE GUIDANCE MESSAGES TABLE
        db.execSQL("CREATE TABLE " + TABLE_GUIDANCE + " (" +
                COL_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SENDER_ID + " INTEGER NOT NULL, " +
                COL_SENDER_NAME + " TEXT NOT NULL, " +
                COL_SENDER_ROLE + " TEXT NOT NULL, " +
                COL_MESSAGE + " TEXT NOT NULL, " +
                COL_TIMESTAMP + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COL_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE)");

        // ✅ CREATE STUDY MATERIALS TABLE
        db.execSQL("CREATE TABLE " + TABLE_STUDY_MATERIALS + " (" +
                COL_MATERIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID + " INTEGER NOT NULL, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_SUBJECT + " TEXT NOT NULL, " +
                COL_TYPE + " TEXT NOT NULL, " +
                COL_FILE_PATH + " TEXT NOT NULL, " +
                COL_UPLOADER_NAME + " TEXT NOT NULL, " +
                COL_UPLOAD_DATE + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + COL_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_ID + ") ON DELETE CASCADE)");

        Log.d(TAG, "onCreate: Tables created successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        // ✅ DROP ALL TABLES IN CORRECT ORDER (respecting foreign keys)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDY_MATERIALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUIDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOST_FOUND);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOMMATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }

    // ========== UTILITY FUNCTIONS ==========

    /**
     * ✅ Get current date/time as string
     */
    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // ========== USER FUNCTIONS ==========

    /**
     * Register new user with full details
     */
    public boolean registerUserWithDetails(String name, String email, String phone, String password, String userRole) {
        if (checkEmailExists(email)) {
            Log.w(TAG, "registerUserWithDetails: Email already exists - " + email);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PHONE, phone);
        values.put(COL_PASSWORD, password);
        values.put(COL_USER_ROLE, userRole);

        long result = db.insert(TABLE_USERS, null, values);
        Log.i(TAG, "registerUserWithDetails: Insert result for " + email + ": " + result);
        return result != -1;
    }

    /**
     * Check if email already exists
     */
    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COL_ID}, COL_EMAIL + "=?",
                    new String[]{email}, null, null, null);
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Get user ID by email
     */
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int userId = -1;
        try {
            cursor = db.query(TABLE_USERS, new String[]{COL_ID}, COL_EMAIL + "=?",
                    new String[]{email}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting userId by email: " + email, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        Log.d(TAG, "getUserIdByEmail for " + email + " returning: " + userId);
        return userId;
    }

    /**
     * Check user login credentials
     */
    public boolean checkUser(String email, String password, String userRole) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT " + COL_ID + " FROM " + TABLE_USERS +
                            " WHERE " + COL_EMAIL + "=? AND " + COL_PASSWORD + "=? AND " + COL_USER_ROLE + "=?",
                    new String[]{email, password, userRole}
            );
            return cursor != null && cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Get user name by ID
     */
    public String getUserNameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String userName = "Unknown";
        try {
            cursor = db.query(TABLE_USERS, new String[]{COL_NAME}, COL_ID + "=?",
                    new String[]{String.valueOf(userId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                userName = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting userName by ID: " + userId, e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return userName;
    }

    // ========== ROOMMATE FUNCTIONS ==========

    /**
     * Insert new roommate request
     */
    public boolean insertRoommateRequest(int userId, String studentName, String gender,
                                         String classSection, String roomType, int membersNeeded,
                                         String description, String contactEmail) {
        if (userId <= 0) {
            Log.e(TAG, "insertRoommateRequest: Invalid userId provided: " + userId);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_STUDENT_NAME, studentName);
        values.put(COL_GENDER, gender);
        values.put(COL_CLASS_SECTION, classSection);
        values.put(COL_ROOM_TYPE, roomType);
        values.put(COL_MEMBERS_NEEDED, membersNeeded);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_CONTACT, contactEmail);

        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_ROOMMATE, null, values);
        } catch (Exception e) {
            Log.e(TAG, "insertRoommateRequest: Error inserting. UserID=" + userId, e);
        }

        Log.i(TAG, "insertRoommateRequest for UserID " + userId + ": result=" + result);
        return result != -1;
    }

    /**
     * Get all roommate requests
     */
    public Cursor getAllRequests() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ROOMMATE + " ORDER BY " + COL_REQ_ID + " DESC", null);
    }

    /**
     * Get user's own roommate requests
     */
    public Cursor getMyRequests(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ROOMMATE +
                        " WHERE " + COL_USER_ID + "=? ORDER BY " + COL_REQ_ID + " DESC",
                new String[]{String.valueOf(userId)});
    }

    /**
     * Delete roommate request by matching name and contact
     */
    public boolean deleteRequestByText(String name, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ROOMMATE,
                COL_STUDENT_NAME + "=? AND " + COL_CONTACT + "=?",
                new String[]{name, contact});
        Log.d(TAG, "deleteRequestByText: Name=" + name + ", Rows affected=" + result);
        return result > 0;
    }

    // ========== LOST & FOUND FUNCTIONS ==========

    /**
     * ✅ UPDATED: Insert lost/found item WITH PHOTO PATH
     */
    public boolean insertLostFound(int userId, String itemName, String itemDesc,
                                   String category, String location, String date,
                                   String lfContact, String photoPath) {
        if (userId <= 0) {
            Log.e(TAG, "insertLostFound: Invalid userId provided: " + userId);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_ITEM_NAME, itemName);
        values.put(COL_ITEM_DESC, itemDesc);
        values.put(COL_CATEGORY, category);
        values.put(COL_LOCATION, location);
        values.put(COL_DATE, date);
        values.put(COL_LF_CONTACT, lfContact);
        values.put(COL_PHOTO_PATH, photoPath);  // ✅ SAVE PHOTO PATH

        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_LOST_FOUND, null, values);
        } catch (Exception e) {
            Log.e(TAG, "insertLostFound: Error inserting item. UserID=" + userId, e);
        }

        Log.i(TAG, "insertLostFound for UserID " + userId + ": result=" + result);
        return result != -1;
    }

    /**
     * ✅ UPDATED: Insert lost item WITH PHOTO PATH
     */
    public boolean insertLostItem(int userId, String itemName, String itemDesc,
                                  String location, String contact, String photoPath) {
        Log.d(TAG, "insertLostItem called with UserID=" + userId + ", Item=" + itemName);
        return insertLostFound(userId, itemName, itemDesc, "Lost", location,
                getCurrentDate(), contact, photoPath);
    }

    /**
     * Get all lost/found items
     */
    public Cursor getAllLostFound() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LOST_FOUND + " ORDER BY " + COL_LF_ID + " DESC", null);
    }

    /**
     * Get lost/found items by category
     */
    public Cursor getLostFoundByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LOST_FOUND +
                        " WHERE " + COL_CATEGORY + "=? ORDER BY " + COL_LF_ID + " DESC",
                new String[]{category});
    }

    /**
     * Get all lost items
     */
    public Cursor getAllLostItems() {
        Log.d(TAG, "getAllLostItems called. Returning items with category 'Lost'.");
        return getLostFoundByCategory("Lost");
    }

    /**
     * Delete lost/found item
     */
    public boolean deleteLostItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_LOST_FOUND, COL_LF_ID + "=?", new String[]{String.valueOf(itemId)});
        Log.d(TAG, "deleteLostItem: ItemID=" + itemId + ", Rows affected=" + result);
        return result > 0;
    }

    /**
     * Update lost/found item
     */
    public boolean updateLostFound(int id, String itemName, String itemDesc, String category,
                                   String location, String date, String contact, String photoPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ITEM_NAME, itemName);
        values.put(COL_ITEM_DESC, itemDesc);
        values.put(COL_CATEGORY, category);
        values.put(COL_LOCATION, location);
        values.put(COL_DATE, date);
        values.put(COL_LF_CONTACT, contact);
        if (photoPath != null) {
            values.put(COL_PHOTO_PATH, photoPath);
        }

        int rowsAffected = db.update(TABLE_LOST_FOUND, values, COL_LF_ID + "=?", new String[]{String.valueOf(id)});
        Log.d(TAG, "updateLostFound: ItemID=" + id + ", Rows affected=" + rowsAffected);
        return rowsAffected > 0;
    }

    // ========== GUIDANCE MESSAGES FUNCTIONS ==========

    /**
     * Insert guidance message
     */
    public boolean insertGuidanceMessage(int senderId, String senderName, String senderRole, String message) {
        if (senderId <= 0) {
            Log.e(TAG, "insertGuidanceMessage: Invalid senderId provided: " + senderId);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SENDER_ID, senderId);
        values.put(COL_SENDER_NAME, senderName);
        values.put(COL_SENDER_ROLE, senderRole);
        values.put(COL_MESSAGE, message);
        values.put(COL_TIMESTAMP, getCurrentDate());

        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_GUIDANCE, null, values);
        } catch (Exception e) {
            Log.e(TAG, "insertGuidanceMessage: Error inserting message. SenderID=" + senderId, e);
        }

        Log.i(TAG, "insertGuidanceMessage for SenderID " + senderId + ": result=" + result);
        return result != -1;
    }

    /**
     * Get all guidance messages
     */
    public Cursor getAllGuidanceMessages() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GUIDANCE + " ORDER BY " + COL_MSG_ID + " ASC", null);
    }

    /**
     * Delete guidance message
     */
    public boolean deleteGuidanceMessage(int msgId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_GUIDANCE, COL_MSG_ID + "=?", new String[]{String.valueOf(msgId)});
        Log.d(TAG, "deleteGuidanceMessage: MsgID=" + msgId + ", Rows affected=" + result);
        return result > 0;
    }

    // ========== STUDY MATERIALS FUNCTIONS ==========

    /**
     * Insert study material
     */
    public boolean insertStudyMaterial(int userId, String title, String description,
                                       String subject, String type, String filePath, String uploaderName) {
        if (userId <= 0) {
            Log.e(TAG, "insertStudyMaterial: Invalid userId provided: " + userId);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, userId);
        values.put(COL_TITLE, title);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_SUBJECT, subject);
        values.put(COL_TYPE, type);
        values.put(COL_FILE_PATH, filePath);
        values.put(COL_UPLOADER_NAME, uploaderName);
        values.put(COL_UPLOAD_DATE, getCurrentDate());

        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_STUDY_MATERIALS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "insertStudyMaterial: Error inserting. UserID=" + userId, e);
        }

        Log.i(TAG, "insertStudyMaterial for UserID " + userId + ": result=" + result);
        return result != -1;
    }

    /**
     * Get all study materials
     */
    public Cursor getAllStudyMaterials() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDY_MATERIALS +
                " ORDER BY " + COL_MATERIAL_ID + " DESC", null);
    }

    /**
     * Get study materials by subject
     */
    public Cursor getStudyMaterialsBySubject(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_STUDY_MATERIALS +
                        " WHERE " + COL_SUBJECT + "=? ORDER BY " + COL_MATERIAL_ID + " DESC",
                new String[]{subject});
    }

    /**
     * Delete study material
     */
    public boolean deleteStudyMaterial(int materialId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_STUDY_MATERIALS, COL_MATERIAL_ID + "=?",
                new String[]{String.valueOf(materialId)});
        Log.d(TAG, "deleteStudyMaterial: MaterialID=" + materialId + ", Rows affected=" + result);
        return result > 0;
    }
}
