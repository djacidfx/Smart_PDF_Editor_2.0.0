package com.templatemela.smartpdfreader.util;

import android.content.Intent;

public class ResultUtils {
    private ResultUtils() {
    }

    private static class SingletonHolder {
        static final ResultUtils INSTANCE = new ResultUtils();

        private SingletonHolder() {
        }
    }

    public static ResultUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean checkResultValidity(int i, Intent intent) {
        return i == -1 && intent != null && intent.getData() != null;
    }
}
