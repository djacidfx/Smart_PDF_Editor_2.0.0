package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;

import com.templatemela.smartpdfreader.interfaces.ExtractImagesListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PdfToImages {

    public static String mDecryptedPath;
    public static int mImagesCount = 0;
    public static ArrayList<String> mOutputFilePaths;
    public static PDFEncryptionUtility mPDFEncryptionUtility;


    public static void pdfToImageConverter(Context mContext, String[] mPassword, String mPath, Uri mUri, ExtractImagesListener mExtractImagesListener) {
        mPDFEncryptionUtility = new PDFEncryptionUtility((Activity) mContext);
        mExtractImagesListener.extractionStarted();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            ParcelFileDescriptor parcelFileDescriptor;
            Context context;
            String[] passArr = mPassword;
            if (passArr != null) {
                mDecryptedPath = mPDFEncryptionUtility.removeDefPasswordForImages(mPath, passArr);
            }
            mOutputFilePaths = new ArrayList<>();
            int i = 0;
            mImagesCount = 0;
            try {
                if (mDecryptedPath != null) {
                    parcelFileDescriptor = ParcelFileDescriptor.open(new File(mDecryptedPath), 268435456);
                } else if (mUri == null || (context = mContext) == null) {
                    parcelFileDescriptor = mPath != null ? ParcelFileDescriptor.open(new File(mPath), 268435456) : null;
                } else {
                    parcelFileDescriptor = context.getContentResolver().openFileDescriptor(mUri, "r");
                }
                if (parcelFileDescriptor != null) {
                    PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                    int pageCount = pdfRenderer.getPageCount();
                    while (i < pageCount) {
                        PdfRenderer.Page openPage = pdfRenderer.openPage(i);
                        Bitmap createBitmap = Bitmap.createBitmap(openPage.getWidth(), openPage.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(createBitmap);
                        canvas.drawColor(-1);
                        canvas.drawBitmap(createBitmap, 0.0f, 0.0f, null);
                        openPage.render(createBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                        openPage.close();
                        StringBuilder sb = new StringBuilder();
                        sb.append(FileUtils.getFileNameWithoutExtension(mPath));
                        sb.append("_");
                        i++;
                        sb.append(i);
                        String saveImage = FileUtils.saveImage(sb.toString(), createBitmap);
                        if (saveImage != null) {
                            mOutputFilePaths.add(saveImage);
                            mImagesCount++;
                        }
                    }
                    pdfRenderer.close();
                }
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                //UI Thread work here
                mExtractImagesListener.updateView(mImagesCount, mOutputFilePaths);
                if (mDecryptedPath != null) {
                    new File(mDecryptedPath).delete();
                }
            });
        });
    }

}
