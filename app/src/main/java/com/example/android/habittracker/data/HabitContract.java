package com.example.android.habittracker.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mihirnewalkar on 12/27/16.
 */

public class HabitContract {

    private HabitContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.habittracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_HABITS= "habits";

    public static abstract class HabitEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_HABITS);

        public static final String TABLE_NAME = "habits";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_HABIT_ACTIVITY = "activity";
        public static final String COLUMN_HABIT_DATE = "date";
        public static final String COLUMN_HABIT_DURATION = "duration";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of habits.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HABITS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single habit.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HABITS;
    }
}
