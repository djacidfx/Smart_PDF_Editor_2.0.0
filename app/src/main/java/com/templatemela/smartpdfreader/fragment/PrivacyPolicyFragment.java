package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdView;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.util.AdsUtils;

import butterknife.ButterKnife;

public class PrivacyPolicyFragment extends Fragment {
    protected WebView web;
    private AdView adView;
    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_privacy_policy, viewGroup, false);
        ButterKnife.bind(this, inflate);
        adView = inflate.findViewById(R.id.adView);
        AdsUtils.showGoogleBannerAd(getContext(), adView);
        this.web = (WebView) inflate.findViewById(R.id.webView);
        this.web.loadUrl("https://templatemela.com/privacy");

        return inflate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
}
