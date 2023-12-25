package com.templatemela.smartpdfreader.util;

import android.os.Handler;
import android.os.Looper;

import com.templatemela.smartpdfreader.interfaces.OnTextToPdfInterface;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TextToPdfConverter {

    public static boolean mSuccess;

    public static void textToPdfConverter(TextToPDFUtils mTexttoPDFUtil, TextToPDFOptions mTextToPdfOptions, String mFileExtension, OnTextToPdfInterface mOnPDFCreatedInterface) {
        mSuccess = true;
        mOnPDFCreatedInterface.onPDFCreationStarted();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            try {
                mTexttoPDFUtil.createPdfFromTextFile(mTextToPdfOptions, mFileExtension);
            } catch (Exception e) {
                mSuccess = false;
                e.printStackTrace();
            }
            handler.post(() -> {
                //UI Thread work here
                mOnPDFCreatedInterface.onPDFCreated(mSuccess);
            });
        });
    }

}
