package com.templatemela.smartpdfreader.util;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStream;
import com.templatemela.smartpdfreader.interfaces.ExtractImagesListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExtractImages {

    public static int mImagesCount = 0;
    public static ArrayList<String> mOutputFilePaths = new ArrayList<>();

    public static void extractImagesFromPdf(String mPath, ExtractImagesListener mExtractImagesListener) {
        mExtractImagesListener.extractionStarted();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            mOutputFilePaths = new ArrayList<>();
            mImagesCount = 0;
            try {
                PdfReader reader;
                reader = new PdfReader(mPath);
                for (int i = 0; i < reader.getXrefSize(); i++) {
                    PdfObject pdfobj = reader.getPdfObject(i);
                    if (pdfobj == null || !pdfobj.isStream()) {
                        continue;
                    }
                    PdfStream stream = (PdfStream) pdfobj;
                    PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);
                    if (pdfsubtype != null && pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
                        byte[] imageAsBytes = PdfReader.getStreamBytesRaw((PRStream) stream);
                        String saveImage = FileUtils.saveImage(FileUtils.getFileNameWithoutExtension(mPath) + "_" + (mImagesCount + 1), BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
                        if (saveImage != null) {
                            mOutputFilePaths.add(saveImage);
                            mImagesCount++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            handler.post(() -> {
                //UI Thread work here
                mExtractImagesListener.updateView(mImagesCount, mOutputFilePaths);
            });
        });
    }

}
