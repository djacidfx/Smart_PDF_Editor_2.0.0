package com.templatemela.smartpdfreader.fragment.texttopdf;

import android.app.Activity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.itextpdf.text.Font;
import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.interfaces.Enhancer;
import com.templatemela.smartpdfreader.model.EnhancementOptionsEntity;
import com.templatemela.smartpdfreader.model.TextToPDFOptions;
import com.templatemela.smartpdfreader.preferences.TextToPdfPreferences;
import com.templatemela.smartpdfreader.util.StringUtils;

public class FontFamilyEnhancer implements Enhancer {
    private final Activity mActivity;
    private final TextToPDFOptions.Builder mBuilder;
    private EnhancementOptionsEntity mEnhancementOptionsEntity;
    private final TextToPdfPreferences mPreferences;
    private TextToPdfContract.View mView;

    FontFamilyEnhancer(Activity activity, TextToPdfContract.View view, TextToPDFOptions.Builder builder) {
        this.mActivity = activity;
        this.mPreferences = new TextToPdfPreferences(activity);
        this.mBuilder = builder;
        this.mEnhancementOptionsEntity = new EnhancementOptionsEntity(activity, R.drawable.ic_font_family, String.format(activity.getString(R.string.default_font_family_text), new Object[]{builder.getFontFamily().name()}));
        this.mView = view;
    }

    @Override
    public void enhance() {
        String fontFamily = mPreferences.getFontFamily();
        int ordinal = Font.FontFamily.valueOf(fontFamily).ordinal();
        MaterialDialog build = new MaterialDialog.Builder(mActivity).title(String.format(this.mActivity.getString(R.string.default_font_family_text), fontFamily)).customView(R.layout.dialog_font_family, true).positiveText(R.string.ok).negativeText(R.string.cancel)
                .onPositive((materialDialog, dialogAction) ->
                        {
                            View customView = materialDialog.getCustomView();
                            String name = ((RadioButton) customView.findViewById(((RadioGroup) customView.findViewById(R.id.radio_group_font_family)).getCheckedRadioButtonId())).getText().toString();
                            mBuilder.setFontFamily(Font.FontFamily.valueOf(name));
                            mPreferences.setFontFamily(name);
                            showFontFamily();
                            StringUtils.getInstance().showSnackbar(mActivity, "font family saved.");
                        }
                ).build();
        ((RadioButton) ((RadioGroup) build.getCustomView()
                .findViewById(R.id.radio_group_font_family)).getChildAt(ordinal)).setChecked(true);
        build.show();
    }


    @Override
    public EnhancementOptionsEntity getEnhancementOptionsEntity() {
        return this.mEnhancementOptionsEntity;
    }

    private void showFontFamily() {
        mEnhancementOptionsEntity.setName(this.mActivity.getString(R.string.font_family_text) + mBuilder.getFontFamily().name());
        mView.updateView();
    }
}
