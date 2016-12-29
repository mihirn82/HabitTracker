package com.example.android.habittracker.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.android.habittracker.data.HabitContract.HabitEntry;

/**
 * Created by mihirnewalkar on 12/27/16.
 */

public class HabitProvider extends ContentProvider{

    /** Tag for the log messages */
    public static final String LOG_TAG = HabitProvider.class.getSimpleName();

    /** DB helper object */
    private HabitDbHelper mDbHelper;

    /** URI matcher code for the content URI for the habits table */
    private static final int HABITS = 100;

    /** URI matcher code for the content URI for a single habit in the habits table */
    private static final int HABITS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(HabitContract.CONTENT_AUTHORITY,HabitContract.PATH_HABITS,HABITS);
        sUriMatcher.addURI(HabitContract.CONTENT_AUTHORITY,HabitContract.PATH_HABITS + "/#",HABITS_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new HabitDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case HABITS:
                // For the HABITS code, query the habits table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the habits table.
                cursor = database.query(HabitEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case HABITS_ID:
                // For the HABITS_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.habitTracker/habits/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = HabitEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the habits table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(HabitEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HABITS:
                return insertHabit(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a habit into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertHabit(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(HabitEntry.COLUMN_HABIT_ACTIVITY);
        if (name == null) {
            throw new IllegalArgumentException("Habit requires a name");
        }

        String date = values.getAsString(HabitEntry.COLUMN_HABIT_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Enter a valid date");
        }

        // Duration should be greater than 0 minutes
        Integer duration = values.getAsInteger(HabitEntry.COLUMN_HABIT_DURATION);
        if (duration != null && duration < 0) {
            throw new IllegalArgumentException("Activity requires a valid duration");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new habit with the given values
        long id = database.insert(HabitEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HABITS:
                return updateHabit(uri, contentValues, selection, selectionArgs);
            case HABITS_ID:
                // For the HABITS_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = HabitEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateHabit(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateHabit(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the {@link HabitEntry#COLUMN_HABIT_ACTIVITY} key is present,
        // check that the name value is not null.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_ACTIVITY)) {
            String name = values.getAsString(HabitEntry.COLUMN_HABIT_ACTIVITY);
            if (name == null) {
                throw new IllegalArgumentException("Habit requires an activity name");
            }
        }

        // If the {@link HabitEntry#COLUMN_HABIT_DATE} key is present,
        // check that the date is valid.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_DATE)) {
            String date = values.getAsString(HabitEntry.COLUMN_HABIT_DATE);
            if (date == null ) {
                throw new IllegalArgumentException("Activity requires date done");
            }
        }
        // If the {@link HabitEntry#COLUMN_HABIT_DURATION} key is present,
        // check that the duration value is valid.
        if (values.containsKey(HabitEntry.COLUMN_HABIT_DURATION)) {
            // Check that the duration is greater 0 minutes
            Integer duration = values.getAsInteger(HabitEntry.COLUMN_HABIT_DURATION);
            if (duration != null && duration < 0) {
                throw new IllegalArgumentException("Activity requires duration performed");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(HabitEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HABITS:
                // Delete all rows that match the selection and selection args
                return database.delete(HabitEntry.TABLE_NAME, selection, selectionArgs);
            case HABITS_ID:
                // Delete a single row given by the ID in the URI
                selection = HabitEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(HabitEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HABITS:
                return HabitEntry.CONTENT_LIST_TYPE;
            case HABITS_ID:
                return HabitEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
