package com.templatemela.smartpdfreader.util;

import android.app.Activity;

import com.dd.morphingbutton.MorphingButton;
import com.templatemela.smartpdfreader.R;

public class MorphButtonUtility {
    private final Activity mActivity;

    public MorphButtonUtility(Activity activity) {
        this.mActivity = activity;
    }

    public int integer() {
        return this.mActivity.getResources().getInteger(R.integer.mb_animation);
    }

    private int dimen(int i) {
        return (int) this.mActivity.getResources().getDimension(i);
    }

    private int color(int color) {
        return this.mActivity.getResources().getColor(color);
    }

    public void morphToSquare(MorphingButton morphingButton, int i) {
        String name;
        MorphingButton.Params defaultButton = defaultButton(i);
        if (morphingButton.getText().toString().isEmpty()) {
            name = this.mActivity.getString(R.string.create_pdf);
        } else {
            name = morphingButton.getText().toString();
        }
        defaultButton.color(color(R.color.mb_blue));
        defaultButton.colorPressed(color(R.color.mb_blue_dark));

        defaultButton.text(name);
        morphingButton.morph(defaultButton);
    }

    public void morphToSuccess(MorphingButton morphingButton) {
        morphingButton.morph(MorphingButton.Params.create().duration(integer()).cornerRadius(dimen(R.dimen.mb_height_56)).width(dimen(R.dimen.mb_height_56)).height(dimen(R.dimen.mb_height_56)).color(color(R.color.mb_green)).colorPressed(color(R.color.mb_green_dark)).icon(R.drawable.ic_check_white_24dp));
    }

    public void morphToGrey(MorphingButton morphingButton, int i) {
        MorphingButton.Params defaultButton = defaultButton(i);
        defaultButton.color(color(R.color.mb_gray));
        defaultButton.colorPressed(color(R.color.mb_gray));
        defaultButton.cornerRadius(dimen(R.dimen.mb_corner_radius_10));
        defaultButton.text(morphingButton.getText().toString());
        morphingButton.morph(defaultButton);
    }

    private MorphingButton.Params defaultButton(int i) {
        return MorphingButton.Params.create().duration(i).cornerRadius(dimen(R.dimen.mb_corner_radius_10)).width(-1).height(-2);
    }

    public void setTextAndActivateButtons(String path, MorphingButton morphingButton, MorphingButton morphingButton2) {
        morphingButton.setText(path);
        morphingButton.setBackgroundColor(mActivity.getResources().getColor(R.color.mb_green_dark));
        morphingButton2.setEnabled(true);
        morphToSquare(morphingButton2, integer());
    }

    public void initializeButton(MorphingButton morphingButton, MorphingButton morphingButton2) {
        morphingButton.setText(R.string.merge_file_select);
        morphingButton.setBackgroundColor(mActivity.getResources().getColor(R.color.mb_blue));
        morphToGrey(morphingButton2, integer());
        morphingButton2.setEnabled(false);
    }

    public void initializeButtonForAddText(MorphingButton morphingButton, MorphingButton morphingButton2, MorphingButton morphingButton3) {
        morphingButton.setText(R.string.select_pdf_file);
        morphingButton.setBackgroundColor(mActivity.getResources().getColor(R.color.mb_blue));
        morphingButton2.setText(R.string.select_text_file);
        morphingButton2.setBackgroundColor(mActivity.getResources().getColor(R.color.mb_blue));
        morphToGrey(morphingButton3, integer());
        morphingButton3.setEnabled(false);
    }
}
