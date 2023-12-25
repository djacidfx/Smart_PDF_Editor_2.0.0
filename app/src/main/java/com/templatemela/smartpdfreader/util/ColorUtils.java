package com.templatemela.smartpdfreader.util;

import android.graphics.Color;

public class ColorUtils {
    private static final double COLOR_DIFF_THRESHOLD = 30.0d;

    private ColorUtils() {
    }

    private static class SingletonHolder {
        static final ColorUtils INSTANCE = new ColorUtils();

        private SingletonHolder() {
        }
    }

    public static ColorUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean colorSimilarCheck(int i, int i2) {
        return Math.sqrt((Math.pow((double) (Color.red(i) - Color.red(i2)), 2.0d) + Math.pow(Color.green(i) - Color.green(i2), 2.0d)) + Math.pow((double) (Color.blue(i) - Color.blue(i2)), 2.0d)) < COLOR_DIFF_THRESHOLD;
    }
}
