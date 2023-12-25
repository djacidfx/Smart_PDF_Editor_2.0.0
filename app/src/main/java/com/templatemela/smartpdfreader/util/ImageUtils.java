package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Rectangle;
import com.templatemela.smartpdfreader.R;

import java.io.File;

public class ImageUtils {
    public String mImageScaleType;

    private static class SingletonHolder {
        static final ImageUtils INSTANCE = new ImageUtils();

        private SingletonHolder() {
        }
    }

    public static ImageUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    static Rectangle calculateFitSize(float f, float f2, Rectangle rectangle) {
        float max = Math.max((f - rectangle.getWidth()) / f, (f2 - rectangle.getHeight()) / f2);
        return new Rectangle((float) Math.abs((int) (f - (f * max))), (float) Math.abs((int) (f2 - (max * f2))));
    }

    public Bitmap getRoundBitmap(Bitmap bitmap) {
        int min = Math.min(bitmap.getWidth(), bitmap.getHeight());
        if (!(bitmap.getWidth() == min && bitmap.getHeight() == min)) {
            float min2 = ((float) Math.min(bitmap.getWidth(), bitmap.getHeight())) / ((float) min);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (((float) bitmap.getWidth()) / min2), (int) (((float) bitmap.getHeight()) / min2), false);
        }
        Bitmap createBitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, min, min);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        float f = ((float) min) / 2.0f;
        float f2 = 0.7f + f;
        canvas.drawCircle(f2, f2, f + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    public Bitmap getRoundBitmapFromPath(String path) {
        File file = new File(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options);
        options.inJustDecodeBounds = false;
        Bitmap decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        if (decodeFile == null) {
            return null;
        }
        return getInstance().getRoundBitmap(decodeFile);
    }

    private int calculateInSampleSize(BitmapFactory.Options options) {
        int height = options.outHeight;
        int width = options.outWidth;
        int size = 1;
        if (height > 500 || width > 500) {
            int h = height / 2;
            int w = width / 2;
            while (h / size >= 500 && w / size >= 500) {
                size *= 2;
            }
        }
        return size;
    }

    public void showImageScaleTypeDialog(Context context, Boolean isDefault) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String type = defaultSharedPreferences.getString(Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT, Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO);
        MaterialDialog materialDialog = DialogUtils.getInstance().createCustomDialogWithoutContent((Activity) context, R.string.image_scale_type)
                .customView(R.layout.image_scale_type_dialog, true)
                .onPositive((materialDialog1, dialogAction) -> {
                    View customView = materialDialog1.getCustomView();
                    if (((RadioGroup) customView.findViewById(R.id.scale_type)).getCheckedRadioButtonId() == R.id.aspect_ratio) {
                        mImageScaleType = Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO;
                    } else {
                        mImageScaleType = Constants.IMAGE_SCALE_TYPE_STRETCH;
                    }
                    SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                    edit.putString(Constants.DEFAULT_IMAGE_SCALE_TYPE_TEXT, mImageScaleType);
                    edit.apply();
                }).build();
        if (type.equalsIgnoreCase(Constants.IMAGE_SCALE_TYPE_ASPECT_RATIO)) {
            ((RadioGroup) materialDialog.getCustomView().findViewById(R.id.scale_type)).check(R.id.aspect_ratio);
        } else {
            ((RadioGroup) materialDialog.getCustomView().findViewById(R.id.scale_type)).check(R.id.stretch);
        }
        if (isDefault.equals(true)) {
            materialDialog.getCustomView().findViewById(R.id.cbSetDefault).setVisibility(View.GONE);
        }
        materialDialog.show();
    }


    public Bitmap toGrayscale(Bitmap bitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return createBitmap;
    }
}
