package com.templatemela.smartpdfreader.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.templatemela.smartpdfreader.R;

import java.util.Objects;

public class StringUtils {
    private StringUtils() {
    }

    private static class SingletonHolder {
        static final StringUtils INSTANCE = new StringUtils();

        private SingletonHolder() {
        }
    }

    public static StringUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.toString().trim().equals("");
    }

    public boolean isNotEmpty(CharSequence charSequence) {
        return charSequence != null && !charSequence.toString().trim().equals("");
    }

    public void showSnackbar(Activity activity, int i) {
        Objects.requireNonNull(activity);
        Snackbar.make(activity.findViewById(android.R.id.content), i, Snackbar.LENGTH_LONG).show();
    }

    public void showSnackbar(Activity activity, String msg) {
        Objects.requireNonNull(activity);
        Snackbar make = Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
        ((TextView) make.getView().findViewById(R.id.snackbar_text)).setMaxLines(5);
        make.show();
    }

    public Snackbar getSnackbarwithAction(Activity activity, int i) {
        Objects.requireNonNull(activity);
        return Snackbar.make(activity.findViewById(android.R.id.content), i, Snackbar.LENGTH_LONG);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus == null) {
            currentFocus = new View(activity);
        }
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public String getDefaultStorageLocation() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + Constants.pdfDirectory;
    }
}
