package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.templatemela.smartpdfreader.R;

public class FeedbackUtils {
    private final Activity mContext;
    private final SharedPreferences mSharedPreferences;

    public FeedbackUtils(Activity activity) {
        mContext = activity;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    public void shareApplication() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", this.mContext.getResources().getString(R.string.rate_us_text));
        openMailIntent(intent);
    }

    public void openMailIntent(Intent intent) {
        try {
            mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share_chooser)));
        } catch (ActivityNotFoundException exception) {
            StringUtils.getInstance().showSnackbar(mContext, R.string.snackbar_no_share_app);
        }
    }

    public void rateUs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(this.mContext.getString(R.string.rate_title)).setMessage(this.mContext.getString(R.string.rate_dialog_text))
                .setNegativeButton(this.mContext.getString(R.string.rate_negative), (dialogInterface, i) -> {
                    mSharedPreferences.edit().putInt(Constants.LAUNCH_COUNT, 0).apply();
                    dialogInterface.cancel();
                }).setPositiveButton(this.mContext.getString(R.string.rate_positive), (dialogInterface, i) -> {
            try {
                mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + mContext.getApplicationContext().getPackageName())));
            } catch (Exception exception) {
                openWebPage("https://play.google.com/store/apps/details?id=" + mContext.getPackageName());
            }
            mSharedPreferences.edit().putInt(Constants.LAUNCH_COUNT, -1).apply();
            dialogInterface.dismiss();
        }).setNeutralButton(this.mContext.getString(R.string.rate_us_never), (dialogInterface, i) -> {
            mSharedPreferences.edit().putInt(Constants.LAUNCH_COUNT, -1).apply();
            dialogInterface.cancel();
        });
        builder.create().show();
    }


    public void openWebPage(String url) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            mContext.startActivity(intent);
        }
    }
}
