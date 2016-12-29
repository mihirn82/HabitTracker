package com.example.android.habittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.android.habittracker.data.HabitContract.HabitEntry;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insertHabit();
    }

    @Override
    protected void onStart(){
        super.onStart();
        insertHabit();
        displayDatabaseInfo();
    }
    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the habits database.
     */
    private void displayDatabaseInfo() {

        String[] projection = {
                HabitEntry._ID,
                HabitEntry.COLUMN_HABIT_ACTIVITY,
                HabitEntry.COLUMN_HABIT_DATE,
                HabitEntry.COLUMN_HABIT_DURATION,
        };

        Cursor cursor = getContentResolver().query(HabitEntry.CONTENT_URI,projection,null,null,null);
    }

    /**
     * Helper method to insert hardcoded habit data into the database. For debugging purposes only.
     */
    private void insertHabit() {

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(HabitEntry.COLUMN_HABIT_ACTIVITY, "Reading");
        values.put(HabitEntry.COLUMN_HABIT_DATE,"12272016");
        values.put(HabitEntry.COLUMN_HABIT_DURATION,"30");

        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(HabitEntry.CONTENT_URI, values);
    }
}
