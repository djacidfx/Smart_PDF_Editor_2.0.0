package com.templatemela.smartpdfreader.util;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.templatemela.smartpdfreader.R;

public class FavouritesPreferences extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
   @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String name) {
    }
    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        addPreferencesFromResource(R.xml.favourites_preferences);
    }
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
