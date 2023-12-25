package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.util.SparseIntArray;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import com.templatemela.smartpdfreader.interfaces.DataSetChanged;

import java.io.FileOutputStream;

public class PDFRotationUtils {
    private final SparseIntArray mAngleRadioButton;
    private final Activity mContext;
    private final FileUtils mFileUtils;

    public PDFRotationUtils(Activity activity) {
        mContext = activity;
        mFileUtils = new FileUtils(activity);
        mAngleRadioButton = new SparseIntArray();
        mAngleRadioButton.put(R.id.deg90, 90);
        mAngleRadioButton.put(R.id.deg180, 180);
        mAngleRadioButton.put(R.id.deg270, 270);
    }

    public void rotatePages(String path, DataSetChanged dataSetChanged) {
        DialogUtils.getInstance().createCustomDialogWithoutContent(mContext, R.string.rotate_pages).customView(R.layout.dialog_rotate_pdf, true).onPositive((materialDialog, dialogAction) -> setPageRotate(path, dataSetChanged, materialDialog)).show();
    }

    public void setPageRotate(String path, DataSetChanged dataSetChanged, MaterialDialog materialDialog) {
        int i = mAngleRadioButton.get(((RadioGroup) materialDialog.getCustomView().findViewById(R.id.rotation_angle)).getCheckedRadioButtonId());
        String fileDirectoryPath = FileUtils.getFileDirectoryPath(path);
        String fileName = FileUtils.getFileName(path);
        String finalName = fileDirectoryPath + String.format(this.mContext.getString(R.string.rotated_file_name), fileName.substring(0, fileName.lastIndexOf(46)), Integer.valueOf(i), this.mContext.getString(R.string.pdf_ext));
        if (rotatePDFPages(i, path, finalName, dataSetChanged)) {
            new DatabaseHelper(mContext).insertRecord(finalName, this.mContext.getString(R.string.rotated));
        }
    }

    private boolean rotatePDFPages(int i, String fileName, String os, DataSetChanged dataSetChanged) {
        try {
            PdfReader pdfReader = new PdfReader(fileName);
            int numberOfPages = pdfReader.getNumberOfPages();
            for (int j = 1; j <= numberOfPages; j++) {
                PdfDictionary pageN = pdfReader.getPageN(j);
                PdfNumber asNumber = pageN.getAsNumber(PdfName.ROTATE);
                if (asNumber == null) {
                    pageN.put(PdfName.ROTATE, new PdfNumber(i));
                } else {
                    pageN.put(PdfName.ROTATE, new PdfNumber((asNumber.intValue() + i) % 360));
                }
            }
            new PdfStamper(pdfReader, new FileOutputStream(os)).close();
            pdfReader.close();
            StringUtils.getInstance().getSnackbarwithAction(mContext, R.string.snackbar_pdfCreated)
                    .setAction(R.string.snackbar_viewAction, view -> mFileUtils.openFile(os, FileUtils.FileType.e_PDF)).show();
            dataSetChanged.updateDataset();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(mContext, R.string.encrypted_pdf);
            return false;
        }
    }

}
