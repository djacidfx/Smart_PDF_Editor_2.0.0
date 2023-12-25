package com.templatemela.smartpdfreader.language;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleUtils {

    public static final String ENGLISH = "en";
    public static final String SELECTED_LANGUAGE = "selected_language";
    public static final String LANGUAGE_SELECTED_FIRST_TIME = "language_selected_first_time";


    public static ContextWrapper changeLang(Context context, String lang_code){
        Locale sysLocale = null;

        Resources rs = context.getResources();
        Configuration config = rs.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = config.getLocales().get(0);
        }
        if (!lang_code.equals("") && !sysLocale.getLanguage().equals(lang_code)) {
            Locale locale = new Locale(lang_code);
            Locale.setDefault(locale);
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        }

        return new ContextWrapper(context);
    }
}
