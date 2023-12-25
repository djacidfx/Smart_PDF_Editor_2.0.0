package com.templatemela.smartpdfreader.util;

import android.os.Handler;
import android.os.Looper;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.templatemela.smartpdfreader.interfaces.MergeFilesListener;

import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MergePdf {
    public static String mFilename;
    public static String mFinPath;
    public static Boolean mIsPDFMerged;

    public static void mergerPdf(String filename, String finPath, boolean mIsPasswordProtected, String mPassword, MergeFilesListener mMergeFilesListener, String mMasterPwd, String... params) {
        mFilename = filename;
        mFinPath = finPath;
        mIsPDFMerged = false;
        mMergeFilesListener.mergeStarted();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            try {
                Document document = new Document();
                mFilename += Constants.pdfExtension;
                mFinPath += mFilename;
                PdfCopy pdfCopy = new PdfCopy(document, new FileOutputStream(mFinPath));
                if (mIsPasswordProtected) {
                    pdfCopy.setEncryption(mPassword.getBytes(), mMasterPwd.getBytes(), 2068, 2);
                }
                document.open();
                int length = params.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        PdfReader pdfReader = new PdfReader(params[i]);
                        int numberOfPages = pdfReader.getNumberOfPages();
                        for (int j = 1; j <= numberOfPages; j++) {
                            pdfCopy.addPage(pdfCopy.getImportedPage(pdfReader, j));
                        }
                        i++;
                    } else {
                        mIsPDFMerged = true;
                        document.close();
                        break;
                    }
                }
            } catch (Exception e) {
                mIsPDFMerged = false;
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here
                mMergeFilesListener.resetValues(mIsPDFMerged, mFinPath);
            });
        });
    }
}
