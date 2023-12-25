package com.templatemela.smartpdfreader.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.itextpdf.text.xml.xmp.PdfSchema;
import com.templatemela.smartpdfreader.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class DirectoryUtils {
    private final Context mContext;
    private ArrayList<String> mFilePaths;
    private final SharedPreferences mSharedPreferences;

    public DirectoryUtils(Context context) {
        this.mContext = context;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public ArrayList<File> searchPDF(String query) {
        ArrayList<File> arrayList = new ArrayList<>();
        for (File next : searchPdfsFromPdfFolder(getOrCreatePdfDirectory().listFiles())) {
            String[] split = next.getPath().split(Constants.PATH_SEPERATOR);
            if (checkChar(query, split[split.length - 1].replace(PdfSchema.DEFAULT_XPATH_ID, "")) == 1) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    private int checkChar(String query, String spiltQuery) {
        String lowerCase = query.toLowerCase();
        String lowerCase2 = spiltQuery.toLowerCase();
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        for (char valueOf : lowerCase.toCharArray()) {
            hashSet.add(Character.valueOf(valueOf));
        }
        for (char valueOf2 : lowerCase2.toCharArray()) {
            hashSet2.add(Character.valueOf(valueOf2));
        }
        if (hashSet.containsAll(hashSet2) || hashSet2.containsAll(hashSet)) {
            return 1;
        }
        return 0;
    }


    public ArrayList<File> getPdfsFromPdfFolder(File[] fileList) {
        ArrayList<File> arrayList = new ArrayList<>();
        if (fileList == null) {
            return arrayList;
        }
        for (File file : fileList) {
            if (isPDFAndNotDirectory(file)) {
                arrayList.add(file);
            }
        }
        return arrayList;
    }

    private ArrayList<File> searchPdfsFromPdfFolder(File[] fileList) {
        ArrayList<File> pdfsFromPdfFolder = getPdfsFromPdfFolder(fileList);
        if (fileList == null) {
            return pdfsFromPdfFolder;
        }
        for (File file : fileList) {
            if (file.isDirectory()) {
                for (File file2 : file.listFiles()) {
                    if (isPDFAndNotDirectory(file2)) {
                        pdfsFromPdfFolder.add(file2);
                    }
                }
            }
        }
        return pdfsFromPdfFolder;
    }

    private boolean isPDFAndNotDirectory(File file) {
        return !file.isDirectory() && file.getName().endsWith(this.mContext.getString(R.string.pdf_ext));
    }

    public File getOrCreatePdfDirectory() {
        File file = new File(mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation()));
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public ArrayList<File> getPdfFromOtherDirectories() {
        mFilePaths = new ArrayList<>();
        walkDir(getOrCreatePdfDirectory(), false);
        ArrayList<File> arrayList = new ArrayList<>();
        Iterator<String> it2 = mFilePaths.iterator();
        while (it2.hasNext()) {
            arrayList.add(new File(it2.next()));
        }
        return arrayList;
    }

    public ArrayList<String> getPdfList(){
        mFilePaths = new ArrayList<>();
        walkDir(getOrCreatePdfDirectory(), false);
        ArrayList<String> arrayList = new ArrayList<>();
        Iterator<String> it2 = mFilePaths.iterator();
        while (it2.hasNext()) {
            arrayList.add(it2.next());
        }
        return arrayList;
    }


    public ArrayList<String> getAllPDFsOnDevice(boolean isTextAndPdf) {
        mFilePaths = new ArrayList<>();
        walkDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), isTextAndPdf);
        return mFilePaths;
    }

    private void walkDir(File file, boolean isTextAndPdf) {
        if (isTextAndPdf) {
            walkDir(file, Arrays.asList(Constants.pdfExtension, Constants.textExtension));
        } else {
            walkDir(file, Collections.singletonList(Constants.pdfExtension));
        }
    }

    private void walkDir(File file, List<String> list) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    walkDir(file2, list);
                } else {
                    for (String endsWith : list) {
                        if (file2.getName().endsWith(endsWith)) {
                            mFilePaths.add(file2.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }


    public ArrayList<String> getAllExcelDocumentsOnDevice() {
        mFilePaths = new ArrayList<>();
        walkDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                Arrays.asList(Constants.excelExtension, Constants.excelWorkbookExtension));
        return mFilePaths;
    }
}
