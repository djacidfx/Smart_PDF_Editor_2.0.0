package com.templatemela.smartpdfreader.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.ads.formats.Banner;
import com.templatemela.smartpdfreader.ads.formats.Interstitial;
import com.templatemela.smartpdfreader.ads.formats.NativeAdApp;
import com.templatemela.smartpdfreader.ads.formats.OpenAd;

public class AdsService implements LifecycleObserver {
    public static final String IS_ADS_DISABLED = "is_ad_disabled";
    public static final String IS_REWARD_VIDEO = "is_reward_video";

    public static String TAG = "tag_ad_manager";
    private static AdsService adsService;
    private AdsManagerConfiguration adsManagerConfiguration;

    private Context mContext;


    public RewardedAd mRewardedAd;

    public enum NativeAdType {
        NATIVE_AD_TYPE_MEDIUM,
        NATIVE_AD_TYPE_MEDIA
    }

    public static AdsService getInstance() {
        if (adsService == null) {
            adsService = new AdsService();
        }
        return adsService;
    }

    public void initAdsService(final Context context, AdsManagerConfiguration adsManagerConfiguration2) {
        MobileAds.initialize(context, initializationStatus -> {
            Log.d(AdsService.TAG, "inside AdsService class, onInitializationComplete()");
            OpenAd.getInstance().initOpenAd(context);
            OpenAd.getInstance().fetchAd();
            loadRewardVideo();
        });
        this.mContext = context;
        this.adsManagerConfiguration = adsManagerConfiguration2;
        if (adsManagerConfiguration2.isInitInterstitialAd()) {
            Log.d(TAG, "default: initialise interstitial ad on start");
            Interstitial.initialize(this.mContext);
        }
    }

    public void setConfiguration(AdsManagerConfiguration adsManagerConfiguration2) {
        Log.d(TAG, "inside AdsService class, setConfiguration()");
        this.adsManagerConfiguration = adsManagerConfiguration2;
    }

    public AdsManagerConfiguration getConfiguration() {
        Log.d(TAG, "inside AdsService class, getConfiguration()");
        return this.adsManagerConfiguration;
    }

    public boolean isEnableAds() {
        return this.adsManagerConfiguration.isAdsEnabled();
    }

    public void showInterstitialAd(Activity mActivity) {
        if (AdsUtil.isNetworkAvailable(mContext) && isEnableAds()) {
            Interstitial.getInterstitialAd(mActivity);
        }
    }

    public void showAdaptiveBannerAd(FrameLayout frameLayout) {
        if (AdsUtil.isNetworkAvailable(mContext) && isEnableAds()) {
            Banner.getAdaptiveBanner(mContext, frameLayout);
        }
    }

