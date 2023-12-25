package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.activity.MainActivity;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipToPdf {
    private static final int BUFFER_SIZE = 4096;

    private static class SingletonHolder {
        static final ZipToPdf INSTANCE = new ZipToPdf();

        private SingletonHolder() {
        }
    }

    public static ZipToPdf getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void convertZipToPDF(String path, Activity activity) {
        ArrayList arrayList = new ArrayList();
        FileUtils.makeAndClearTemp();
        String name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + Constants.pdfDirectory + Constants.tempDirectory;
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(path)));
            int i = 0;
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry == null) {
                    break;
                }
                String lowerCase = nextEntry.getName().toLowerCase();
                if (nextEntry.isDirectory()) {
                    i++;
                } else if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".png")) {
                    String fPath = Constants.PATH_SEPERATOR + lowerCase;
                    int lastIndexOf = lowerCase.lastIndexOf(Constants.PATH_SEPERATOR);
                    if (lastIndexOf != -1) {
                        fPath = lowerCase.substring(lastIndexOf);
                    }
                    if (i != 0) {
                        fPath = fPath.replace(Constants.PATH_SEPERATOR, Constants.PATH_SEPERATOR + i + "- ");
                    }
                    File file = new File(name + fPath);
                    arrayList.add(Uri.fromFile(file));
                    byte[] bArr = new byte[BUFFER_SIZE];
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);
                    while (true) {
                        int read = zipInputStream.read(bArr, 0, BUFFER_SIZE);
                        if (read == -1) {
                            break;
                        }
                        bufferedOutputStream.write(bArr, 0, read);
                    }
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                }
            }
            zipInputStream.close();
            if (arrayList.size() == 0) {
                StringUtils.getInstance().showSnackbar(activity, (int) R.string.error_no_image_in_zip);
            } else {
                ((MainActivity) activity).convertImagesToPdf(arrayList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(activity, R.string.error_open_file);
        }
    }
}
