package com.templatemela.smartpdfreader.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceManager;

import com.templatemela.smartpdfreader.R;
import com.templatemela.smartpdfreader.language.LangSupportBaseActivity;
import com.templatemela.smartpdfreader.util.Constants;

import java.util.Objects;

public class TM_FavouritesActivity extends LangSupportBaseActivity {
    private final boolean[] mKeyState = new boolean[21];
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.add_to_favourite);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.fav_pref_screen);

        storeInitialState();
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar supportActionBar = getSupportActionBar();
        Objects.requireNonNull(supportActionBar);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourite_pref_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId != R.id.fav_action_done) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            super.onBackPressed();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        onBackPressedState();
        super.onBackPressed();
    }

    private void storeInitialState() {
        mKeyState[0] = mSharedPreferences.getBoolean(Constants.IMAGE_TO_PDF_KEY, false);
        mKeyState[1] = mSharedPreferences.getBoolean(Constants.TEXT_TO_PDF_KEY, false);
        mKeyState[2] = mSharedPreferences.getBoolean(Constants.QR_BARCODE_KEY, false);
        mKeyState[3] = mSharedPreferences.getBoolean(Constants.VIEW_FILES_KEY, false);
        mKeyState[4] = mSharedPreferences.getBoolean("History", false);
        mKeyState[5] = mSharedPreferences.getBoolean("Add password", false);
        mKeyState[6] = mSharedPreferences.getBoolean("Remove password", false);
        mKeyState[7] = mSharedPreferences.getBoolean(Constants.ROTATE_PAGES_KEY, false);
        mKeyState[8] = mSharedPreferences.getBoolean(Constants.ADD_WATERMARK_KEY, false);
        mKeyState[9] = mSharedPreferences.getBoolean(Constants.ADD_IMAGES_KEY, false);
        mKeyState[10] = mSharedPreferences.getBoolean(Constants.MERGE_PDF_KEY, false);
        mKeyState[11] = mSharedPreferences.getBoolean(Constants.SPLIT_PDF_KEY, false);
        mKeyState[12] = mSharedPreferences.getBoolean(Constants.INVERT_PDF_KEY, false);
        mKeyState[13] = mSharedPreferences.getBoolean("Compress PDF", false);
        mKeyState[14] = mSharedPreferences.getBoolean(Constants.REMOVE_DUPLICATE_PAGES_KEY, false);
        mKeyState[15] = mSharedPreferences.getBoolean(Constants.REMOVE_PAGES_KEY, false);
        mKeyState[16] = mSharedPreferences.getBoolean(Constants.REORDER_PAGES_KEY, false);
        mKeyState[17] = mSharedPreferences.getBoolean(Constants.EXTRACT_IMAGES_KEY, false);
        mKeyState[18] = mSharedPreferences.getBoolean(Constants.PDF_TO_IMAGES_KEY, false);
        mKeyState[19] = mSharedPreferences.getBoolean(Constants.EXCEL_TO_PDF_KEY, false);
        mKeyState[20] = mSharedPreferences.getBoolean(Constants.ZIP_TO_PDF_KEY, false);
    }

    private void onBackPressedState() {
        mSharedPreferences.edit().putBoolean(Constants.IMAGE_TO_PDF_KEY, mKeyState[0]).apply();
        mSharedPreferences.edit().putBoolean(Constants.TEXT_TO_PDF_KEY, mKeyState[1]).apply();
        mSharedPreferences.edit().putBoolean(Constants.QR_BARCODE_KEY, mKeyState[2]).apply();
        mSharedPreferences.edit().putBoolean(Constants.VIEW_FILES_KEY, mKeyState[3]).apply();
        mSharedPreferences.edit().putBoolean("History", mKeyState[4]).apply();
        mSharedPreferences.edit().putBoolean("Add password", mKeyState[5]).apply();
        mSharedPreferences.edit().putBoolean("Remove password", mKeyState[6]).apply();
        mSharedPreferences.edit().putBoolean(Constants.ROTATE_PAGES_KEY, mKeyState[7]).apply();
        mSharedPreferences.edit().putBoolean(Constants.ADD_WATERMARK_KEY, mKeyState[8]).apply();
        mSharedPreferences.edit().putBoolean(Constants.ADD_IMAGES_KEY, mKeyState[9]).apply();
        mSharedPreferences.edit().putBoolean(Constants.MERGE_PDF_KEY, mKeyState[10]).apply();
        mSharedPreferences.edit().putBoolean(Constants.SPLIT_PDF_KEY, mKeyState[11]).apply();
        mSharedPreferences.edit().putBoolean(Constants.INVERT_PDF_KEY, mKeyState[12]).apply();
        mSharedPreferences.edit().putBoolean("Compress PDF", mKeyState[13]).apply();
        mSharedPreferences.edit().putBoolean(Constants.REMOVE_DUPLICATE_PAGES_KEY, mKeyState[14]).apply();
        mSharedPreferences.edit().putBoolean(Constants.REMOVE_PAGES_KEY, mKeyState[15]).apply();
        mSharedPreferences.edit().putBoolean(Constants.REORDER_PAGES_KEY, mKeyState[16]).apply();
        mSharedPreferences.edit().putBoolean(Constants.EXTRACT_IMAGES_KEY, mKeyState[17]).apply();
        mSharedPreferences.edit().putBoolean(Constants.PDF_TO_IMAGES_KEY, mKeyState[18]).apply();
        mSharedPreferences.edit().putBoolean(Constants.EXCEL_TO_PDF_KEY, mKeyState[19]).apply();
        mSharedPreferences.edit().putBoolean(Constants.ZIP_TO_PDF_KEY, mKeyState[20]).apply();
    }
}
