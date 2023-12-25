package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.widget.TextView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.OnPDFCompressedInterface;
import com.templatemela.smartpdfreader.interfaces.OnPdfReorderedInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PDFUtils {
    private final Activity mContext;
    private final FileUtils mFileUtils;

    public PDFUtils(Activity activity) {
        mContext = activity;
        mFileUtils = new FileUtils(activity);
    }

    public void showDetails(File file) {
        String name = file.getName();
        String path = file.getPath();
        String formattedSize = FileUtils.getFormattedSize(file);
        String formattedDate = FileUtils.getFormattedDate(file);
        TextView textView = new TextView(mContext);
        TextView textView2 = new TextView(mContext);
        textView.setText(String.format(mContext.getResources().getString(R.string.file_info), name, path, formattedSize, formattedDate));
        textView.setTextIsSelectable(true);
        textView2.setText(R.string.details);
        textView2.setPadding(20, 10, 10, 10);
        textView2.setTextSize(30.0f);
        textView2.setTextColor(mContext.getResources().getColor(R.color.black));
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog create = builder.create();
        builder.setView(textView);
        builder.setCustomTitle(textView2);
        builder.setPositiveButton(this.mContext.getResources().getString(R.string.ok), (dialogInterface, i) -> create.dismiss());
        builder.create();
        builder.show();
    }

    public boolean isPDFEncrypted(String path) {
        try {
//            Log.e("DDDDDDDD ",path);
            PdfReader pdfReader = new PdfReader("file://"+path);
            boolean isEncy = pdfReader.isEncrypted();
            pdfReader.close();
            return isEncy;
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public void compressPDF(String path, String outPath, int quality, OnPDFCompressedInterface onPDFCompressedInterface) {
        compressPdfAsync(path, outPath, quality, onPDFCompressedInterface);
    }

    boolean success = false;

    private void compressPdfAsync(String inputPath, String outputPath, int quality, OnPDFCompressedInterface mPDFCompressedInterface) {
        mPDFCompressedInterface.pdfCompressionStarted();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            try {
                PdfReader pdfReader = new PdfReader(inputPath);
                compressReader(pdfReader, quality);
                saveReader(pdfReader, outputPath);
                pdfReader.close();
                success = true;

            } catch (DocumentException | IOException e) {
                e.printStackTrace();
                success = false;
            }
            handler.post(() -> mPDFCompressedInterface.pdfCompressionEnded(outputPath, success));
        });
    }

    private void compressReader(PdfReader pdfReader, int quality) throws IOException {
        int xrefSize = pdfReader.getXrefSize();
        for (int i = 0; i < xrefSize; i++) {
            PdfObject pdfObject = pdfReader.getPdfObject(i);
            if (pdfObject != null && pdfObject.isStream()) {
                compressStream((PRStream) pdfObject, quality);
            }
        }
        pdfReader.removeUnusedObjects();
    }

    private void compressStream(PRStream pRStream, int quality) throws IOException {
        PdfObject pdfObject = pRStream.get(PdfName.SUBTYPE);
        System.out.println(pRStream.type());
        if (pdfObject != null && pdfObject.toString().equals(PdfName.IMAGE.toString())) {
            byte[] imageAsBytes = new PdfImageObject(pRStream).getImageAsBytes();
            Bitmap decodeByteArray = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            if (decodeByteArray != null) {
                int width = decodeByteArray.getWidth();
                int height = decodeByteArray.getHeight();
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                new Canvas(createBitmap).drawBitmap(decodeByteArray, 0.0f, 0.0f, null);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                createBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                pRStream.clear();
                pRStream.setData(byteArrayOutputStream.toByteArray(), false, 9);
                pRStream.put(PdfName.TYPE, PdfName.XOBJECT);
                pRStream.put(PdfName.SUBTYPE, PdfName.IMAGE);
                pRStream.put(PdfName.FILTER, PdfName.DCTDECODE);
                pRStream.put(PdfName.WIDTH, new PdfNumber(width));
                pRStream.put(PdfName.HEIGHT, new PdfNumber(height));
                pRStream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                pRStream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
            }
        }
    }

    private void saveReader(PdfReader pdfReader, String outputPath) throws DocumentException, IOException {
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(outputPath));
        pdfStamper.setFullCompression();
        pdfStamper.close();
    }


    public boolean addImagesToPdf(String iPath, String oPath, ArrayList<String> arrayList) {
        try {
            PdfReader pdfReader = new PdfReader(iPath);
            Document document = new Document();
            PdfWriter instance = PdfWriter.getInstance(document, new FileOutputStream(oPath));
            document.open();
            initDoc(pdfReader, document, instance);
            appendImages(document, arrayList);
            document.close();
            StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated).setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(oPath, FileUtils.FileType.e_PDF)).show();
            new DatabaseHelper(mContext).insertRecord(oPath, this.mContext.getString(R.string.created));
            return true;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(mContext, R.string.remove_pages_error);
            return false;
        }
    }


    private void initDoc(PdfReader pdfReader, Document document, PdfWriter pdfWriter) {
        int numberOfPages = pdfReader.getNumberOfPages();
        PdfContentByte directContent = pdfWriter.getDirectContent();
        for (int i = 1; i <= numberOfPages; i++) {
            PdfImportedPage importedPage = pdfWriter.getImportedPage(pdfReader, i);
            document.newPage();
            directContent.addTemplate(importedPage, 0.0f, 0.0f);
        }
    }

    private void appendImages(Document document, ArrayList<String> arrayList) throws DocumentException, IOException {
        Rectangle pageSize = document.getPageSize();
        for (int i = 0; i < arrayList.size(); i++) {
            document.newPage();
            Image instance = Image.getInstance(arrayList.get(i));
            instance.setBorder(0);
            instance.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
            instance.setAbsolutePosition((pageSize.getWidth() - instance.getScaledWidth()) / 2.0f, (pageSize.getHeight() - instance.getScaledHeight()) / 2.0f);
            document.add(instance);
        }
    }

    public boolean reorderRemovePDF(String path, String os, String ranges) {
        try {
            PdfReader pdfReader = new PdfReader(path);
            pdfReader.selectPages(ranges);
            if (pdfReader.getNumberOfPages() == 0) {
                StringUtils.getInstance().showSnackbar(mContext, R.string.remove_pages_error);
                return false;
            }
            new PdfStamper(pdfReader, new FileOutputStream(os)).close();
            StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(os, FileUtils.FileType.e_PDF)).show();
            new DatabaseHelper(mContext).insertRecord(os, this.mContext.getString(R.string.created));
            return true;
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(mContext, R.string.remove_pages_error);
            return false;
        }
    }


    public void reorderPdfPages(Uri uri, String path, OnPdfReorderedInterface onPdfReorderedInterface) {
        reorderPdfPage(uri, path, mContext, onPdfReorderedInterface);
    }

    private void reorderPdfPage(Uri mUri, String mPath, Activity mActivity, OnPdfReorderedInterface mOnPdfReorderedInterface) {
        mOnPdfReorderedInterface.onPdfReorderStarted();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            //Background work here
            ParcelFileDescriptor parcelFileDescriptor;
            ArrayList<Bitmap> arrayList = new ArrayList<>();
            try {
                if (mUri != null) {
                    parcelFileDescriptor = mActivity.getContentResolver().openFileDescriptor(mUri, "r");
                } else {
                    parcelFileDescriptor = mPath != null ? ParcelFileDescriptor.open(new File(mPath), 268435456) : null;
                }
                if (parcelFileDescriptor == null) {
                    arrayList = new ArrayList<>();
                } else {
                    PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                    ArrayList<Bitmap> bitmaps = getBitmaps(pdfRenderer);
                    pdfRenderer.close();
                    arrayList = bitmaps;
                }
            } catch (IOException | IllegalArgumentException | OutOfMemoryError | SecurityException e) {
                e.printStackTrace();
                arrayList = new ArrayList<>();
            }

            ArrayList<Bitmap> finalArrayList = arrayList;
            handler.post(() -> {
                //UI Thread work here
                if (finalArrayList.isEmpty()) {
                    mOnPdfReorderedInterface.onPdfReorderFailed();
                } else {
                    mOnPdfReorderedInterface.onPdfReorderCompleted(finalArrayList);
                }
            });
        });
    }

    private ArrayList<Bitmap> getBitmaps(PdfRenderer pdfRenderer) {
        ArrayList<Bitmap> arrayList = new ArrayList<>();
        int pageCount = pdfRenderer.getPageCount();
        for (int i = 0; i < pageCount; i++) {
            PdfRenderer.Page openPage = pdfRenderer.openPage(i);
            Bitmap createBitmap = Bitmap.createBitmap(openPage.getWidth(), openPage.getHeight(), Bitmap.Config.ARGB_8888);
            openPage.render(createBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            arrayList.add(createBitmap);
            openPage.close();
        }
        return arrayList;
    }

}
