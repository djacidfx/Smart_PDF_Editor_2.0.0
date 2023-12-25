package com.templatemela.smartpdfreader.fragment.texttopdf;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.Enhancer;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;
import com.templatemela.smartpdfreader.preferences.TextToPdfPreferences;
import com.templatemela.smartpdfreader.util.ColorUtils;
import com.templatemela.smartpdfreader.util.StringUtils;

public class PageColorEnhancer implements Enhancer {
    private final Activity mActivity;
    private final TextToPDFOptions.Builder mBuilder;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final TextToPdfPreferences mPreferences;

    PageColorEnhancer(Activity activity, TextToPDFOptions.Builder builder) {
        this.mActivity = activity;
        this.mPreferences = new TextToPdfPreferences(activity);
        this.mBuilder = builder;
        this.mEnhancementOptionsEntity = new EnhancementOptionsEntity(activity, R.drawable.ic_color_fill, R.string.page_color);
    }

    @Override
    public void enhance() {
        MaterialDialog build = new MaterialDialog.Builder(mActivity).title(R.string.page_color)
                .customView(R.layout.dialog_color_chooser, true).positiveText(R.string.ok).negativeText(R.string.cancel)
                .neutralText(R.string.remove_dialog).onNeutral((materialDialog, dialogAction) -> {
                    mPreferences.setPageColor(-1);
                    mBuilder.setPageColor(-1);
                    StringUtils.getInstance().showSnackbar(mActivity, "Page color removed.");
                }).onPositive((materialDialog, dialogAction) -> savePageColor(materialDialog, dialogAction)).build();
        ColorPickerView colorPickerView = build.getCustomView().findViewById(R.id.color_picker);
        colorPickerView.setColor(mBuilder.getPageColor());
        build.show();
    }

    public void savePageColor(MaterialDialog materialDialog, DialogAction dialogAction) {
        View customView = materialDialog.getCustomView();
        CheckBox checkBox = customView.findViewById(R.id.set_default);
        ColorPickerView colorPickerView = customView.findViewById(R.id.color_picker);
        int color = colorPickerView.getColor();
        if (ColorUtils.getInstance().colorSimilarCheck(mPreferences.getFontColor(), color)) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_color_too_close);
        }
        if (checkBox.isChecked()) {
            mPreferences.setPageColor(color);
        }
        StringUtils.getInstance().showSnackbar(mActivity, "Page color saved.");
        mBuilder.setPageColor(color);
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }
}
