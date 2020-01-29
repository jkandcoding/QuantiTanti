package com.example.android.quantitanti.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {CostEntry.class}, views = {DailyExpensesView.class}, version = 1, exportSchema = false)
public abstract class CostDatabase extends RoomDatabase {

    private static final String LOG_TAG = CostDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "costlist";
    private static CostDatabase sInstance;

    public static CostDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        CostDatabase.class, CostDatabase.DATABASE_NAME)

                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract CostDao costDao();
}
