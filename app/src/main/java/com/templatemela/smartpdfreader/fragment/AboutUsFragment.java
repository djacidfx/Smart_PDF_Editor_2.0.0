package com.templatemela.smartpdfreader.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.templatemela.smartpdfreader.R;

import butterknife.ButterKnife;

public class AboutUsFragment extends Fragment {

    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_about_us, viewGroup, false);
        ButterKnife.bind(this, inflate);

        try {
            PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
            TextView textView = inflate.findViewById(R.id.version_value);
            textView.setText(textView.getText().toString() + " " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return inflate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
}
