package com.example.studentaid;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * ContentProvider for Student Aid Application
 * Provides standardized access to app data
 */
public class StudentAidProvider extends ContentProvider {

    private static final String TAG = "StudentAidProvider";
    private DatabaseHelper dbHelper;

    // URI matcher codes
    private static final int USERS = 100;
    private static final int USER_ID = 101;
    private static final int ROOMMATE_REQUESTS = 200;
    private static final int ROOMMATE_REQUEST_ID = 201;
    private static final int LOST_FOUND = 300;
    private static final int LOST_FOUND_ID = 301;
    private static final int GUIDANCE_MESSAGES = 400;
    private static final int GUIDANCE_MESSAGE_ID = 401;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Users URIs
        uriMatcher.addURI(StudentAidContract.AUTHORITY,
                StudentAidContract.Users.TABLE_NAME, USERS);
        uriMatcher.addURI(StudentAidContract.AUTHORITY,
                StudentAidContract.Users.TABLE_NAME + "/#", USER_ID);

        // Roommate Requests URIs
        uriMatcher.addURI(StudentAidContract.AUTHORITY,
                StudentAidContract.RoommateRequests.TABLE_NAME, ROOMMATE_REQUESTS);
        uriMatcher.addURI(StudentAidContract.AUTHORITY,
                StudentAidContract.RoommateRequests.TABLE_NAME + "/#", ROOMMATE_REQUEST_ID);

        // Lost & Found URIs
        uriMatcher.addURI(StudentAidContract.AUTHORITY,
                StudentAidContract.LostFound.TABLE_NAME, LOST_FOUND);
        uriMatcher.addURI(StudentAidContract.AUTHORITY,
                StudentAidContract.LostFound.TABLE_NAME + "/#", LOST_FOUND_ID);

        // Guidance Messages URIs
        uriMatcher.addURI(StudentAidContract.AUTHORITY,
                StudentAidContract.GuidanceMessages.TABLE_NAME, GUIDANCE_MESSAGES);
        uriMatcher.addURI(StudentAidContract.AUTHORITY,
                StudentAidContract.GuidanceMessages.TABLE_NAME + "/#", GUIDANCE_MESSAGE_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        Log.d(TAG, "ContentProvider created");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);

