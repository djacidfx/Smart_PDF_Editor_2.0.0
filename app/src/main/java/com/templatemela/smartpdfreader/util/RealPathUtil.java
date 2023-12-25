package com.templatemela.smartpdfreader.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

public class RealPathUtil {

    private static class SingletonHolder {
        static final RealPathUtil INSTANCE = new RealPathUtil();

        private SingletonHolder() {
        }
    }

    public static RealPathUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public String getRealPath(Context context, Uri uri) {
        return getRealPathFromURI_API19New(context, uri);
    }

    public String getRealPathFromURI_API19New(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                try {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                    return getDataColumn(context, contentUri, null, null);

                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
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
                else if ("document".equals(type)){
                   //  contentUri= MediaStore.Files.

                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
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

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            System.out.println("getPath() uri: " + uri.toString());
            System.out.println("getPath() uri authority: " + uri.getAuthority());
            System.out.println("getPath() uri path: " + uri.getPath());

            // ExternalStorageProvider
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                System.out.println("getPath() docId: " + docId + ", split: " + split.length + ", type: " + type);

                // This is for checking Main Memory
                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1] + "/";
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                    // This is for checking SD Card
                } else {
                    return "storage" + "/" + docId.replace(":", "/");
                }

            }
        }
        return null;
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private String getRealPathFromURI_API19(Context context, Uri uri) {
        String path;
        if (isDriveFile(uri) || !DocumentsContract.isDocumentUri(context, uri)) {
            return null;
        }
        if (isExternalStorageDocument(uri)) {
            String documentId = DocumentsContract.getDocumentId(uri);
            String[] split = documentId.split(":");
            if (!"primary".equalsIgnoreCase(split[0])) {
                path = "storage/" + documentId.replace(":", Constants.PATH_SEPERATOR);
            } else if (split.length > 1) {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + Constants.PATH_SEPERATOR + split[1];
            } else {
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + Constants.PATH_SEPERATOR;
            }
            return path;
        } else if (isRawDownloadsDocument(uri)) {
            return getDownloadsDocumentPath(context, uri, true);
        } else {
            if (isDownloadsDocument(uri)) {
                return getDownloadsDocumentPath(context, uri, false);
            }
            return null;
        }
    }

    private String getDownloadsDocumentPath(Context context, Uri uri, boolean isSub) {
        String name;
        String filePath = getFilePath(context, uri);
        if (isSub) {
            name = getSubFolders(uri);
        } else {
            name = "";
        }
        if (filePath == null) {
            String documentId = DocumentsContract.getDocumentId(uri);
            if (TextUtils.isEmpty(documentId)) {
                return null;
            }
            String replaceFirst = documentId.startsWith("raw:") ? documentId.replaceFirst("raw:", "") : null;
            try {
                return getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(documentId)), null, null);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return replaceFirst;
            }
        } else if (name != null) {
            return Environment.getExternalStorageDirectory().toString() + "/Download/" + name + filePath;
        } else {
            return Environment.getExternalStorageDirectory().toString() + "/Download/" + filePath;
        }
    }

    private String getSubFolders(Uri uri) {
        String[] split = String.valueOf(uri).replace("%2F", Constants.PATH_SEPERATOR).replace("%20", " ").replace("%3A", ":").split(Constants.PATH_SEPERATOR);
        String s1 = split[split.length - 2];
        String s2 = split[split.length - 3];
        String s3 = split[split.length - 4];
        String s4 = split[split.length - 5];
        if (split[split.length - 6].equals("Download")) {
            return s4 + Constants.PATH_SEPERATOR + s3 + Constants.PATH_SEPERATOR + s2 + Constants.PATH_SEPERATOR + s1 + Constants.PATH_SEPERATOR;
        } else if (s4.equals("Download")) {
            return s3 + Constants.PATH_SEPERATOR + s2 + Constants.PATH_SEPERATOR + s1 + Constants.PATH_SEPERATOR;
        } else if (s3.equals("Download")) {
            return s2 + Constants.PATH_SEPERATOR + s1 + Constants.PATH_SEPERATOR;
        } else if (!s2.equals("Download")) {
            return null;
        } else {
            return s1 + Constants.PATH_SEPERATOR;
        }
    }

    private String getFilePath(Context context, Uri uri) {
        Cursor query = context.getContentResolver().query(uri, new String[]{"_display_name"}, null, null, null);
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    String string = query.getString(query.getColumnIndexOrThrow("_display_name"));
                    query.close();
                    return string;
                }
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        }
        if (query == null) {
            return null;
        }
        query.close();
        return null;
    }

    private String getDataColumn(Context context, Uri uri, String docId, String[] selection) {
        Cursor query;
        String data = null;
        try {
            query = context.getContentResolver().query(uri, new String[]{"_data"}, docId, selection, null);
            if (query != null) {
                if (query.moveToFirst()) {
                    data = query.getString(query.getColumnIndexOrThrow("_data"));
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception e) {
            Log.e("Error", " " + e.getMessage());
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        return data;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDriveFile(Uri uri) {
        if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
            return true;
        }
        return "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isRawDownloadsDocument(Uri uri) {
        return String.valueOf(uri).contains("com.android.providers.downloads.documents/document/raw");
    }
}
