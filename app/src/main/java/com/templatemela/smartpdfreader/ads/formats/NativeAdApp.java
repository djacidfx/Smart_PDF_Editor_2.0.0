package com.templatemela.smartpdfreader.ads.formats;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.ads.AdsService;

import java.util.List;

public class NativeAdApp {

    public static String TAG = "tag_ad_manager";

    public static void getNativeAd(final Context context, final FrameLayout frameLayout, final int i, final AdsService.NativeAdType nativeAdType) {
        final AdLoader adLoader = new AdLoader.Builder(context, context.getString(R.string.admob_native_id))
//        final AdLoader adLoader = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(ad -> {
                    // some code that displays the ad.
                    NativeAdView NativeAdView;
                    Log.d(NativeAdApp.TAG, "Native Ad: onUnifiedNativeAdLoaded()");
                    if (!AdsService.getInstance().getConfiguration().isNativeAdLayoutDefault()) {
                        NativeAdView = (NativeAdView) LayoutInflater.from(context).inflate(i, null);
                    } else if (nativeAdType == AdsService.NativeAdType.NATIVE_AD_TYPE_MEDIA) {
                        NativeAdView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.admob_native_default_media, null);
                    } else {
                        NativeAdView = (NativeAdView) LayoutInflater.from(context).inflate(R.layout.admob_native_default, null);
                    }
                    populateNativeAdView(ad, NativeAdView, context, nativeAdType);
                    frameLayout.removeAllViews();
                    frameLayout.addView(NativeAdView);

                }).build();
        adLoader.loadAds(new AdRequest.Builder().build(), 3);
    }


    public static void populateNativeAdView(NativeAd nativeAdView, NativeAdView NativeAdView, Context context, AdsService.NativeAdType nativeAdType) {
        if (nativeAdView != null) {
            try {
                NativeAdView.setIconView(NativeAdView.findViewById(R.id.ad_icon));
                NativeAdView.setHeadlineView(NativeAdView.findViewById(R.id.ad_headline));
                NativeAdView.setBodyView(NativeAdView.findViewById(R.id.ad_body));
                NativeAdView.setCallToActionView(NativeAdView.findViewById(R.id.ad_action));
                ((TextView) NativeAdView.getHeadlineView()).setText(nativeAdView.getHeadline());
                ((TextView) NativeAdView.getBodyView()).setText(nativeAdView.getBody());
                ((Button) NativeAdView.getCallToActionView()).setText(nativeAdView.getCallToAction());
                if (nativeAdView.getIcon() == null || nativeAdView.getIcon().getUri() == null) {
                    Glide.with(context).load(nativeAdView.getImages().get(0).getUri()).into((ImageView) NativeAdView.getIconView());
                    Log.d(TAG, "getImages() -> getUri()");
                } else {
                    Glide.with(context).load(nativeAdView.getIcon().getUri()).into((ImageView) NativeAdView.getIconView());
                    Log.d(TAG, "getIcon() -> getUri()");
                }
                if (nativeAdType == AdsService.NativeAdType.NATIVE_AD_TYPE_MEDIA) {
                    VideoController videoController = nativeAdView.getMediaContent().getVideoController();
                    videoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                        public void onVideoEnd() {
                            super.onVideoEnd();
                        }
                    });
                    MediaView mediaView = NativeAdView.findViewById(R.id.ad_media);
                    ImageView imageView = NativeAdView.findViewById(R.id.ad_image);
                    if (videoController.hasVideoContent()) {
                        NativeAdView.setMediaView(mediaView);
                        imageView.setVisibility(View.GONE);
                    } else {
                        if (!(mediaView == null)) {
                            NativeAdView.setImageView(imageView);
                            mediaView.setVisibility(View.GONE);
                        }
                        List<NativeAd.Image> images = nativeAdView.getImages();
                        if (images.size() > 0) {
                            try {
                                Glide.with(context).load(images.get(0).getUri()).into(imageView);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                NativeAdView.setNativeAd(nativeAdView);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
