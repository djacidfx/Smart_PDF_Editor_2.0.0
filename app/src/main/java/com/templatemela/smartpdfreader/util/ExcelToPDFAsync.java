package com.templatemela.smartpdfreader.util;

import android.os.Handler;
import android.os.Looper;

import com.aspose.cells.PdfSaveOptions;
import com.aspose.cells.PdfSecurityOptions;
import com.aspose.cells.Workbook;
import com.templatemela.smartpdfreader.interfaces.OnPDFCreatedInterface;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcelToPDFAsync {

    public static boolean mSuccess;

    public static void excelToPdfConverter(String mPath, String mDestPath, OnPDFCreatedInterface mOnPDFCreatedInterface, boolean mIsPasswordProtected, String mPassword) {
        mSuccess = true;
        mOnPDFCreatedInterface.onPDFCreationStarted();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            try {
                Workbook workbook = new Workbook(mPath);
                if (mIsPasswordProtected) {
                    PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
                    pdfSaveOptions.setSecurityOptions(new PdfSecurityOptions());
                    pdfSaveOptions.getSecurityOptions().setUserPassword(mPassword);
                    pdfSaveOptions.getSecurityOptions().setOwnerPassword(mPassword);
                    pdfSaveOptions.getSecurityOptions().setExtractContentPermission(false);
                    pdfSaveOptions.getSecurityOptions().setPrintPermission(false);
                    workbook.save(mDestPath, pdfSaveOptions);
                }
                workbook.save(mDestPath, 13);
            } catch (Exception e) {
                e.printStackTrace();
                mSuccess = false;
            }

            handler.post(() -> {
                //UI Thread work here
                mOnPDFCreatedInterface.onPDFCreated(mSuccess, mPath);
            });
        });
    }

}
