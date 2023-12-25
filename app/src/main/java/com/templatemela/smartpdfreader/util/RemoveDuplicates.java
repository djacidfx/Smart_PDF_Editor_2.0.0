package com.templatemela.smartpdfreader.util;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.templatemela.smartpdfreader.interfaces.OnPDFCreatedInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoveDuplicates {
    public static final ArrayList<Bitmap> mBitmaps = new ArrayList<>();
    public static Boolean mIsNewPDFCreated;
    public static String mPath;
    public static final StringBuilder mSequence = new StringBuilder();

    public static void removeDuplicate(String path, OnPDFCreatedInterface mOnPDFCreatedInterface) {
        mPath = path;
        mOnPDFCreatedInterface.onPDFCreationStarted();
        mIsNewPDFCreated = false;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            try {
                ParcelFileDescriptor open = mPath != null ? ParcelFileDescriptor.open(new File(mPath), 268435456) : null;
                if (open != null) {
                    PdfRenderer pdfRenderer = new PdfRenderer(open);
                    int pageCount = pdfRenderer.getPageCount();
                    for (int i = 0; i < pageCount; i++) {
                        PdfRenderer.Page openPage = pdfRenderer.openPage(i);
                        Bitmap createBitmap = Bitmap.createBitmap(openPage.getWidth(), openPage.getHeight(), Bitmap.Config.ARGB_8888);
                        openPage.render(createBitmap, null, null, 1);
                        openPage.close();
                        checkAndAddIfBitmapExists(createBitmap, i);
                    }
                    pdfRenderer.close();
                    if (mBitmaps.size() != pageCount) {
//                        return;
//                    }
                        String sb = mSequence.toString();

                        StringBuffer newSb = new StringBuffer(sb);
                        newSb.deleteCharAt(sb.length() - 1);
//                        System.out.println("SSSSSSSSSSS " + newSb);

                        String replace = mPath.replace(".advanced", "_edited_" + sb + ".advanced");
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + Constants.pdfDirectory);
                        String newPath = file + File.separator + FileUtils.getFileNameWithoutExtension(path) + "_edited_" + newSb + ".pdf";
//                        Log.e("newPath ", newPath);

                        if (createPDF(mPath, newPath, newSb.toString())) {
                            mPath = newPath;
                            mIsNewPDFCreated = true;
                        }
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


    public static void checkAndAddIfBitmapExists(Bitmap bitmap, int i) {
        Iterator<Bitmap> it2 = mBitmaps.iterator();
        boolean isExists = true;
        while (it2.hasNext()) {
            if (it2.next().sameAs(bitmap)) {
                isExists = false;
            }
        }
        if (isExists) {
            mBitmaps.add(bitmap);
            StringBuilder sb = mSequence;
            sb.append(i + 1);
            sb.append("-");
        }
    }


    public static boolean createPDF(String path, String os, String ranges) {
        try {
            PdfReader pdfReader = new PdfReader(path);
            pdfReader.selectPages(ranges);
            PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(os));
            pdfStamper.close();
            return true;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
