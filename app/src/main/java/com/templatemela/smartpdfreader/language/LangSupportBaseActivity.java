package com.templatemela.smartpdfreader.language;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class LangSupportBaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang_code = LocaleUtils.ENGLISH;
        SharedPreferences sharedPreferences = newBase.getSharedPreferences("MyLangPref", MODE_PRIVATE);
        if (sharedPreferences != null) {
            lang_code = sharedPreferences.getString(LocaleUtils.SELECTED_LANGUAGE, LocaleUtils.ENGLISH);
        }
        Context context = LocaleUtils.changeLang(newBase, lang_code);
        super.attachBaseContext(context);
    }
}
