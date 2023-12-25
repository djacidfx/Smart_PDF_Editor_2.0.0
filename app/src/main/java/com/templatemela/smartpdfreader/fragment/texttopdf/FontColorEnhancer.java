package com.templatemela.smartpdfreader.fragment.texttopdf;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.danielnilsson9.colorpickerview.view.ColorPickerView;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.Enhancer;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;
import com.templatemela.smartpdfreader.preferences.TextToPdfPreferences;
import com.templatemela.smartpdfreader.util.ColorUtils;
import com.templatemela.smartpdfreader.util.Constants;
import com.templatemela.smartpdfreader.util.StringUtils;

public class FontColorEnhancer implements Enhancer {
    private final Activity mActivity;
    private final TextToPDFOptions.Builder mBuilder;
    private final EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final TextToPdfPreferences mPreferences;

    FontColorEnhancer(Activity activity, TextToPDFOptions.Builder builder) {
        this.mActivity = activity;
        this.mPreferences = new TextToPdfPreferences(activity);
        this.mBuilder = builder;
        this.mEnhancementOptionsEntity = new EnhancementOptionsEntity(activity, R.drawable.ic_font_color, R.string.font_color);
    }

    @Override
    public void enhance() {
        MaterialDialog build = new MaterialDialog.Builder(mActivity).title(R.string.font_color)
                .customView(R.layout.dialog_color_chooser, true).
                        positiveText(R.string.ok).neutralText(R.string.remove_dialog)
                .negativeText(R.string.cancel)
                .onNeutral((materialDialog, dialogAction) -> {
                    mPreferences.setFontColor(Constants.DEFAULT_FONT_COLOR);
                    mBuilder.setFontColor(Constants.DEFAULT_FONT_COLOR);
                    StringUtils.getInstance().showSnackbar(mActivity, "Page color removed.");
                })
                .onPositive((materialDialog, dialogAction) -> {
                    View customView = materialDialog.getCustomView();
                    CheckBox checkBox = customView.findViewById(R.id.set_default);
                    ColorPickerView colorPickerView = customView.findViewById(R.id.color_picker);
                    int color = colorPickerView.getColor();
                    if (ColorUtils.getInstance().colorSimilarCheck(color, mPreferences.getPageColor())) {
                        StringUtils.getInstance().showSnackbar(mActivity, R.string.snackbar_color_too_close);
                    }
                    if (checkBox.isChecked()) {
                        mPreferences.setFontColor(color);
                    }
                    mBuilder.setFontColor(color);
                    StringUtils.getInstance().showSnackbar(mActivity, "font color saved.");
                })
                .build();
        ColorPickerView colorPickerView = build.getCustomView().findViewById(R.id.color_picker);
        colorPickerView.setColor(mBuilder.getFontColor());
        build.show();
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }
}