        switch (match) {
            case USERS:
                cursor = db.query(DatabaseHelper.TABLE_USERS, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case USER_ID:
                selection = DatabaseHelper.COL_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(DatabaseHelper.TABLE_USERS, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case ROOMMATE_REQUESTS:
                cursor = db.query(DatabaseHelper.TABLE_ROOMMATE, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case ROOMMATE_REQUEST_ID:
                selection = DatabaseHelper.COL_REQ_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(DatabaseHelper.TABLE_ROOMMATE, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case LOST_FOUND:
                cursor = db.query(DatabaseHelper.TABLE_LOST_FOUND, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case LOST_FOUND_ID:
                selection = DatabaseHelper.COL_LF_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(DatabaseHelper.TABLE_LOST_FOUND, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case GUIDANCE_MESSAGES:
                cursor = db.query(DatabaseHelper.TABLE_GUIDANCE, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            case GUIDANCE_MESSAGE_ID:
                selection = DatabaseHelper.COL_MSG_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(DatabaseHelper.TABLE_GUIDANCE, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Set notification URI
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        Log.d(TAG, "Query: " + uri + " returned " + cursor.getCount() + " rows");
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case USERS:
                return "vnd.android.cursor.dir/vnd." + StudentAidContract.AUTHORITY + ".users";
            case USER_ID:
                return "vnd.android.cursor.item/vnd." + StudentAidContract.AUTHORITY + ".users";
            case ROOMMATE_REQUESTS:
                return "vnd.android.cursor.dir/vnd." + StudentAidContract.AUTHORITY + ".roommate_requests";
            case ROOMMATE_REQUEST_ID:
                return "vnd.android.cursor.item/vnd." + StudentAidContract.AUTHORITY + ".roommate_requests";
            case LOST_FOUND:
                return "vnd.android.cursor.dir/vnd." + StudentAidContract.AUTHORITY + ".lost_found";
            case LOST_FOUND_ID:
                return "vnd.android.cursor.item/vnd." + StudentAidContract.AUTHORITY + ".lost_found";
            case GUIDANCE_MESSAGES:
                return "vnd.android.cursor.dir/vnd." + StudentAidContract.AUTHORITY + ".guidance_messages";
            case GUIDANCE_MESSAGE_ID:
                return "vnd.android.cursor.item/vnd." + StudentAidContract.AUTHORITY + ".guidance_messages";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        long id;
        Uri returnUri;

        switch (match) {
            case USERS:
                id = db.insert(DatabaseHelper.TABLE_USERS, null, values);
                returnUri = ContentUris.withAppendedId(StudentAidContract.Users.CONTENT_URI, id);
                break;

            case ROOMMATE_REQUESTS:
                id = db.insert(DatabaseHelper.TABLE_ROOMMATE, null, values);
                returnUri = ContentUris.withAppendedId(StudentAidContract.RoommateRequests.CONTENT_URI, id);
                break;

            case LOST_FOUND:
                id = db.insert(DatabaseHelper.TABLE_LOST_FOUND, null, values);
                returnUri = ContentUris.withAppendedId(StudentAidContract.LostFound.CONTENT_URI, id);
                break;

            case GUIDANCE_MESSAGES:
                id = db.insert(DatabaseHelper.TABLE_GUIDANCE, null, values);
                returnUri = ContentUris.withAppendedId(StudentAidContract.GuidanceMessages.CONTENT_URI, id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Notify observers
        if (getContext() != null && id != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "Inserted into " + uri + ", ID: " + id);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case USERS:
                rowsDeleted = db.delete(DatabaseHelper.TABLE_USERS, selection, selectionArgs);
                break;

            case USER_ID:
                selection = DatabaseHelper.COL_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(DatabaseHelper.TABLE_USERS, selection, selectionArgs);
                break;

            case ROOMMATE_REQUESTS:
                rowsDeleted = db.delete(DatabaseHelper.TABLE_ROOMMATE, selection, selectionArgs);
                break;

            case ROOMMATE_REQUEST_ID:
                selection = DatabaseHelper.COL_REQ_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(DatabaseHelper.TABLE_ROOMMATE, selection, selectionArgs);
                break;

            case LOST_FOUND:
                rowsDeleted = db.delete(DatabaseHelper.TABLE_LOST_FOUND, selection, selectionArgs);
                break;

            case LOST_FOUND_ID:
                selection = DatabaseHelper.COL_LF_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(DatabaseHelper.TABLE_LOST_FOUND, selection, selectionArgs);
                break;

            case GUIDANCE_MESSAGES:
                rowsDeleted = db.delete(DatabaseHelper.TABLE_GUIDANCE, selection, selectionArgs);
                break;

            case GUIDANCE_MESSAGE_ID:
                selection = DatabaseHelper.COL_MSG_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(DatabaseHelper.TABLE_GUIDANCE, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Notify observers
        if (getContext() != null && rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "Deleted from " + uri + ", rows: " + rowsDeleted);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case USERS:
                rowsUpdated = db.update(DatabaseHelper.TABLE_USERS, values,
                        selection, selectionArgs);
                break;

            case USER_ID:
                selection = DatabaseHelper.COL_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(DatabaseHelper.TABLE_USERS, values,
                        selection, selectionArgs);
                break;

            case ROOMMATE_REQUESTS:
                rowsUpdated = db.update(DatabaseHelper.TABLE_ROOMMATE, values,
                        selection, selectionArgs);
                break;

            case ROOMMATE_REQUEST_ID:
                selection = DatabaseHelper.COL_REQ_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(DatabaseHelper.TABLE_ROOMMATE, values,
                        selection, selectionArgs);
                break;

            case LOST_FOUND:
                rowsUpdated = db.update(DatabaseHelper.TABLE_LOST_FOUND, values,
                        selection, selectionArgs);
                break;

            case LOST_FOUND_ID:
                selection = DatabaseHelper.COL_LF_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(DatabaseHelper.TABLE_LOST_FOUND, values,
                        selection, selectionArgs);
                break;

            case GUIDANCE_MESSAGES:
                rowsUpdated = db.update(DatabaseHelper.TABLE_GUIDANCE, values,
                        selection, selectionArgs);
                break;

            case GUIDANCE_MESSAGE_ID:
                selection = DatabaseHelper.COL_MSG_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(DatabaseHelper.TABLE_GUIDANCE, values,
                        selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Notify observers
        if (getContext() != null && rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d(TAG, "Updated " + uri + ", rows: " + rowsUpdated);
        return rowsUpdated;
    }
}
