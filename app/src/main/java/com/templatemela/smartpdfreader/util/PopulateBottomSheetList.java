package com.templatemela.smartpdfreader.util;

import android.os.Handler;
import android.os.Looper;

import com.templatemela.smartpdfreader.interfaces.BottomSheetPopulate;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class PopulateBottomSheetList {
    public static void populateBottomFiles(BottomSheetPopulate mOnLoadListener, DirectoryUtils mDirectoryUtils, boolean isTextAndPDF) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            ArrayList<String> arrayList = mDirectoryUtils.getAllPDFsOnDevice(isTextAndPDF);

            handler.post(() -> {
                //UI Thread work here
                mOnLoadListener.onPopulate(arrayList);
            });
        });
    }
}
