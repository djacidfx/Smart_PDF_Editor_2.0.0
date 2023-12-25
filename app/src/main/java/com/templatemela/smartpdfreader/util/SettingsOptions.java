package com.templatemela.smartpdfreader.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.itextpdf.text.Font;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;

import java.util.ArrayList;

public class SettingsOptions {
    public static ArrayList<EnhancementOptionsEntity> getEnhancementOptions(Context context) {
        ArrayList<EnhancementOptionsEntity> arrayList = new ArrayList<>();
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_image_compression, String.format(context.getString(R.string.image_compression_value_default), defaultSharedPreferences.getInt(Constants.DEFAULT_COMPRESSION, 30))));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_image_size, String.format(context.getString(R.string.page_size_value_def), defaultSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT, Constants.DEFAULT_PAGE_SIZE))));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_text_size, String.format(context.getString(R.string.font_size_value_def), defaultSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, 11))));
        Font.FontFamily valueOf = Font.FontFamily.valueOf(defaultSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT, Constants.DEFAULT_FONT_FAMILY));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_extract_text, String.format(context.getString(R.string.font_family_value_def), valueOf.name())));
     //   arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_theme_value, String.format(context.getString(R.string.theme_value_def), defaultSharedPreferences.getString(Constants.DEFAULT_THEME_TEXT, "White"))));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_page_margin, R.string.image_scale_type));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_set_password, R.string.change_master_pwd));
        arrayList.add(new EnhancementOptionsEntity(context, R.drawable.ic_page_number, R.string.show_pg_num));
        return arrayList;
    }
}
