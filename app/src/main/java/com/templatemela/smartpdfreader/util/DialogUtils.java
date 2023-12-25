package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.templatemela.smartpdfreader.R;

public class DialogUtils {
    private DialogUtils() {
    }

    private static class SingletonHolder {
        static final DialogUtils INSTANCE = new DialogUtils();

        private SingletonHolder() {
        }
    }

    public static DialogUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public MaterialDialog.Builder createWarningDialog(Activity activity, int content) {
        return new MaterialDialog.Builder(activity).title(R.string.warning).content(content).positiveText(android.R.string.ok).negativeText(android.R.string.cancel);
    }

    public MaterialDialog.Builder createOverwriteDialog(Activity activity) {
        return new MaterialDialog.Builder(activity).title(R.string.warning).content(R.string.overwrite_message).positiveText(android.R.string.ok).negativeText(android.R.string.cancel);
    }

    public MaterialDialog.Builder createCustomDialog(Activity activity, int title, int content) {
        return new MaterialDialog.Builder(activity).title(title).content(content).positiveText(android.R.string.ok).negativeText(android.R.string.cancel);
    }

    public MaterialDialog.Builder createCustomDialogWithoutContent(Activity activity, int title) {
        return new MaterialDialog.Builder(activity).title(title).positiveText(android.R.string.ok).negativeText(android.R.string.cancel);
    }

    public MaterialDialog createAnimationDialog(Activity activity) {
        return new MaterialDialog.Builder(activity).customView(R.layout.lottie_anim_dialog, false).build();
    }

    public MaterialDialog createCustomAnimationDialog(Activity activity, String title) {
        View inflate = LayoutInflater.from(activity).inflate(R.layout.lottie_anim_dialog, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.textView)).setText(title);
        return new MaterialDialog.Builder(activity).customView(inflate, false).build();
    }

    public void showFilesInfoDialog(Activity activity, int i) {
        int content = R.string.viewfiles_rotatepages;
        switch (i) {
            case 21:
                content = R.string.viewfiles_addpassword;
                break;
            case 22:
                content = R.string.viewfiles_removepassword;
                break;
            case 23:
                content = R.string.viewfiles_addwatermark;
                break;
        }
        new MaterialDialog.Builder(activity).title(R.string.app_name).content(content).positiveText(android.R.string.ok).build().show();
    }
}
