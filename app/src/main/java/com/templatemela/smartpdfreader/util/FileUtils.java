package com.templatemela.smartpdfreader.util;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.print.PrintManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.artifex.mupdf.viewer.DocumentActivity;
import com.itextpdf.text.Annotation;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.database.DatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileUtils {
    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public enum FileType {
        e_PDF,
        e_TXT
    }

    public FileUtils(Activity activity) {
        this.mContext = activity;
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public static String getFormattedDate(File file) {
        String[] split = new Date(file.lastModified()).toString().split(" ");
        String[] split2 = split[3].split(":");
        return split[0] + ", " + split[1] + " " + split[2] + " at " + (split2[0] + ":" + split2[1]);
    }

    public static String getFormattedSize(File file) {
        return readableFileSize(file.length());
    }

    public static String readableFileSize(long j) {
        if (j <= 0) {
            return "0";
        }
        double d = (double) j;
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        return new DecimalFormat("#,##0.#").format(d / Math.pow(1024.0d, (double) log10)) + " " + new String[]{"B", "kB", "MB", "GB", "TB"}[log10];
    }

    public void printFile(File file) {
        PrintDocumentAdapterHelper printDocumentAdapterHelper = new PrintDocumentAdapterHelper(file);
        PrintManager printManager = (PrintManager) mContext.getSystemService("print");
        String name = this.mContext.getString(R.string.app_name) + " Document";
        if (printManager != null) {
            printManager.print(name, printDocumentAdapterHelper, null);
            new DatabaseHelper(mContext).insertRecord(file.getAbsolutePath(), this.mContext.getString(R.string.printed));
        }
    }

    public void shareFile(File file) {
        Uri uriForFile = FileProvider.getUriForFile(mContext, Constants.AUTHORITY_APP, file);
        ArrayList arrayList = new ArrayList();
        arrayList.add(uriForFile);
        shareFile((ArrayList<Uri>) arrayList);
    }

    public void shareMultipleFiles(List<File> list) {
        ArrayList arrayList = new ArrayList();
        for (File uriForFile : list) {
            arrayList.add(FileProvider.getUriForFile(mContext, Constants.AUTHORITY_APP, uriForFile));
        }
        shareFile((ArrayList<Uri>) arrayList);
    }

    private void shareFile(ArrayList<Uri> arrayList) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND_MULTIPLE");
        intent.putExtra("android.intent.extra.TEXT", this.mContext.getString(R.string.i_have_attached_pdfs_to_this_message));
        intent.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
        intent.addFlags(1);
        intent.setType(this.mContext.getString(R.string.pdf_type));
        Activity activity = this.mContext;
        activity.startActivity(Intent.createChooser(intent, activity.getResources().getString(R.string.share_chooser)));
    }

    public void openFile(String path, FileType fileType) {
        Activity activity;
        int i;
        if (path == null) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.error_path_not_found);
            return;
        }
        if (fileType == FileType.e_PDF) {
            activity = mContext;
            i = R.string.pdf_type;
            File file = new File(path);
            Intent intent;
            Intent intent1 = new Intent(mContext, DocumentActivity.class);
            intent1.setAction("android.intent.action.VIEW");
            intent1.putExtra("in_app", true);
            intent1.setData(Uri.fromFile(file));
            mContext.startActivity(intent1);
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName(), file);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    mContext.startActivity(intent);
                }
                catch (Exception e){
                    Toast.makeText(mContext, "Something want wrong! ", Toast.LENGTH_SHORT).show();
                }

            }
            else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(file.getPath()), "application/pdf");
                intent = Intent.createChooser(intent, "Open File");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }*/
        } else {
            activity = mContext;
            i = R.string.txt_type;
            openFileInternal(path, activity.getString(i));
        }

    }

    private void openFileInternal(String path, String type) {
        File file = new File(path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(FLAG_ACTIVITY_NO_HISTORY);
        try {
            intent.setDataAndType(FileProvider.getUriForFile(mContext, Constants.AUTHORITY_APP, file), type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openIntent(Intent.createChooser(intent, mContext.getString(R.string.open_file)));
        } catch (Exception e) {
            e.printStackTrace();
            StringUtils.getInstance().showSnackbar(mContext,  R.string.error_open_file);
        }
    }

    private int checkRepeat(String name, List<File> list) {
        boolean b = true;
        int i = 0;
        while (b) {
            i++;
            String string = this.mContext.getString(R.string.pdf_ext);
            b = list.contains(new File(name.replace(string, i + this.mContext.getString(R.string.pdf_ext))));
        }
        return i;
    }

    public String getUriRealPath(Uri uri) {
        if (uri == null || FileUriUtils.getInstance().isWhatsappImage(uri.getAuthority())) {
            return null;
        }
        return FileUriUtils.getInstance().getUriRealPathAboveKitkat(mContext, uri);
    }

    public boolean isFileExist(String name) {
        return new File(mSharedPreferences.getString(Constants.STORAGE_LOCATION, StringUtils.getInstance().getDefaultStorageLocation()) + name).exists();
    }

    public String getFileName(Uri uri) {
        Cursor query;
        String scheme = uri.getScheme();
        String name = null;
        if (scheme == null) {
            return null;
        }
        if (scheme.equals(Annotation.FILE)) {
            return uri.getLastPathSegment();
        }
        if (scheme.equals(Annotation.CONTENT) && (query = mContext.getContentResolver().query(uri, null, null, null, null)) != null) {
            if (query.getCount() != 0) {
                int columnIndexOrThrow = query.getColumnIndexOrThrow("_display_name");
                query.moveToFirst();
                name = query.getString(columnIndexOrThrow);
            }
            query.close();
        }
        return name;
    }

    public static String getFileName(String name) {
        int lastIndexOf;
        if (name != null && (lastIndexOf = name.lastIndexOf(Constants.PATH_SEPERATOR)) < name.length()) {
            return name.substring(lastIndexOf + 1);
        }
        return null;
    }

    public static String getFileNameWithoutExtension(String name) {
        return (name == null || name.lastIndexOf(Constants.PATH_SEPERATOR) == -1) ? name : name.substring(name.lastIndexOf(Constants.PATH_SEPERATOR) + 1).replace(Constants.pdfExtension, "");
    }

    public static String getFileDirectoryPath(String path) {
        return path.substring(0, path.lastIndexOf(Constants.PATH_SEPERATOR) + 1);
    }

    public String getLastFileName(ArrayList<String> arrayList) {
        if (arrayList.size() == 0) {
            return "";
        }
        String stripExtension = stripExtension(getFileNameWithoutExtension(arrayList.get(arrayList.size() - 1)));
        return stripExtension + this.mContext.getString(R.string.pdf_suffix);
    }

    public String stripExtension(String name) {
        if (name == null) {
            return null;
        }
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return name;
        }
        return name.substring(0, lastIndexOf);
    }

    public static String saveImage(String name, Bitmap bitmap) {
        if (bitmap == null || checkIfBitmapIsWhite(bitmap)) {
            return null;
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + Constants.pdfDirectory);
        String path = name + ".png" /*PictureMimeType.PNG*/;
        File file2 = new File(file, path);
        if (file2.exists()) {
            file2.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            Log.v("saving", path);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file + Constants.PATH_SEPERATOR + path;
    }

    private static boolean checkIfBitmapIsWhite(Bitmap bitmap) {
        if (bitmap == null) {
            return true;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (bitmap.getPixel(i, j) != -1) {
                    return false;
                }
            }
        }
        return true;
    }

    public void openImage(String path) {
        File file = new File(path);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(1073741824);
        intent.setDataAndType(FileProvider.getUriForFile(mContext, Constants.AUTHORITY_APP, file), "image/*");
        intent.addFlags(1);
        openIntent(Intent.createChooser(intent, this.mContext.getString(R.string.open_file)));
    }

    private void openIntent(Intent intent) {
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_no_pdf_app);
        }
    }

    public Intent getFileChooser() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.GET_CONTENT");
        intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory() + Constants.PATH_SEPERATOR), this.mContext.getString(R.string.pdf_type));
        return Intent.createChooser(intent, this.mContext.getString(R.string.merge_file_select));
    }

    public String getUniqueFileName(String name) {
        File parentFile;
        File[] listFiles;
        File file = new File(name);
        if (!isFileExist(file.getName()) || (parentFile = file.getParentFile()) == null || (listFiles = parentFile.listFiles()) == null) {
            return name;
        }
        int checkRepeat = checkRepeat(name, Arrays.asList(listFiles));
        String pdf = mContext.getString(R.string.pdf_ext);
        return name.replace(pdf, checkRepeat + this.mContext.getResources().getString(R.string.pdf_ext));
    }

    public static void makeAndClearTemp() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + Constants.pdfDirectory + Constants.tempDirectory);
        if (file.mkdir() && file.isDirectory()) {
            for (String file2 : file.list()) {
                new File(file, file2).delete();
            }
        }
    }
}
