package com.templatemela.smartpdfreader.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.language.LanguageSelectionActivity;
import com.templatemela.smartpdfreader.language.LocaleUtils;

public class SplashActivity extends LangSupportBaseActivity {
    Handler handler;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        handler = new Handler(Looper.myLooper());
        handler.postDelayed(this::gotoMainActivity, 2000);
    }
    public void gotoMainActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyLangPref", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean(LocaleUtils.LANGUAGE_SELECTED_FIRST_TIME, false);
        Intent intent;
        if (isFirstTime) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LanguageSelectionActivity.class);
            intent.putExtra("isFromMain",false);
        }
        startActivity(intent);
        finishAffinity();
    }
}
