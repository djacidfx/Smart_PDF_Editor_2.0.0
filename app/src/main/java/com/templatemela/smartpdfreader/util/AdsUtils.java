package com.templatemela.smartpdfreader.util;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.templatemela.smartpdfreader.R;

import java.util.ArrayList;
import java.util.List;
public class AdsUtils {

    public static boolean isShowAds = true;
    static InterstitialAd mInterstitialAd;


    public static void initAd(Context context) {
        if (isShowAds) {
            List<String> testDevices = new ArrayList<>();
            testDevices.add("B3EEABB8EE11C2BE770B684D95219ECB");
            MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(testDevices).build());
            MobileAds.initialize(context, (OnInitializationCompleteListener) new OnInitializationCompleteListener() {
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }
    }


    public static void showGoogleBannerAd(Context context, AdView adView) {
        adView.setVisibility(View.VISIBLE);
        MobileAds.initialize(context, (OnInitializationCompleteListener) new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adView.loadAd(new AdRequest.Builder().build());
    }

    public static void loadInterAd(Context context) {
        if (isShowAds) {

            InterstitialAd.load(context, context.getString(R.string.admob_interstitial_id), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    mInterstitialAd = null;
                }
            });

        }
    }

    public static void showInterAd(final Context context, final Intent intent) {
        if (isShowAds) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show((Activity) context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        AdsUtils.startActivity(context, intent);
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("TAG", "The ad failed to show.");
                    }

                    public void onAdShowedFullScreenContent() {
                        AdsUtils.mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            } else {
                startActivity(context, intent);
            }
        } else {
            startActivity(context, intent);
        }

        return;
    }

    public static void showAdWithChangeFragment(Context context,FragmentManager fragmentManager, Fragment fragment){

        if (isShowAds) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show((Activity) context);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
                        Log.d("TAG", "The ad was dismissed.");
                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("TAG", "The ad failed to show.");
                    }

                    public void onAdShowedFullScreenContent() {
                        AdsUtils.mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            } else {
                fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            }
        } else {
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        }
    }

    static void startActivity(Context context, Intent intent) {
        if (intent != null) {
            context.startActivity(intent);
        }
    }


}
