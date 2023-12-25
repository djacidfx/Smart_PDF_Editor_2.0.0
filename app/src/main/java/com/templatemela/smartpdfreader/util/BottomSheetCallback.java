package com.templatemela.smartpdfreader.util;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class BottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {
    private final boolean mIsAdded;
    private final ImageView mUpArrow;

    @Override
    public void onStateChanged(@NonNull View view, int newState) {
    }

    public BottomSheetCallback(ImageView upArrow, boolean isAdded) {
        this.mUpArrow = upArrow;
        this.mIsAdded = isAdded;
    }

    @Override
    public void onSlide(@NonNull View view, float f) {
        if (mIsAdded) {
            animateBottomSheetArrow(f);
        }
    }

    private void animateBottomSheetArrow(float f) {
        if (f >= 0.0f && f <= 1.0f) {
            mUpArrow.setRotation(f * -180.0f);
        } else if (f >= -1.0f && f < 0.0f) {
            mUpArrow.setRotation(f * 180.0f);
        }
    }
}
