package com.templatemela.smartpdfreader.fragment.texttopdf;

import android.app.Activity;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.Enhancer;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;
import com.templatemela.smartpdfreader.preferences.TextToPdfPreferences;
import com.templatemela.smartpdfreader.util.StringUtils;

public class FontSizeEnhancer implements Enhancer {
    private final Activity mActivity;
    private final TextToPDFOptions.Builder mBuilder;
    private EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final TextToPdfPreferences mPreferences;
    private final TextToPdfContract.View mView;

    FontSizeEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder) {
        mActivity = activity;
        mPreferences = new TextToPdfPreferences(activity);
        mBuilder = builder;
        builder.setFontSizeTitle(String.format(activity.getString(R.string.edit_font_size), mPreferences.getFontSize()));
        mEnhancementOptionsEntity = new EnhancementOptionsEntity(activity.getResources().getDrawable(R.drawable.ic_text_size), builder.getFontSizeTitle());
        mView = view;
    }

    @Override
    public void enhance() {
        new MaterialDialog.Builder(mActivity).title(mBuilder.getFontSizeTitle())
                .customView(R.layout.dialog_font_size, true)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((materialDialog, dialogAction) -> {
                    changeFontSize(materialDialog, dialogAction);

                }).show();
    }

    public void changeFontSize(MaterialDialog materialDialog, DialogAction dialogAction) {
        EditText editText = materialDialog.getCustomView().findViewById(R.id.fontInput);
        CheckBox checkBox = materialDialog.getCustomView().findViewById(R.id.cbSetFontDefault);
        try {
            int parseInt = Integer.parseInt(String.valueOf(editText.getText()));
            if (parseInt <= 1000) {
                if (parseInt >= 0) {
                    mBuilder.setFontSize(parseInt);
                    showFontSize();
                    StringUtils.getInstance().showSnackbar(mActivity,  R.string.font_size_changed);
                    if (checkBox.isChecked()) {
                        mPreferences.setFontSize(mBuilder.getFontSize());
                        mBuilder.setFontSizeTitle(String.format(this.mActivity.getString(R.string.edit_font_size), mPreferences.getFontSize()));
                        return;
                    }
                    return;
                }
            }
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        } catch (NumberFormatException exception) {
            StringUtils.getInstance().showSnackbar(mActivity, R.string.invalid_entry);
        }
    }

    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return mEnhancementOptionsEntity;
    }

    private void showFontSize() {
        mEnhancementOptionsEntity.setName(String.format(this.mActivity.getString(R.string.font_size), new Object[]{Integer.valueOf(this.mBuilder.getFontSize())}));
        mView.updateView();
    }
}
