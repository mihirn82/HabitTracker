package com.example.android.habittracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.habittracker.data.HabitContract.HabitEntry;

/**
 * Created by mihirnewalkar on 12/27/16.
 */

public class HabitDbHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "habit.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String TEXT_NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HabitEntry.TABLE_NAME + " (" +
                    HabitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    HabitEntry.COLUMN_HABIT_ACTIVITY + TEXT_TYPE + TEXT_NOT_NULL + COMMA_SEP +
                    HabitEntry.COLUMN_HABIT_DATE + TEXT_TYPE + COMMA_SEP +
                    HabitEntry.COLUMN_HABIT_DURATION + " INTEGER NOT NULL" + " );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HabitEntry.TABLE_NAME;

    public HabitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
