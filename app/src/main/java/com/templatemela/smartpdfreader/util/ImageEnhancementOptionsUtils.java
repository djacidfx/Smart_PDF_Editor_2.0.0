package com.templatemela.smartpdfreader.util;

import android.content.Context;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.model.ImageToPDFOptions;

import java.util.ArrayList;

public class ImageEnhancementOptionsUtils {

    private static class SingletonHolder {

        public static final ImageEnhancementOptionsUtils INSTANCE = new ImageEnhancementOptionsUtils();

        private SingletonHolder() {
        }
    }

    public static ImageEnhancementOptionsUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context, ImageToPDFOptions imageToPDFOptions) {
        ArrayList<EnhancementOptionsEntity> arrayList = new ArrayList<>();
        arrayList.add(new EnhancementOptionsEntity(context, imageToPDFOptions.isPasswordProtected() ? R.drawable.baseline_done_24 : R.drawable.ic_password_protect_pdf, R.string.password_protect_pdf_text));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_edit_images, R.string.edit_images_text));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_image_compression, String.format(context.getResources().getString(R.string.compress_image), imageToPDFOptions.getQualityString())));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_filter_image, R.string.filter_images_Text));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_image_size, R.string.set_page_size_text));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_set_image_scale_type, R.string.image_scale_type));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_preview_pdf, R.string.preview_image_to_pdf));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_border_width, String.format(context.getResources().getString(R.string.border_dialog_title), imageToPDFOptions.getBorderWidth())));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_rearrange_image, R.string.rearrange_images));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_create_grayscale_pdf, R.string.grayscale_images));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_page_margin, R.string.add_margins));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_page_number, R.string.show_pg_num));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_add_watermark, R.string.add_watermark));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_color_fill, R.string.page_color));
        return arrayList;
    }
}
