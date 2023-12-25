package com.templatemela.smartpdfreader.util;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.List;

public class AppUtils {
    public static final List<String> storagePermissionList = Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    public static final String REGULAR_FONT = "fonts/heebo_regular.ttf";
    public static final String BOLD_FONT = "fonts/heebo_bold.ttf";



    public static int BORDER_WIDTH_DP = 5;
    public static String FACE = "com.facebook.katana";
    public static String GMAIL = "com.google.android.gm";
    public static String INSTA = "com.instagram.android";
    public static String MESSEGER = "com.facebook.orca";
    public static String TWITTER = "com.twitter.android";
    public static String WHATSAPP = "com.whatsapp";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static String PREFS_NAME = "theme_prefs";
    public static String KEY_THEME = "prefs.theme";
    public static  int THEME_UNDEFINED = -1;
    public static  int THEME_LIGHT = 0;
    public static  int THEME_DARK = 1;


    public static int getWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public static void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Bitmap cropBitmapWithMask(Bitmap bitmap, Bitmap bitmap2, int i, int i2) {
        if (!(bitmap == null || bitmap2 == null)) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > 0 && height > 0) {
                Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                Paint paint = new Paint(1);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                canvas.drawBitmap(bitmap2, (float) i, (float) i2, paint);
                paint.setXfermode((Xfermode) null);
                return createBitmap;
            }
        }
        return null;
    }

    public static Bitmap tfResizeBilinear(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return null;
        }
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap).drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), new Rect(0, 0, i, i2), null);
        return createBitmap;
    }

    public static boolean stringIsNotEmpty(String str) {
        return str != null && !str.equals("null") && !str.trim().equals("");
    }

    public static Uri addImageToGallery(Context mContext, String filePath) {
        ContentValues values = new ContentValues();
        values.put("datetaken", Long.valueOf(System.currentTimeMillis()));
        values.put("mime_type", "image/jpeg");
        values.put("_data", filePath);
        return mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static void notifyMediaScannerService(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path}, new String[]{"image/jpeg"}, null);
    }

    public static void changeMenuFont(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    changeMenuFont(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), BOLD_FONT));

            }
        } catch (Exception e) {
        }
    }

    public static void changeTabsFont(Context context, TabLayout tabLayout) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(context.getAssets(), REGULAR_FONT));
                    ((TextView) tabViewChild).setTextSize(12);
                    ((TextView) tabViewChild).setAllCaps(false);
                }
            }
        }
    }

    public static int dpToPx(Context context, int px) {
        return (int) (((float) px) * context.getResources().getDisplayMetrics().density);
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int i) {
        Matrix matrix = new Matrix();
        switch (i) {
            case 1:
                return bitmap;
            case 2:
                matrix.setScale(-1.0f, 1.0f);
                break;
            case 3:
                matrix.setRotate(180.0f);
                break;
            case 4:
                matrix.setRotate(180.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 5:
                matrix.setRotate(90.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 6:
                matrix.setRotate(90.0f);
                break;
            case 7:
                matrix.setRotate(-90.0f);
                matrix.postScale(-1.0f, 1.0f);
                break;
            case 8:
                matrix.setRotate(-90.0f);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            bitmap.recycle();
            return createBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}

