package com.templatemela.smartpdfreader.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.itextpdf.text.Annotation;

public class FileUriUtils {
    private final String mEXTERNALSTORAGEDOC;
    private final String mISDOWNLOADDOC;
    private final String mISGOOGLEPHOTODOC;
    private final String mISMEDIADOC;

    private FileUriUtils() {
        this.mEXTERNALSTORAGEDOC = "com.android.externalstorage.documents";
        this.mISDOWNLOADDOC = "com.android.providers.downloads.documents";
        this.mISMEDIADOC = "com.android.providers.media.documents";
        this.mISGOOGLEPHOTODOC = "com.google.android.apps.photos.content";
    }

    private static class SingletonHolder {
        static final FileUriUtils INSTANCE = new FileUriUtils();

        private SingletonHolder() {
        }
    }

    public static FileUriUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }


    public boolean isWhatsappImage(String name) {
        return "com.whatsapp.provider.media".equals(name);
    }

    private boolean checkURIAuthority(Uri uri, String s) {
        return s.equals(uri.getAuthority());
    }

    private boolean checkURI(Uri uri, String name) {
        return (uri == null || uri.getScheme() == null || !uri.getScheme().equalsIgnoreCase(name)) ? false : true;
    }

    private boolean isDocumentUri(Context context, Uri uri) {
        if (context == null || uri == null) {
            return false;
        }
        return DocumentsContract.isDocumentUri(context, uri);
    }

    private String getURIForMediaDoc(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isLocalStorageDocument(Uri uri) {
        return ("com.ianhanniballake.localstorage.documents").equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private String getURIForDownloadDoc(ContentResolver contentResolver, Uri uri) {
        return getImageRealPath(contentResolver, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(DocumentsContract.getDocumentId(uri))), null);
    }

    private String getURIForExternalstorageDoc(Uri uri) {
        String[] split = DocumentsContract.getDocumentId(uri).split(":");
        if (split.length != 2) {
            return null;
        }
        String s = split[0];
        String s1 = split[1];
        if (!"primary".equalsIgnoreCase(s)) {
            return null;
        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + Constants.PATH_SEPERATOR + s1;
    }

    private String getUriForDocumentUri(Context context, ContentResolver contentResolver, Uri uri) {
        if (checkURIAuthority(uri, "com.android.providers.media.documents")) {
            return getURIForMediaDoc(context, uri);
        }
        if (checkURIAuthority(uri, "com.android.providers.downloads.documents")) {
            return getURIForDownloadDoc(contentResolver, uri);
        }
        if (checkURIAuthority(uri, "com.android.externalstorage.documents")) {
            return getURIForExternalstorageDoc(uri);
        }
        return null;
    }


    public String getUriRealPathAboveKitkat(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (checkURI(uri, Annotation.CONTENT)) {
            if (checkURIAuthority(uri, "com.google.android.apps.photos.content")) {
                return uri.getLastPathSegment();
            }
            return getImageRealPath(contentResolver, uri, (String) null);
        } else if (checkURI(uri, Annotation.FILE)) {
            return uri.getPath();
        } else {
            if (isDocumentUri(context, uri)) {
                return getUriForDocumentUri(context,contentResolver, uri);
            }
            return null;
        }
    }

    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String name) {
        int columnIndex;
        Cursor query = contentResolver.query(uri, null, name, null, null);
        if (query == null || !query.moveToFirst() || (columnIndex = query.getColumnIndex("_data")) == -1) {
            return "";
        }
        String string = query.getString(columnIndex);
        query.close();
        return string;
    }

    public String getFilePath(Uri uri) {
        String path = uri.getPath();
        if (path == null) {
            return null;
        }
        return path.replace("/document/raw:", "");
    }
}
