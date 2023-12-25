package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.database.DatabaseHelper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.cli.HelpFormatter;

public class SplitPDFUtils {
    private static final int ERROR_INVALID_INPUT = 3;
    private static final int ERROR_PAGE_NUMBER = 1;
    private static final int ERROR_PAGE_RANGE = 2;
    private static final int NO_ERROR = 0;
    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public SplitPDFUtils(Activity activity) {
        this.mContext = activity;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public ArrayList<String> splitPDFByConfig(String path, String config) {
        String fileName = path;
        String replaceAll = config.replaceAll("\\s+", "");
        ArrayList<String> arrayList = new ArrayList<>();
        String[] split = replaceAll.split("[,]");
        Log.v("Ranges", Arrays.toString(split));
        if (fileName != null && isInputValid(fileName,split)) {
            try {
                String string = mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation());
                PdfReader pdfReader = new PdfReader(fileName);
                for (String s : split) {
                        String fName = string + FileUtils.getFileName(path);
                        if (pdfReader.getNumberOfPages() <= 1) {
                            StringUtils.getInstance().showSnackbar(mContext, R.string.split_one_page_pdf_alert);
                        } else if (!s.contains(HelpFormatter.DEFAULT_OPT_PREFIX)) {
                            int parseInt = Integer.parseInt(s);
                            Document document = new Document();
                            String replace = fName.replace(Constants.pdfExtension, "_" + parseInt + Constants.pdfExtension);
                            PdfCopy pdfCopy = new PdfCopy(document, new FileOutputStream(replace));
                            document.open();
                            pdfCopy.addPage(pdfCopy.getImportedPage(pdfReader, parseInt));
                            document.close();
                            arrayList.add(replace);
                            new DatabaseHelper(mContext).insertRecord(replace, this.mContext.getString(R.string.created));
                        } else {
                            int parseInt2 = Integer.parseInt(s.substring(0, s.indexOf(HelpFormatter.DEFAULT_OPT_PREFIX)));
                            int parseInt3 = Integer.parseInt(s.substring(s.indexOf(HelpFormatter.DEFAULT_OPT_PREFIX) + 1));
                            if (pdfReader.getNumberOfPages() == (parseInt3 - parseInt2) + 1) {
                                StringUtils.getInstance().showSnackbar(mContext, R.string.split_range_alert);
                            } else {
                                Document document2 = new Document();
                                String replace2 = fName.replace(Constants.pdfExtension, "_" + parseInt2 + HelpFormatter.DEFAULT_OPT_PREFIX + parseInt3 + Constants.pdfExtension);
                                PdfCopy pdfCopy2 = new PdfCopy(document2, new FileOutputStream(replace2));
                                document2.open();
                                while (parseInt2 <= parseInt3) {
                                    pdfCopy2.addPage(pdfCopy2.getImportedPage(pdfReader, parseInt2));
                                    parseInt2++;
                                }
                                document2.close();
                                new DatabaseHelper(mContext).insertRecord(replace2, this.mContext.getString(R.string.created));
                                arrayList.add(replace2);
                            }
                        }
                    }
            } catch (DocumentException | IOException | IllegalArgumentException e) {
                e.printStackTrace();
                StringUtils.getInstance().showSnackbar(this.mContext, (int) R.string.file_access_error);
            }
        }
        return arrayList;
    }

    private boolean isInputValid(String fileName, String[] names) {
        try {
            if (new PdfReader(fileName).isEncrypted()){
                StringUtils.getInstance().showSnackbar(mContext, R.string.encrypted_pdf);
                return false;
            }
            int checkRangeValidity = checkRangeValidity(new PdfReader(fileName).getNumberOfPages(), names);
             if (checkRangeValidity == ERROR_PAGE_RANGE) {
                StringUtils.getInstance().showSnackbar(mContext, R.string.error_page_range);
                return false;
            } else if (checkRangeValidity != ERROR_INVALID_INPUT) {
                return true;
            } else {
                StringUtils.getInstance().showSnackbar(mContext, R.string.error_invalid_input);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int checkRangeValidity(int pages, String[] names) {
        if (names.length == 0) {
            return 3;
        }
        int length = names.length;
        int i2 = 0;
        while (i2 < length) {
            String str = names[i2];
            if (!str.contains(HelpFormatter.DEFAULT_OPT_PREFIX)) {
                try {
                    int parseInt = Integer.parseInt(str);
                    if (parseInt <= pages && parseInt != 0) {
                        i2++;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return 3;
                }
            } else {
                try {
                    int parseInt2 = Integer.parseInt(str.substring(0, str.indexOf(HelpFormatter.DEFAULT_OPT_PREFIX)));
                    int parseInt3 = Integer.parseInt(str.substring(str.indexOf(HelpFormatter.DEFAULT_OPT_PREFIX) + 1));
                    if (parseInt3 > pages  || parseInt2 > pages) {
                        return 2;
                    }
                   /* if (parseInt2 <= pages && parseInt3 <= pages && parseInt2 != 0 && parseInt3 != 0) {

                        i2++;
                    }*/
                } catch (NumberFormatException | StringIndexOutOfBoundsException e2) {
                    e2.printStackTrace();
                    return 3;
                }
            }
            return 1;
        }
        return 0;
    }
}
