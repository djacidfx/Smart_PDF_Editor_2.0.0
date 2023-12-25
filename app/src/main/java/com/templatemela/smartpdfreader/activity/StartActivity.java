package com.templatemela.smartpdfreader.activity;

import android.content.Intent;
import android.os.Bundle;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.ads.AdsService;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;

public class StartActivity extends LangSupportBaseActivity {
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_start);
        AdsService.getInstance().showNativeAd(findViewById(R.id.adView), R.layout.admob_native_ad, AdsService.NativeAdType.NATIVE_AD_TYPE_MEDIA);

        findViewById(R.id.txtStart).setOnClickListener(view -> {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            AdsService.getInstance().showInterstitialAd(this);
            finishAffinity();
        });
    }
}
