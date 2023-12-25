package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextToPDFUtils {
    private final Activity mContext;
    private final DocFileReader mDocFileReader;
    private final DocxFileReader mDocxFileReader;
    private final SharedPreferences mSharedPreferences;
    private final TextFileReader mTextFileReader;

    public TextToPDFUtils(Activity activity) {
        this.mContext = activity;
        this.mTextFileReader = new TextFileReader(activity);
        this.mDocFileReader = new DocFileReader(activity);
        this.mDocxFileReader = new DocxFileReader(activity);
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void createPdfFromTextFile(TextToPDFOptions textToPDFOptions, String extension) throws DocumentException, IOException {
        String string = mSharedPreferences.getString(Constants.MASTER_PWD_STRING, Constants.appName);
        Rectangle rectangle = new Rectangle(PageSize.getRectangle(textToPDFOptions.getPageSize()));
        rectangle.setBackgroundColor(getBaseColor(textToPDFOptions.getPageColor()));
        Document document = new Document(rectangle);
        String ext = mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation()) + textToPDFOptions.getOutFileName() + Constants.pdfExtension;
        PdfWriter instance = PdfWriter.getInstance(document, new FileOutputStream(ext));
        instance.setPdfVersion(PdfWriter.VERSION_1_7);
        if (textToPDFOptions.isPasswordProtected()) {
            instance.setEncryption(textToPDFOptions.getPassword().getBytes(), string.getBytes(), 2068, 2);
        }
        document.open();
        Font font = new Font(textToPDFOptions.getFontFamily());
        font.setStyle(0);
        font.setSize((float) textToPDFOptions.getFontSize());
        font.setColor(getBaseColor(textToPDFOptions.getFontColor()));
        document.add(new Paragraph("\n"));
        addContentToDocument(textToPDFOptions, extension, document, font);
        document.close();
        new DatabaseHelper(mContext).insertRecord(ext, this.mContext.getString(R.string.created));
    }

    private void addContentToDocument(TextToPDFOptions textToPDFOptions, String extension, Document document, Font font) throws DocumentException {
        if (extension != null) {
            extension.hashCode();
            if (extension.equals(Constants.docExtension)) {
                mDocFileReader.read(textToPDFOptions.getInFileUri(), document, font);
            } else if (!extension.equals(Constants.docxExtension)) {
                mTextFileReader.read(textToPDFOptions.getInFileUri(), document, font);
            } else {
                mDocxFileReader.read(textToPDFOptions.getInFileUri(), document, font);
            }
        } else {
            throw new DocumentException();
        }
    }

    private BaseColor getBaseColor(int color) {
        return new BaseColor(Color.red(color), Color.green(color), Color.blue(color));
    }
}
