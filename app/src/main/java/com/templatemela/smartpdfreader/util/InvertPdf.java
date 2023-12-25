package com.templatemela.smartpdfreader.util;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.templatemela.smartpdfreader.interfaces.OnPDFCreatedInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvertPdf {
    public static Boolean mIsNewPDFCreated;
    public static String mPath;

    public static void invertPdfFiles(String path, OnPDFCreatedInterface mOnPDFCreatedInterface) {
        mPath = path;
        mOnPDFCreatedInterface.onPDFCreationStarted();
        mIsNewPDFCreated = false;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            try {
                if ((mPath != null ? ParcelFileDescriptor.open(new File(mPath), 268435456) : null) != null) {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + Constants.pdfDirectory);
                    String newPath = file + File.separator + FileUtils.getFileNameWithoutExtension(mPath) + "_inverted.pdf";

                    String replace = mPath.replace(".advanced", "_inverted.pdf");
                    if (createPDF(mPath, newPath)) {
                        mPath = newPath;
                        mIsNewPDFCreated = true;
                    }
                }
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
                mIsNewPDFCreated = false;
            }

            handler.post(() -> {
                //UI Thread work here
                mOnPDFCreatedInterface.onPDFCreated(mIsNewPDFCreated, mPath);
            });
        });
    }

    public static boolean createPDF(String path, String os) {
        try {
            PdfReader pdfReader = new PdfReader(path);
            FileOutputStream fileOutputStream = new FileOutputStream(os);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, fileOutputStream);
            invert(pdfStamper);
            pdfStamper.close();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void invert(PdfStamper pdfStamper) {
        for (int numberOfPages = pdfStamper.getReader().getNumberOfPages(); numberOfPages > 0; numberOfPages--) {
            invertPage(pdfStamper, numberOfPages);
        }
    }

    public static void invertPage(PdfStamper pdfStamper, int i) {
        Rectangle pageSize = pdfStamper.getReader().getPageSize(i);
        PdfContentByte overContent = pdfStamper.getOverContent(i);
        PdfGState pdfGState = new PdfGState();
        pdfGState.setBlendMode(PdfGState.BM_DIFFERENCE);
        overContent.setGState(pdfGState);
        overContent.setColorFill(new GrayColor(1.0f));
        overContent.rectangle(pageSize.getLeft(), pageSize.getBottom(), pageSize.getWidth(), pageSize.getHeight());
        overContent.fill();
        PdfContentByte underContent = pdfStamper.getUnderContent(i);
        underContent.setColorFill(new GrayColor(1.0f));
        underContent.rectangle(pageSize.getLeft(), pageSize.getBottom(), pageSize.getWidth(), pageSize.getHeight());
        underContent.fill();
    }

}
