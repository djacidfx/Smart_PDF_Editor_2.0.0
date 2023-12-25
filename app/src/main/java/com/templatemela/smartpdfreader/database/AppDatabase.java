package com.templatemela.smartpdfreader.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.templatemela.smartpdfreader.util.Constants;

@Database(entities = {History.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract HistoryDao historyDao();

    public static AppDatabase getDatabase(Context context) {
        INSTANCE = Room.databaseBuilder(context, AppDatabase.class, Constants.DATABASE_NAME).build();
        return INSTANCE;
    }


}
