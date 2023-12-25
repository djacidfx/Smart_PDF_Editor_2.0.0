package com.templatemela.smartpdfreader.database;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseHelper {

    public final Context mContext;

    public DatabaseHelper(Context context) {
        this.mContext = context;
    }

    public void insertRecord(String path, String operationType) {
        insertRecord(new History(path, new Date().toString(), operationType));
    }

    private void insertRecord(History history) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            AppDatabase.getDatabase(mContext.getApplicationContext()).historyDao().insertAll(history);

            handler.post(() -> {
                //UI Thread work here
            });
        });
    }
}