    public void showOpenAdIfAvailable(final Activity activity) {
        if (OpenAd.getInstance().getIsShowingAd() || !OpenAd.getInstance().isAdAvailable()) {
            OpenAd.getInstance().fetchAd();
            return;
        }
        Log.d(TAG, "showOpenAdIfAvailable()");
        OpenAd.getInstance().displayAd(activity, new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                OpenAd.getInstance().setIsShowingAd(false);
                try {
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                try {
                    OpenAd.getInstance().setIsShowingAd(false);
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdShowedFullScreenContent() {
                OpenAd.getInstance().setIsShowingAd(true);
            }
        });
    }

    public void showNativeAd(FrameLayout frameLayout, int i, NativeAdType nativeAdType) {
        if (AdsUtil.isNetworkAvailable(mContext) && isEnableAds()) {
            NativeAdApp.getNativeAd(mContext, frameLayout, i, nativeAdType);
        }
    }

    public void showNativeAdView(FrameLayout frameLayout, Context context) {
        if (AdsUtil.isNetworkAvailable(mContext) && isEnableAds()) {
            MobileAds.initialize(context, initializationStatus -> {
                Log.d(AdsService.TAG, "inside AdsService class, onInitializationComplete()");
            });

            AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.admob_native_id));
            builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    NativeAdView adView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified, null);
                    populateNativeAdView(nativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }
            });
            AdLoader adLoader = builder
                    .withAdListener(
                            new AdListener() {
                                @Override
                                public void onAdFailedToLoad(LoadAdError loadAdError) {
                               /*     String error =
                                            String.format(
                                                    "domain: , code: %d, message: ",
                                                    loadAdError.getDomain(),
                                                    loadAdError.getCode(),
                                                    loadAdError.getMessage());
                                    Log.e("EEEEEEEE ", error);*/
                                    Toast.makeText(
                                            context,
                                            "Failed to load native ad: " + loadAdError.getMessage(),
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    private void populateSimpleTemplateAdView(final NativeCustomTemplateAd nativeCustomTemplateAd,
                                              View adView) {
        TextView headline = adView.findViewById(R.id.simplecustom_headline);
        TextView caption = adView.findViewById(R.id.simplecustom_caption);

        headline.setText(nativeCustomTemplateAd.getText("Headline"));
        caption.setText(nativeCustomTemplateAd.getText("Caption"));

        FrameLayout mediaPlaceholder = adView.findViewById(R.id.simplecustom_media_placeholder);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeCustomTemplateAd.getVideoController();

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeCustomTemplateAd has a video asset.
        if (vc.hasVideoContent()) {
            mediaPlaceholder.addView(nativeCustomTemplateAd.getVideoMediaView());


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                }
            });
        } else {
            ImageView mainImage = new ImageView(mContext);
            mainImage.setAdjustViewBounds(true);
            mainImage.setImageDrawable(nativeCustomTemplateAd.getImage("MainImage").getDrawable());

            mainImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nativeCustomTemplateAd.performClick("MainImage");
                }
            });
            mediaPlaceholder.addView(mainImage);
        }
    }

    public void loadRewardVideo() {
        if (AdsUtil.isNetworkAvailable(mContext) && isEnableAds()) {
            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
            RewardedAd.load(
                    mContext,
                    mContext.getResources().getString(R.string.admob_reward_id),
                    adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error.
                            Log.d(TAG, loadAdError.getMessage());
                            mRewardedAd = null;
//                            Toast.makeText(mContext, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
//                            Toast.makeText(mContext, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void showRewardVideo(Activity activity) {
        if (mRewardedAd == null) {
            Log.d("TAG", "The rewarded ad wasn't ready yet.");
            return;
        }
        mRewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "onAdShowedFullScreenContent");
//                        Toast.makeText(mContext, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when ad fails to show.
                        Log.d(TAG, "onAdFailedToShowFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mRewardedAd = null;
//                        Toast.makeText(
//                                mContext, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        Log.d(TAG, "onAdDismissedFullScreenContent");
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mRewardedAd = null;
//                        Toast.makeText(mContext, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
//                                .show();
                        // Preload the next rewarded ad.
                        loadRewardVideo();
                    }
                });
        mRewardedAd.show(activity
                ,
                new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        // Handle the reward.
                        Log.d("TAG", "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                        mContext.getSharedPreferences("Preferences", 0).edit().putBoolean(IS_REWARD_VIDEO, true).apply();
                        loadRewardVideo();
                    }
                });
    }


    public String setBannerId() {
        if (adsManagerConfiguration.isDebug()) {
            return "ca-app-pub-3940256099942544/6300978111";
        }
        return this.adsManagerConfiguration.getAdMobBannerId();
    }

    public String setNativeId() {
        if (adsManagerConfiguration.isDebug()) {
            return "ca-app-pub-3940256099942544/2247696110";
        }
        return this.adsManagerConfiguration.getAdMobNativeId();
    }

    public String setInterstialId() {
        if (adsManagerConfiguration.isDebug()) {
            return "ca-app-pub-3940256099942544/1033173712";
        }
        return this.adsManagerConfiguration.getAdMobInterstialId();
    }

    public String setOpenAdId() {
        if (adsManagerConfiguration.isDebug()) {
            return "ca-app-pub-3940256099942544/3419835294";
        }
        return this.adsManagerConfiguration.getAdMobAppId();
    }

    public String setRewardVideoAdId() {
        if (adsManagerConfiguration.isDebug()) {
            return "ca-app-pub-3940256099942544/5354046379";
        }
        return this.adsManagerConfiguration.getAdMobRewardVideoId();
    }
}
