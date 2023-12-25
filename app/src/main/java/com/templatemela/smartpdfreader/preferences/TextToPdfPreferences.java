package com.templatemela.smartpdfreader.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.templatemela.smartpdfreader.util.Constants;

public class TextToPdfPreferences {
    private final SharedPreferences mSharedPreferences;

    public TextToPdfPreferences(Context context) {
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getFontColor() {
        return this.mSharedPreferences.getInt(Constants.DEFAULT_FONT_COLOR_TEXT, Constants.DEFAULT_FONT_COLOR);
    }

    public void setFontColor(int i) {
        SharedPreferences.Editor edit = this.mSharedPreferences.edit();
        edit.putInt(Constants.DEFAULT_FONT_COLOR_TEXT, i);
        edit.apply();
    }

    public int getPageColor() {
        return this.mSharedPreferences.getInt(Constants.DEFAULT_PAGE_COLOR_TTP, -1);
    }

    public void setPageColor(int pageColor) {
        SharedPreferences.Editor edit = this.mSharedPreferences.edit();
        edit.putInt(Constants.DEFAULT_PAGE_COLOR_TTP, pageColor);
        edit.apply();
    }

    public String getFontFamily() {
        return this.mSharedPreferences.getString(Constants.DEFAULT_FONT_FAMILY_TEXT, Constants.DEFAULT_FONT_FAMILY);
    }

    public void setFontFamily(String str) {
        SharedPreferences.Editor edit = this.mSharedPreferences.edit();
        edit.putString(Constants.DEFAULT_FONT_FAMILY_TEXT, str);
        edit.apply();
    }

    public int getFontSize() {
        return this.mSharedPreferences.getInt(Constants.DEFAULT_FONT_SIZE_TEXT, 11);
    }

    public void setFontSize(int i) {
        SharedPreferences.Editor edit = this.mSharedPreferences.edit();
        edit.putInt(Constants.DEFAULT_FONT_SIZE_TEXT, i);
        edit.apply();
    }

    public String getPageSize() {
        return this.mSharedPreferences.getString(Constants.DEFAULT_PAGE_SIZE_TEXT, Constants.DEFAULT_PAGE_SIZE);
    }

    public void setPageSize(String str) {
        SharedPreferences.Editor edit = this.mSharedPreferences.edit();
        edit.putString(Constants.DEFAULT_PAGE_SIZE_TEXT, str);
        edit.apply();
    }
}
