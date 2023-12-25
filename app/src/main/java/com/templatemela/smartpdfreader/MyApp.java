package com.templatemela.smartpdfreader;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.templatemela.smartpdfreader.ads.AdsManagerConfiguration;
import com.templatemela.smartpdfreader.ads.AdsService;
import com.templatemela.smartpdfreader.managers.SharedPreferencesManager;
import com.templatemela.smartpdfreader.util.AppUtils;

public class MyApp extends MultiDexApplication {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AdsService.getInstance().initAdsService(this, new AdsManagerConfiguration.Builder().setDebug(false)
                .setAdMobAppId(getString(R.string.admob_app_id))
                .setAdMobBannerId(getString(R.string.admob_banner_id))
                .setAdMobNativeId(getString(R.string.admob_native_id))
                .setAdMobInterstialId(getString(R.string.admob_interstitial_id))
                .setAdMobOpenAdId(getString(R.string.admob_open_id))
                .setAdMobRewardVideoId(getString(R.string.admob_reward_id))
                .setDarkThemeMode(true)
                .setAdsEnabled(!(SharedPreferencesManager.getInstance().contains(AdsService.IS_ADS_DISABLED) && SharedPreferencesManager.getInstance().getBoolean(AdsService.IS_ADS_DISABLED)))
                .setPersonalizedAdsEnabled(true)
                .setInitInterstitialAd(true)
                .setNativeAdLayoutDefault(false)
                .build());


        initTheme();

    }

    private void initTheme() {
        int savedTheme = getSavedTheme();
        if (savedTheme == AppUtils.THEME_LIGHT) {
            setTheme(AppCompatDelegate.MODE_NIGHT_NO,AppUtils.THEME_LIGHT);
        } else if (savedTheme == AppUtils.THEME_DARK) {
            setTheme(AppCompatDelegate.MODE_NIGHT_YES,AppUtils.THEME_DARK);
        }
    }

    private int getSavedTheme() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(AppUtils.PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(AppUtils.KEY_THEME, AppUtils.THEME_UNDEFINED);
    }


    public void saveTheme(int theme) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(AppUtils.PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(AppUtils.KEY_THEME, theme).apply();
    }

    private void setTheme(int themeMode, int prefsMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
        saveTheme(prefsMode);
    }

    public static Context getContext() {
        return context;
    }

}
