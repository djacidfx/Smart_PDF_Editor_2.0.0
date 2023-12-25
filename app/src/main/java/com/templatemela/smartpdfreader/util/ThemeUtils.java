package com.templatemela.smartpdfreader.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.templatemela.smartpdfreader.R;

public class ThemeUtils {

    private static class SingletonHolder {
        static final ThemeUtils INSTANCE = new ThemeUtils();

        private SingletonHolder() {
        }
    }

    public static ThemeUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setThemeApp(Context context) {
        String name = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.DEFAULT_THEME_TEXT, "White");
        if (name != null) {
            name.hashCode();
            char c = 65535;
            switch (name.hashCode()) {
                case 2122646:
                    if (name.equals(Constants.THEME_DARK)) {
                        c = 0;
                        break;
                    }
                    break;
                case 64266207:
                    if (name.equals(Constants.THEME_BLACK)) {
                        c = 1;
                        break;
                    }
                    break;
                case 83549193:
                    if (name.equals("White")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    context.setTheme(R.style.ActivityThemeDark);
                    return;
                case 1:
                    context.setTheme(R.style.AppThemeBlack);
                    return;
                case 2:
                    context.setTheme(R.style.AppThemeWhite);
                    return;
                default:
                    return;
            }
        }
    }

    public int getSelectedThemePosition(Context context) {
        String name = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.DEFAULT_THEME_TEXT, "White");
        name.hashCode();
        if (!name.equals(Constants.THEME_DARK)) {
            return !name.equals("White") ? 0 : 2;
        }
        return 1;
    }

    public void saveTheme(Context context, String name) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString(Constants.DEFAULT_THEME_TEXT, name);
        edit.apply();
    }
}
