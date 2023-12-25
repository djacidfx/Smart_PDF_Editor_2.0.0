package com.templatemela.smartpdfreader.util;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfWriter;
import com.templatemela.smartpdfreader.interfaces.OnPDFCreatedInterface;
import com.templatemela.smartpdfreader.model.ImageToPDFOptions;
import com.templatemela.smartpdfreader.model.Watermark;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreatePdf {
    public static int mBorderWidth = 0;
    public static String mFileName = null;
    public static String mImageScaleType = null;
    public static ArrayList<String> mImagesUri = null;
    public static int mMarginBottom = 0;
    public static int mMarginLeft = 0;
    public static int mMarginRight = 0;
    public static int mMarginTop = 0;
    public static String mMasterPwd = null;
    public static OnPDFCreatedInterface mOnPDFCreatedInterface = null;
    public static int mPageColor = 0;
    public static String mPageNumStyle = null;
    public static String mPageSize = null;
    public static String mPassword = null;
    public static boolean mPasswordProtected = false;
    public static String mPath;
    public static String mQualityString = null;
    public static boolean mSuccess;
    public static Watermark mWatermark = null;
    public static Boolean mWatermarkAdded = null;

    public static void CreatePdfInitialize(ImageToPDFOptions imageToPDFOptions, String path, OnPDFCreatedInterface onPDFCreatedInterface) {
        mImagesUri = imageToPDFOptions.getImagesUri();
        mFileName = imageToPDFOptions.getOutFileName();
        mPassword = imageToPDFOptions.getPassword();
        mQualityString = imageToPDFOptions.getQualityString();
        mOnPDFCreatedInterface = onPDFCreatedInterface;
        mPageSize = imageToPDFOptions.getPageSize();
        mPasswordProtected = imageToPDFOptions.isPasswordProtected();
        mBorderWidth = imageToPDFOptions.getBorderWidth();
        mWatermarkAdded = imageToPDFOptions.isWatermarkAdded();
        mWatermark = imageToPDFOptions.getWatermark();
        mMarginTop = imageToPDFOptions.getMarginTop();
        mMarginBottom = imageToPDFOptions.getMarginBottom();
        mMarginRight = imageToPDFOptions.getMarginRight();
        mMarginLeft = imageToPDFOptions.getMarginLeft();
        mImageScaleType = imageToPDFOptions.getImageScaleType();
        mPageNumStyle = imageToPDFOptions.getPageNumStyle();
        mMasterPwd = imageToPDFOptions.getMasterPwd();
        mPageColor = imageToPDFOptions.getPageColor();
        mPath = path;
        createPdfFiles();
    }

    public static void createPdfFiles() {
        mSuccess = true;
        mOnPDFCreatedInterface.onPDFCreationStarted();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            setFilePath();
            Log.v("stage 1", "store the advanced in sd card");
            Rectangle rectangle = new Rectangle(PageSize.getRectangle(mPageSize));
            rectangle.setBackgroundColor(getBaseColor(mPageColor));
            Document document = new Document(rectangle, (float) mMarginLeft, (float) mMarginRight, (float) mMarginTop, (float) mMarginBottom);
            Log.v("stage 2", "Document Created");
            document.setMargins((float) mMarginLeft, (float) mMarginRight, (float) mMarginTop, (float) mMarginBottom);
            Rectangle pageSize = document.getPageSize();
            try {
                PdfWriter instance = PdfWriter.getInstance(document, new FileOutputStream(mPath));
                Log.v("Stage 3", "Pdf writer");
                if (mPasswordProtected) {
                    instance.setEncryption(mPassword.getBytes(), mMasterPwd.getBytes(), 2068, 2);
                    Log.v("Stage 3.1", "Set Encryption");
                }
                if (mWatermarkAdded) {
                    WatermarkPageEvent watermarkPageEvent = new WatermarkPageEvent();
                    watermarkPageEvent.setWatermark(mWatermark);
                    instance.setPageEvent(watermarkPageEvent);
                }
                document.open();
                Log.v("Stage 4", "Document opened");
                for (int i = 0; i < mImagesUri.size(); i++) {
                    int quality = 30;
                    if (StringUtils.getInstance().isNotEmpty(mQualityString)) {
                        quality = Integer.parseInt(mQualityString);
                    }
                    Image instance2 = Image.getInstance(mImagesUri.get(i));
                    double d = ((double) quality) * 0.09d;
                    instance2.setCompressionLevel((int) d);
                    instance2.setBorder(15);
                    instance2.setBorderWidth((float) mBorderWidth);
                    Log.v("Stage 5", "Image compressed " + d);
                    Log.v("Stage 6", "Image path adding");
                    float width = document.getPageSize().getWidth() - ((float) (mMarginLeft + mMarginRight));
                    float height = document.getPageSize().getHeight() - ((float) (mMarginBottom + mMarginTop));
                    if (mImageScaleType.equals(Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO)) {
                        instance2.scaleToFit(width, height);
                    } else {
                        instance2.scaleAbsolute(width, height);
                    }
                    instance2.setAbsolutePosition((pageSize.getWidth() - instance2.getScaledWidth()) / 2.0f, (pageSize.getHeight() - instance2.getScaledHeight()) / 2.0f);
                    Log.v("Stage 7", "Image Alignments");
                    addPageNumber(pageSize, instance);
                    document.add(instance2);
                    document.newPage();
                }
                Log.v("Stage 8", "Image adding");
                document.close();
                Log.v("Stage 7", "Document Closed" + mPath);
                Log.v("Stage 8", "Record inserted in database");
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


    public static void setFilePath() {
        File file = new File(mPath);
        if (!file.exists()) {
            file.mkdir();
        }
        mPath += mFileName + Constants.pdfExtension;
    }

    public static void addPageNumber(Rectangle rectangle, PdfWriter pdfWriter) {
        if (mPageNumStyle != null) {
            ColumnText.showTextAligned(pdfWriter.getDirectContent(), 6, getPhrase(pdfWriter, mPageNumStyle, mImagesUri.size()), (rectangle.getRight() + rectangle.getLeft()) / 2.0f, rectangle.getBottom() + 25.0f, 0.0f);
        }
    }

    public static Phrase getPhrase(PdfWriter pdfWriter, String pageStyle, int size) {
        pageStyle.hashCode();
        if (pageStyle.equals(Constants.PG_NUM_STYLE_PAGE_X_OF_N)) {
            return new Phrase(String.format("Page %d of %d", new Object[]{Integer.valueOf(pdfWriter.getPageNumber()), Integer.valueOf(size)}));
        } else if (!pageStyle.equals(Constants.PG_NUM_STYLE_X_OF_N)) {
            return new Phrase(String.format("%d", new Object[]{Integer.valueOf(pdfWriter.getPageNumber())}));
        } else {
            return new Phrase(String.format("%d of %d", new Object[]{Integer.valueOf(pdfWriter.getPageNumber()), Integer.valueOf(size)}));
        }
    }

    public static BaseColor getBaseColor(int color) {
        return new BaseColor(Color.red(color), Color.green(color), Color.blue(color));
    }
}
