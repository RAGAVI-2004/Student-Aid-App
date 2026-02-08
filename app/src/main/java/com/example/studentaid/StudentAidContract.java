package com.example.studentaid;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for StudentAid ContentProvider
 * Defines URIs and column names for all tables
 */
public final class StudentAidContract {

    // Authority for the ContentProvider
    public static final String AUTHORITY = "com.example.studentaid.provider";

    // Base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Private constructor to prevent instantiation
    private StudentAidContract() {}

    // ========== USERS TABLE ==========
    public static final class Users implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        // Column names
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_ROLE = "role";
    }

    // ========== ROOMMATE REQUESTS TABLE ==========
    public static final class RoommateRequests implements BaseColumns {
        public static final String TABLE_NAME = "roommate_requests";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        // Column names
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_STUDENT_NAME = "student_name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_CLASS_SECTION = "class_section";
        public static final String COLUMN_ROOM_TYPE = "room_type";
        public static final String COLUMN_MEMBERS_NEEDED = "members_needed";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CONTACT = "contact";
    }

    // ========== LOST & FOUND TABLE ==========
    public static final class LostFound implements BaseColumns {
        public static final String TABLE_NAME = "lost_found";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        // Column names
        public static final String COLUMN_USER_ID = "user_id";
        public static final String COLUMN_ITEM_NAME = "item_name";
        public static final String COLUMN_ITEM_DESC = "item_desc";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_CONTACT = "lf_contact";
    }

    // ========== GUIDANCE MESSAGES TABLE ==========
    public static final class GuidanceMessages implements BaseColumns {
        public static final String TABLE_NAME = "guidance_messages";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        // Column names
        public static final String COLUMN_SENDER_ID = "sender_id";
        public static final String COLUMN_SENDER_NAME = "sender_name";
        public static final String COLUMN_SENDER_ROLE = "sender_role";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
