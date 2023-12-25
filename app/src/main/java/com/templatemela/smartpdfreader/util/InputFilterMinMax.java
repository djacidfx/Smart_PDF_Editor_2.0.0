package com.templatemela.smartpdfreader.util;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterMinMax implements InputFilter {
    private int max;
    private int min;

    private boolean isInRange(int a, int b, int c) {
        if (b > a) {
            if (c >= a && c <= b) {
                return true;
            }
        } else if (c >= b && c <= a) {
            return true;
        }
        return false;
    }

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            if (isInRange(min, max, Integer.parseInt(dest.toString() + source.toString()))) {
                return null;
            }
            return "";
        } catch (NumberFormatException exception) {
            return "";
        }
    }
}
