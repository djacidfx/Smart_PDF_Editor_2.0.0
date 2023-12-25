package com.templatemela.smartpdfreader.providers.fragmentmanagement;

import androidx.fragment.app.Fragment;

interface IFragmentManagement {
    Fragment checkForAppShortcutClicked();

    void favouritesFragmentOption();

    boolean handleBackPressed();

    boolean handleNavigationItemSelected(int i);
}
